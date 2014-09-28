package com.ubiquity.sprocket.service;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.redis.JedisConnectionFactory;

public class LocationServiceTest {
	
	
	
	@SuppressWarnings("unused")
	private static LocationService locationService;

	@SuppressWarnings("unused")
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@BeforeClass
	public static void setUp() throws Exception {
		Configuration config = new PropertiesConfiguration("test.properties");
		
		locationService = new LocationService(config);
		
		JedisConnectionFactory.initialize(config);
		ServiceFactory.initialize(config, null);

	}
	
	

}
