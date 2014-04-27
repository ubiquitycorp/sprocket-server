package com.ubiquity.sprocket.worker.cache;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.niobium.repository.redis.JedisConnectionFactory;
import com.ubiquity.giftsender.service.ServiceFactory;

public class CacheWorker {

	protected static Logger log = LoggerFactory.getLogger(CacheWorker.class);

	public void destroy() {
		stopServices();
	}
	public void initialize(Configuration configuration) throws SchedulerException {

		startServices(configuration);

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
		final CacheWorker worker = new CacheWorker();
		try {
			worker.initialize(new PropertiesConfiguration("cacheworker.properties"));
		} catch (ConfigurationException e) {
			log.error("Unable to configure service", e);
			System.exit(-1);
		} catch (SchedulerException e) {
			log.error("Unable to schedule service", e);
			System.exit(-1);
		}

		log.info("Service initialized.");
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				log.warn("Received shutdown signal");
				worker.destroy();
			}
		});

	}

	private void startServices(Configuration configuration) {
		JedisConnectionFactory.initialize(configuration);
		ServiceFactory.initialize(configuration);
	}

	private void stopServices() {
		JedisConnectionFactory.destroyPool();
		EntityManagerSupport.closeEntityManagerFactory();
	}

}
