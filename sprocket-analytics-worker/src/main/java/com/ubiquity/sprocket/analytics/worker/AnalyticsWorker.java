package com.ubiquity.sprocket.analytics.worker;

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
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.common.thread.ThreadPool;
import com.niobium.repository.jpa.EntityManagerSupport;
import com.niobium.repository.redis.JedisConnectionFactory;
import com.ubiquity.sprocket.analytics.worker.jobs.AssignmentSyncJob;
import com.ubiquity.sprocket.analytics.worker.jobs.RecommendationSyncJob;
import com.ubiquity.sprocket.analytics.worker.mq.consumer.TrackConsumer;
import com.ubiquity.sprocket.messaging.MessageQueueFactory;
import com.ubiquity.sprocket.repository.HBaseConnectionFactory;
import com.ubiquity.sprocket.service.ServiceFactory;


public class AnalyticsWorker {

	private static final int DEFAULT_NUM_CONSUMERS = 2;
	protected static Logger log = LoggerFactory.getLogger(AnalyticsWorker.class);

	public void destroy() {
		stopServices();
	}
	
	public void initialize(Configuration configuration, Configuration errorsConfiguration) throws SchedulerException, IOException {

		startServices(configuration, errorsConfiguration);

		log.info("Service initialized.");

		//startScheduler();
		

		List<TrackConsumer> consumers = new LinkedList<TrackConsumer>();
		try {			
			for(int i = 0; i < DEFAULT_NUM_CONSUMERS; i++)
				consumers.add(new TrackConsumer(MessageQueueFactory.createTrackConsumerChannel()));
		} catch (IOException e) {
			log.error("Unable to start service", e);
			System.exit(0);
		}

		// start the thread pool, 10 consumer threads
		ThreadPool<TrackConsumer> threadPool = new ThreadPool<TrackConsumer>();
		threadPool.start(consumers);
		
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
		final AnalyticsWorker worker = new AnalyticsWorker();
		try {
			worker.initialize(new PropertiesConfiguration("analyticsworker.properties"),
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
				log.warn("Received shutdown signal");
				worker.destroy();
			}
		});

	}

	private void startServices(Configuration configuration, Configuration errorsConfiguration) throws IOException {
		ServiceFactory.initialize(configuration, errorsConfiguration);
		JedisConnectionFactory.initialize(configuration);
		MessageQueueFactory.initialize(configuration);
		HBaseConnectionFactory.initialize(configuration);
	}

	private void stopServices() {
		JedisConnectionFactory.destroyPool();
		EntityManagerSupport.closeEntityManagerFactory();
		HBaseConnectionFactory.close();
		// TODO: we need an mq disconnect
	}
	
	private void startScheduler() throws SchedulerException {
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
		scheduler.start();

		JobDetail job = newJob(RecommendationSyncJob.class)
				.withIdentity("recommendationSync", "sync")
				.build();

		Trigger trigger = newTrigger() 
				.withIdentity(triggerKey("recommendationTrigger", "trigger"))
				.withSchedule(simpleSchedule()
						.withIntervalInHours(1)
						.repeatForever())
						.startAt(futureDate(1, IntervalUnit.MINUTE))
						.build();
	
		scheduler.scheduleJob(job, trigger);
		
		job = newJob(AssignmentSyncJob.class)
				.withIdentity("assignmentSync", "sync")
				.build();

		trigger = newTrigger() 
				.withIdentity(triggerKey("assignmentTrigger", "trigger"))
				.withSchedule(simpleSchedule()
						.withIntervalInMinutes(10)
						.repeatForever())
						.startAt(futureDate(3, IntervalUnit.MINUTE))
						.build();
		
		scheduler.scheduleJob(job, trigger);

	
		
	}

}
