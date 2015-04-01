package com.ubiquity.sprocket.service;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.integration.domain.AgeRange;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.Gender;
import com.ubiquity.integration.factory.TestActivityFactory;
import com.ubiquity.sprocket.analytics.recommendation.factory.TestProfileFactory;
import com.ubiquity.sprocket.domain.Content;
import com.ubiquity.sprocket.domain.Profile;
import com.ubiquity.sprocket.domain.factory.ContentFactory;

public class AnalyticsServiceTest {

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
		Profile jack = TestProfileFactory.createProfileAndIdentity(Math.abs(new java.util.Random().nextLong()),
				ExternalNetwork.Facebook, Gender.Male, new AgeRange(21, 35), 34.0522300, -118.2436800);
		Profile john = TestProfileFactory.createProfileAndIdentity(Math.abs(new java.util.Random().nextLong()),
				ExternalNetwork.Facebook, Gender.Male, new AgeRange(55, 65), 34.1234567, -118.2412345);
		Profile joe = TestProfileFactory.createProfileAndIdentity(Math.abs(new java.util.Random().nextLong()),
				ExternalNetwork.Facebook, Gender.Male, new AgeRange(55, 65), 34.1334567, -118.2499999);
		Profile jill = TestProfileFactory.createProfileAndIdentity(Math.abs(new java.util.Random().nextLong()),
				ExternalNetwork.Facebook, Gender.Female, new AgeRange(55, 65), -34.6131500, -58.3772300);
		Profile jane = TestProfileFactory.createProfileAndIdentity(Math.abs(new java.util.Random().nextLong()),
				ExternalNetwork.Facebook, Gender.Female, new AgeRange(55, 65), -34.6131500, -58.3772300);
		
		// persist all of these records
		analyticsService.createProfile(jack);
		analyticsService.createProfile(john);
		analyticsService.createProfile(joe);
		analyticsService.createProfile(jill);
		analyticsService.createProfile(jane);

		
		// create some public content and track it
		Content content = ContentFactory.createContent(TestActivityFactory.createActivityWithMininumRequirements(null, ExternalNetwork.Facebook));
		analyticsService.track(content, jack.getUserId(), System.currentTimeMillis(), null);
		analyticsService.track(content, john.getUserId(), System.currentTimeMillis(), null);
		

	}

}
