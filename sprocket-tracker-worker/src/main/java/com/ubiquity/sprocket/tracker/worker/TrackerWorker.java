package com.ubiquity.sprocket.tracker.worker;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.common.thread.ThreadPool;
import com.niobium.repository.jpa.EntityManagerSupport;
import com.niobium.repository.redis.JedisConnectionFactory;
import com.ubiquity.sprocket.messaging.MessageQueueFactory;
import com.ubiquity.sprocket.service.ServiceFactory;
import com.ubiquity.sprocket.tracker.worker.mq.consumer.TrackConsumer;

public class TrackerWorker {

	private static final int DEFAULT_NUM_CONSUMERS = 10;
	protected static Logger log = LoggerFactory.getLogger(TrackerWorker.class);

	public void destroy() {
		stopServices();
	}
	public void initialize(Configuration configuration, Configuration errorsConfiguration) throws SchedulerException, IOException {

		startServices(configuration, errorsConfiguration);

		log.info("Service initialized.");

		List<TrackConsumer> consumers = new LinkedList<TrackConsumer>();
		try {			
			for(int i = 0; i < DEFAULT_NUM_CONSUMERS; i++)
				consumers.add(new TrackConsumer(MessageQueueFactory.createTrackQueueConsumerChannel()));
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
		final TrackerWorker worker = new TrackerWorker();
		try {
			worker.initialize(new PropertiesConfiguration("trackerworker.properties"),
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
	}

	private void stopServices() {
		JedisConnectionFactory.destroyPool();
		EntityManagerSupport.closeEntityManagerFactory();
		// TODO: we need an mq disconnect
	}

}
