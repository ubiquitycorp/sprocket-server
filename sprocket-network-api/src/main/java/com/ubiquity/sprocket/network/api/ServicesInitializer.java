package com.ubiquity.sprocket.network.api;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.redis.JedisConnectionFactory;
import com.ubiquity.sprocket.network.api.cache.CacheFactory;
/***
 * Called on application start-up. Initializes any services or singletons that
 * require special startup
 * 
 * @author chris
 * 
 */
public class ServicesInitializer implements ServletContextListener {

	private Logger log = LoggerFactory.getLogger(getClass());

	/***
	 * Called when container is shut down
	 */
	public void contextDestroyed(ServletContextEvent event) {

	}

	/***
	 * Called on container startup.
	 * 
	 */
	public void contextInitialized(ServletContextEvent event) {
		log.info("Initializing services...");
		// get the properties configuration
		Configuration configuration;
		try {
			configuration = new PropertiesConfiguration(
					"sprocketnetworkapi.properties");

			log.info("{} version: {}",
					configuration.getProperty("application.name"),
					configuration.getProperty("application.version"));

			// start cache services
			JedisConnectionFactory.initialize(configuration);

			// start MQ services
			CacheFactory.initialize(configuration);
			//bootstrap();
		} catch (Exception e) {
			log.error("Unable to initialize dependent services, exiting...", e);
			System.exit(1);
		}
	}
	
}
