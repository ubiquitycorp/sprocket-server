package com.ubiquity.sprocket.location.worker;

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
import com.ubiquity.sprocket.location.worker.mq.consumer.LocationUpdateConsumer;
import com.ubiquity.sprocket.messaging.MessageQueueFactory;
import com.ubiquity.sprocket.service.ServiceFactory;

public class LocationWorker {

	private static final int DEFAULT_NUM_CONSUMERS = 10;
	protected static Logger log = LoggerFactory.getLogger(LocationWorker.class);

	public void destroy() {
		stopServices();
	}
	
	
	public void initialize(Configuration configuration) throws SchedulerException, IOException {

		startServices(configuration);
		
		List<LocationUpdateConsumer> consumers = new LinkedList<LocationUpdateConsumer>();
		try {			
			for(int i = 0; i < DEFAULT_NUM_CONSUMERS; i++)
				consumers.add(new LocationUpdateConsumer(MessageQueueFactory.createCacheInvalidateConsumerChannel()));
		} catch (IOException e) {
			log.error("Unable to start service", e);
			System.exit(0);
		}

		// start the thread pool, 10 consumer threads
		ThreadPool<LocationUpdateConsumer> threadPool = new ThreadPool<LocationUpdateConsumer>();
		threadPool.start(consumers);
		
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
		final LocationWorker worker = new LocationWorker();
		try {
			worker.initialize(new PropertiesConfiguration("locationworker.properties"));
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

	private void startServices(Configuration configuration) throws IOException {
		ServiceFactory.initialize(configuration, null); 
		MessageQueueFactory.initialize(configuration);
	}

	private void stopServices() {
		EntityManagerSupport.closeEntityManagerFactory();
	}


}
