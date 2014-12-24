package com.ubiquity.sprocket.datasync.master.worker;

import static org.quartz.DateBuilder.futureDate;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.TriggerKey.triggerKey;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.niobium.repository.redis.JedisConnectionFactory;
import com.ubiquity.integration.api.PlaceAPIFactory;
import com.ubiquity.integration.api.SocialAPIFactory;
import com.ubiquity.sprocket.datasync.worker.jobs.DataSyncJob;
import com.ubiquity.sprocket.messaging.MessageQueueFactory;
import com.ubiquity.sprocket.service.ServiceFactory;

public class DataSyncWorker {
	protected static Logger log = LoggerFactory.getLogger(DataSyncWorker.class);

	public void destroy() {
		stopServices();
	}
	public void initialize(Configuration configuration, Configuration errorsConfiguration) throws SchedulerException, IOException {

		startServices(configuration, errorsConfiguration);
		
		startScheduler(configuration.getInt("rules.sync.blockSize", 20));
		log.info("Initialized {} version: {}", configuration.getProperty("application.name"),
				configuration.getProperty("application.version"));
		
				
		while (true) {
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {
				log.error("Main thread interrupted", e);
			}
		}
		
		
	}

	public static void main(String[] args) {
		final DataSyncWorker worker = new DataSyncWorker();
		try {
			log.info("Initializing worker...");
			worker.initialize(new PropertiesConfiguration("datasyncworker.properties"),  
					new PropertiesConfiguration("messages.properties"));
		} catch (ConfigurationException e) {
			log.error("Unable to configure service", e);
			System.exit(-1);
		} catch (SchedulerException e) {
			log.error("Unable to schedule service", e);
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

	private void startServices(Configuration configuration, Configuration errorsConfiguration) throws IOException {
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
	
	private void startScheduler(int blockSize) throws SchedulerException {
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
		scheduler.start();

		JobDetail job = newJob(DataSyncJob.class)
				.withIdentity("dataSync", "sync")
				.usingJobData("blockSize", blockSize)
				.build();

		Trigger trigger = newTrigger() 
				.withIdentity(triggerKey("dataTrigger", "trigger"))
				.withSchedule(simpleSchedule()
						.withIntervalInMinutes(8)
						.repeatForever())
						.startAt(futureDate(1, IntervalUnit.SECOND))
						.build();

		scheduler.scheduleJob(job, trigger);
	}

}
