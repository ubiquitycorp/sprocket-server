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
import com.ubiquity.integration.api.PlaceAPIFactory;
import com.ubiquity.integration.api.SocialAPIFactory;
import com.ubiquity.sprocket.domain.ConfigurationType;
import com.ubiquity.sprocket.messaging.MessageQueueFactory;
import com.ubiquity.sprocket.repository.ConfigurationRepository;
import com.ubiquity.sprocket.repository.ConfigurationRepositoryJpaImpl;
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

			log.info("{} version: {}",
					configuration.getProperty("application.name"),
					configuration.getProperty("application.version"));

			// start cache services
			JedisConnectionFactory.initialize(configuration);

			// start MQ services
			MessageQueueFactory.initialize(configuration);

			ServiceFactory.initialize(configuration, errorsConfiguration);

			SocialAPIFactory.initialize(configuration);
			ContentAPIFactory.initialize(configuration);
			PlaceAPIFactory.initialize(configuration);
			//bootstrap();
		} catch (Exception e) {
			log.error("Unable to initialize dependent services, exiting...", e);
			System.exit(1);
		}
	}

	@SuppressWarnings("unused")
	private void bootstrap() {
		try
		{
		ConfigurationRepository configRepository = new ConfigurationRepositoryJpaImpl();
		
		com.ubiquity.sprocket.domain.Configuration config1 = new com.ubiquity.sprocket.domain.Configuration.Builder()
				.name("message.service.host").value("localhost").isActive(true)
				.configurationType(ConfigurationType.SERVICE)
				.lastUpdated(System.currentTimeMillis()).build();

		EntityManagerSupport.beginTransaction();
		configRepository.create(config1);
		EntityManagerSupport.commit();

		com.ubiquity.sprocket.domain.Configuration config2 = new com.ubiquity.sprocket.domain.Configuration.Builder()
				.name("message.service.port").value("5222").isActive(true)
				.configurationType(ConfigurationType.SERVICE)
				.lastUpdated(System.currentTimeMillis()).build();

		EntityManagerSupport.beginTransaction();
		configRepository.create(config2);
		EntityManagerSupport.commit();

		com.ubiquity.sprocket.domain.Configuration config3 = new com.ubiquity.sprocket.domain.Configuration.Builder()
				.name("message.service.protocol").value("xmpp").isActive(true)
				.configurationType(ConfigurationType.SERVICE)
				.lastUpdated(System.currentTimeMillis()).build();

		EntityManagerSupport.beginTransaction();
		configRepository.create(config3);
		EntityManagerSupport.commit();

		com.ubiquity.sprocket.domain.Configuration config4 = new com.ubiquity.sprocket.domain.Configuration.Builder()
				.name("http.connnection.timeout").value("5").isActive(true)
				.configurationType(ConfigurationType.RULE)
				.lastUpdated(System.currentTimeMillis()).build();

		EntityManagerSupport.beginTransaction();
		configRepository.create(config4);
		EntityManagerSupport.commit();

		com.ubiquity.sprocket.domain.Configuration config5 = new com.ubiquity.sprocket.domain.Configuration.Builder()
				.name("http.transmission.timeout").value("20").isActive(true)
				.configurationType(ConfigurationType.RULE)
				.lastUpdated(System.currentTimeMillis()).build();

		EntityManagerSupport.beginTransaction();
		configRepository.create(config5);
		EntityManagerSupport.commit();

		com.ubiquity.sprocket.domain.Configuration config6 = new com.ubiquity.sprocket.domain.Configuration.Builder()
				.name("xmpp.connection.timeout").value("5").isActive(true)
				.configurationType(ConfigurationType.RULE)
				.lastUpdated(System.currentTimeMillis()).build();

		EntityManagerSupport.beginTransaction();
		configRepository.create(config6);
		EntityManagerSupport.commit();

		com.ubiquity.sprocket.domain.Configuration config7 = new com.ubiquity.sprocket.domain.Configuration.Builder()
				.name("xmpp.transmission.timeout").value("20").isActive(true)
				.configurationType(ConfigurationType.RULE)
				.lastUpdated(System.currentTimeMillis()).build();

		EntityManagerSupport.beginTransaction();
		configRepository.create(config7);
		EntityManagerSupport.commit();
		} catch (Exception ex){
			log.debug(ex.getMessage());
		}

	}
}
