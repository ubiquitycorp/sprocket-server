package com.ubiquity.sprocket.datasync.worker;

import static org.quartz.DateBuilder.futureDate;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.TriggerKey.triggerKey;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.common.thread.ThreadPool;
import com.niobium.repository.jpa.EntityManagerSupport;
import com.niobium.repository.redis.JedisConnectionFactory;
import com.ubiquity.integration.api.PlaceAPIFactory;
import com.ubiquity.integration.api.SocialAPIFactory;
import com.ubiquity.sprocket.datasync.worker.master.jobs.ContactsSyncJob;
import com.ubiquity.sprocket.datasync.worker.master.jobs.DataSyncJob;
import com.ubiquity.sprocket.datasync.worker.mq.consumer.CacheInvalidateConsumer;
import com.ubiquity.sprocket.messaging.MessageQueueFactory;
import com.ubiquity.sprocket.service.ServiceFactory;

public class DataSyncWorker {

	private static final int DEFAULT_NUM_CONSUMERS = 10;
	protected static Logger log = LoggerFactory.getLogger(DataSyncWorker.class);
	private static WorkerRole role;

	public void destroy() {
		stopServices();
	}

	public void initialize(Configuration configuration,
			Configuration errorsConfiguration) throws IOException,
			SchedulerException {

		startServices(configuration, errorsConfiguration);
		if (role.equals(WorkerRole.MASTER) || role.equals(WorkerRole.UNIVERSAL))
			// start scheduler in the master worker
			startScheduler(configuration.getInt("rules.sync.blockSize", 10),
					configuration.getInt("rules.sync.period", 8),
					configuration.getInt("rules.sync.contacts.blockSize", 10),
					configuration.getInt("rules.sync.contacts.period", 8));
		
		if (role.equals(WorkerRole.SLAVE) || role.equals(WorkerRole.UNIVERSAL)) {
			// creates N threads to consume messages in a slave worker
			List<CacheInvalidateConsumer> consumers = new LinkedList<CacheInvalidateConsumer>();
			try {
				for (int i = 0; i < DEFAULT_NUM_CONSUMERS; i++)
					consumers.add(new CacheInvalidateConsumer(
							MessageQueueFactory
									.createCacheInvalidateConsumerChannel()));
			} catch (IOException e) {
				log.error("Unable to start service", e);
				System.exit(0);
			}

			// start the thread pool, 10 consumer threads
			ThreadPool<CacheInvalidateConsumer> threadPool = new ThreadPool<CacheInvalidateConsumer>();
			threadPool.start(consumers);
		}

		log.info("Initialized {} version: {}"
				, configuration.getProperty("application.name")
				, configuration.getProperty("application.version"));
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				log.error("Main thread interrupted", e);
			}
		}
	}

	public static void main(String[] args) throws SchedulerException {
		final DataSyncWorker worker = new DataSyncWorker();
		try {
			log.info("Initializing " + args[0] + " worker...");
			// args[0] represents is worker is master or slave
			if (args[0].equalsIgnoreCase("master"))
				role = WorkerRole.MASTER;
			else if (args[0].equalsIgnoreCase("slave"))
				role = WorkerRole.SLAVE;
			else
				role = WorkerRole.UNIVERSAL;

			worker.initialize(new PropertiesConfiguration(
					"datasyncworker.properties"), new PropertiesConfiguration(
					"messages.properties"));

		} catch (ConfigurationException e) {
			log.error("Unable to configure service", e);
			System.exit(-1);
		} catch (IOException e) {
			log.error("Unable to connect to dependent service", e);
			System.exit(-1);
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				log.info("Received shutdown signal");
				worker.destroy();
			}
		});
	}

	private void startServices(Configuration configuration,
			Configuration errorsConfiguration) throws IOException {
		ServiceFactory.initialize(configuration, errorsConfiguration);
		JedisConnectionFactory.initialize(configuration);
		MessageQueueFactory.initialize(configuration);
		SocialAPIFactory.initialize(configuration);
		PlaceAPIFactory.initialize(configuration);
	}

	private void stopServices() {
		JedisConnectionFactory.destroyPool();
		EntityManagerSupport.closeEntityManagerFactory();
		// TODO: we need an mq disconnect
	}

	private void startScheduler(int blockSize, int syncPeriod, int contactsBlockSize, int contactsSyncPeriod)
			throws SchedulerException {
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
		scheduler.start();
		//create job for syncing data
		JobDetail job = newJob(DataSyncJob.class)
				.withIdentity("dataSync", "sync")
				.usingJobData("blockSize", blockSize).build();

		Trigger trigger = newTrigger()
				.withIdentity(triggerKey("dataTrigger", "trigger"))
				.withSchedule(
						simpleSchedule().withIntervalInMinutes(syncPeriod)
								.repeatForever())
				.startAt(futureDate(1, IntervalUnit.SECOND)).build();
		
		//create job for syncing contacts
		JobDetail contactsJob = newJob(ContactsSyncJob.class)
				.withIdentity("contactsSync", "sync")
				.usingJobData("blockSize", contactsBlockSize).build();

		Trigger contactsTrigger = newTrigger()
				.withIdentity(triggerKey("contactsTrigger", "trigger"))
				.withSchedule(
						simpleSchedule().withIntervalInMinutes(contactsSyncPeriod)
								.repeatForever())
				.startAt(futureDate(1, IntervalUnit.SECOND)).build();

		scheduler.scheduleJob(job, trigger);
		scheduler.scheduleJob(contactsJob, contactsTrigger);
	}

}
