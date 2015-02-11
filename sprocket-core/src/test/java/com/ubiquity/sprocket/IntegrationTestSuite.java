package com.ubiquity.sprocket;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.niobium.repository.redis.JedisConnectionFactory;
import com.ubiquity.integration.api.SocialAPIFactory;
import com.ubiquity.sprocket.service.ContactServiceTest;
import com.ubiquity.sprocket.service.ContentServiceTest;
import com.ubiquity.sprocket.service.ServiceFactory;
import com.ubiquity.sprocket.service.SocialServiceTest;

/***
 * Test suite will ensure that the integration tests have their dependencies met
 * and cleaned up
 * 
 * @author chris
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ ContactServiceTest.class, SocialServiceTest.class,
		ContentServiceTest.class })
public class IntegrationTestSuite {

	@AfterClass
	public static void tearDown() throws Exception {
		// Close the pool for redis and and sql
		JedisConnectionFactory.destroyPool();
		EntityManagerSupport.closeEntityManagerFactory();
	}

	@BeforeClass
	public static void setUpDependencies() throws Exception {
		Configuration configuration = new PropertiesConfiguration(
				"test.properties");
		// Start a connection pool to redis
		JedisConnectionFactory.initialize(configuration);
		//HBaseConnectionFactory.initialize(configuration);
		ServiceFactory.initialize(configuration, null);
		SocialAPIFactory.initialize(configuration);

	}
}
