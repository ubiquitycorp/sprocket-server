package com.ubiquity.sprocket.api;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.niobium.repository.redis.JedisConnectionFactory;
import com.ubiquity.integration.api.ContentAPIFactory;
import com.ubiquity.integration.api.SocialAPIFactory;
import com.ubiquity.integration.api.PlaceAPIFactory;
import com.ubiquity.sprocket.messaging.MessageQueueFactory;
import com.ubiquity.sprocket.service.ServiceFactory;

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
					"sprocketapi.properties");
			
			log.info("{} version: {}", configuration.getProperty("application.name"),
					configuration.getProperty("application.version"));

			// start cache services
			JedisConnectionFactory.initialize(configuration);
			
			// start MQ services
			MessageQueueFactory.initialize(configuration);
			
			ServiceFactory.initialize(configuration);
			
			SocialAPIFactory.initialize(configuration);
			ContentAPIFactory.initialize(configuration);
			PlaceAPIFactory.initialize(configuration);
			bootstrap();
		} catch (Exception e) {
			log.error("Unable to initialize dependent services, exiting...", e);
			System.exit(1);
		}
	}

	
	private void bootstrap() {
	
		
		
	}
}
