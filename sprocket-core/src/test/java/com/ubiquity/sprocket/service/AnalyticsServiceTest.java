package com.ubiquity.sprocket.service;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnalyticsServiceTest {

	@SuppressWarnings("unused")
	private static AnalyticsService analyticsService;

	@SuppressWarnings("unused")
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@BeforeClass
	public static void setUp() throws Exception {
		Configuration config = new PropertiesConfiguration("test.properties");
		analyticsService = new AnalyticsService(config);	
	}
	
	@Test
	public void testRecommendActivities() {
		
	}



}
