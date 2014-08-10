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
import com.ubiquity.social.repository.ActivityRepositoryTest;
import com.ubiquity.social.repository.MessageRepositoryTest;
import com.ubiquity.sprocket.repository.EngagedItemRepositoryTest;
import com.ubiquity.sprocket.repository.SocialRepositoryTest;
import com.ubiquity.sprocket.repository.UserRepositoryTest;
import com.ubiquity.sprocket.repository.VideoContentRepositoryTest;
import com.ubiquity.sprocket.service.AnalyticsServiceTest;
import com.ubiquity.sprocket.service.AuthenticationServiceTest;

/***
 * Test suite will ensure that the integration tests have their dependencies met and cleaned up
 * 
 * @author chris
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ 
	AuthenticationServiceTest.class, 
	AnalyticsServiceTest.class, 
	SocialRepositoryTest.class, 
	UserRepositoryTest.class, 
	ActivityRepositoryTest.class, 
	MessageRepositoryTest.class,
	VideoContentRepositoryTest.class,
	EngagedItemRepositoryTest.class
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
	}
}
