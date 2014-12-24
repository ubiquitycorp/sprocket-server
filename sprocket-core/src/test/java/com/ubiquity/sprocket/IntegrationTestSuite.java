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
import com.ubiquity.sprocket.repository.HBaseConnectionFactory;

/***
 * Test suite will ensure that the integration tests have their dependencies met and cleaned up
 * 
 * @author chris
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ 
	
	})
public class IntegrationTestSuite {
	
	@AfterClass
	public static void tearDown() throws Exception {
		// Close the pool for redis and and sql
		JedisConnectionFactory.destroyPool();
		EntityManagerSupport.closeEntityManagerFactory();
	}
	
	@BeforeClass
	public static void setUpDependencies() throws Exception {
		Configuration configuration = new PropertiesConfiguration("test.properties");
		// Start a connection pool to redis
		JedisConnectionFactory.initialize(configuration);
		HBaseConnectionFactory.initialize(configuration);;

	}
}
