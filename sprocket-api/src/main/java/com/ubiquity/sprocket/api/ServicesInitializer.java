package com.ubiquity.sprocket.api;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.niobium.repository.redis.JedisConnectionFactory;
import com.ubiquity.content.api.ContentAPIFactory;
import com.ubiquity.social.api.SocialAPIFactory;
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
		Configuration configuration, errorsConfiguration;
		try {
			configuration = new PropertiesConfiguration(
					"sprocketapi.properties");
			
			errorsConfiguration = new PropertiesConfiguration(
					"messages.properties");
			
			log.info("{} version: {}", configuration.getProperty("application.name"),
					configuration.getProperty("application.version"));

			// start cache services
			JedisConnectionFactory.initialize(configuration);
			
			// start MQ services
			MessageQueueFactory.initialize(configuration);
			
			ServiceFactory.initialize(configuration, errorsConfiguration);
			
			SocialAPIFactory.initialize(configuration);
			ContentAPIFactory.initialize(configuration);

			bootstrap();
		} catch (Exception e) {
			log.error("Unable to initialize dependent services, exiting...", e);
			System.exit(1);
		}
	}

	
	private void bootstrap() {
		// Added popular cities of California to Place table with longitude/latitude pair 
		ServiceFactory.getLocationService().getOrCreatePlaceByName("Eureka, CA");
		ServiceFactory.getLocationService().getOrCreatePlaceByName("Chico, CA");
		ServiceFactory.getLocationService().getOrCreatePlaceByName("Sacramento, CA");
		ServiceFactory.getLocationService().getOrCreatePlaceByName("San Francisco, CA");
		ServiceFactory.getLocationService().getOrCreatePlaceByName("Oakland, CA");
		ServiceFactory.getLocationService().getOrCreatePlaceByName("San Jose, CA");
		ServiceFactory.getLocationService().getOrCreatePlaceByName("Santa Cruz, CA");
		ServiceFactory.getLocationService().getOrCreatePlaceByName("Monterey, CA");
		ServiceFactory.getLocationService().getOrCreatePlaceByName("Fresno, CA");
		ServiceFactory.getLocationService().getOrCreatePlaceByName("Bakersfield, CA");
		ServiceFactory.getLocationService().getOrCreatePlaceByName("San Luis Obispo, CA");
		ServiceFactory.getLocationService().getOrCreatePlaceByName("Santa Barbara, CA");
		ServiceFactory.getLocationService().getOrCreatePlaceByName("San Bernardino, CA");
		ServiceFactory.getLocationService().getOrCreatePlaceByName("Los Angeles, CA");
		ServiceFactory.getLocationService().getOrCreatePlaceByName("Long Beach, CA");
		ServiceFactory.getLocationService().getOrCreatePlaceByName("Palm Springs, CA");
		ServiceFactory.getLocationService().getOrCreatePlaceByName("Irvine, CA");
		ServiceFactory.getLocationService().getOrCreatePlaceByName("San Diego, CA");
		
		ServiceFactory.getLocationService().getOrCreatePlaceByName("Alexandria, Egypt");
	}
}
