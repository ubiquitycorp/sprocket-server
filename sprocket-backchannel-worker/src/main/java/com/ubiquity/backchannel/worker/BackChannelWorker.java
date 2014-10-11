package com.ubiquity.backchannel.worker;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BackChannelWorker {

	protected static Logger log = LoggerFactory.getLogger(BackChannelWorker.class);

	public abstract void initialize(Configuration configuration) throws IOException;
	
	public abstract void destroy();

	public static void main(String[] args) throws Exception {
		final BackChannelWorker worker = new BackChannelWorkerEjabberdImpl();
		
		try {
			Configuration configuration = new PropertiesConfiguration("backchannelworker.properties");
			worker.initialize(configuration);
			
			log.info("Initialized {} version: {}", configuration.getProperty("application.name"),
					configuration.getProperty("application.version"));
			
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
		log.info("Service initialized");
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				log.warn("Received shutdown signal");
			}
		});
	}


}
