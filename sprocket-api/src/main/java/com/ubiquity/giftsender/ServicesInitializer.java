package com.ubiquity.giftsender;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.niobium.repository.redis.JedisConnectionFactory;
import com.ubiquity.giftsender.service.ServiceFactory;

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
		JedisConnectionFactory.destroyPool();
		EntityManagerSupport.closeEntityManagerFactory();
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
					"giftsenderapi.properties");
			log.debug("Configuration {}", configuration);

			JedisConnectionFactory.initialize(configuration);

			ServiceFactory.initialize(configuration);

		} catch (Exception e) {
			log.error("Unable to initialize dependent services, exiting...", e);
			System.exit(1);
		}
	}
}
