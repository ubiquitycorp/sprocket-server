package com.ubiquity.sprocket.analytics.location;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.factory.TestUserFactory;
import com.ubiquity.sprocket.domain.GroupMembership;
import com.ubiquity.sprocket.domain.Location;
import com.ubiquity.sprocket.location.LocationEngine;
import com.ubiquity.sprocket.location.LocationEngineSparkImpl;

public class LocationEngineTest{

	private LocationEngine engine;

	private Logger log = LoggerFactory.getLogger(getClass());
	
	private User john, jack, joe; // LA users
	private User jose, juan; // Argentina users
	private User till; // San Diego user
	
	private Configuration config;

	@Before
	public void setUp() throws Exception {
		config = new PropertiesConfiguration("test.properties");
		engine = new LocationEngineSparkImpl(config);		
	}


	@Test
	public void testMapGroupsUsersIntoCorrectRegion() {
		
		jack = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		john = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		joe = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		jose = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		juan = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		till = TestUserFactory.createTestUserWithMinimumRequiredProperties();

		// add everyone but the sand diego user; this should create 2 clusters, such that the San Diego user will be classified 
		List<Location> loci = Arrays.asList(new Location[] {
				new Location.Builder().latitude(34.0522300).longitude(-118.2436800).user(jack).build(),
				new Location.Builder().latitude(34.1234567).longitude(-118.2412345).user(john).build(),
				new Location.Builder().latitude(34.1334567).longitude(-118.2499999).user(joe).build(),
				new Location.Builder().latitude(-34.6131500 ).longitude(-58.3772300).user(jose).build(),
				new Location.Builder().latitude(-34.7131500 ).longitude(-58.3872300).user(juan).build(),
		});
		engine.updateLocationRecords(loci);
		
		
		// redraws the world
		engine.map();
		
		// Till, from San Diego
		GroupMembership tillMembership = engine.assign(new Location.Builder().latitude(32.715786).longitude(-117.158340).user(till).build());
		GroupMembership jackMembership = engine.assign(new Location.Builder().latitude(34.0522300).longitude(-118.2436800).user(jack).build());
		Assert.assertEquals(tillMembership.getGroupIdentifier(), jackMembership.getGroupIdentifier());

	}


	


}
