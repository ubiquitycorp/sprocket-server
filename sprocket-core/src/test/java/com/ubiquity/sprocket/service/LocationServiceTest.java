package com.ubiquity.sprocket.service;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.redis.JedisConnectionFactory;
import com.ubiquity.integration.api.PlaceAPIFactory;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.factory.TestPlaceFactory;
import com.ubiquity.location.domain.Place;

public class LocationServiceTest {

	private static LocationService locationService;
	private static Place losAngeles;

	@SuppressWarnings("unused")
	private Logger log = LoggerFactory.getLogger(getClass());

	@BeforeClass
	public static void setUp() throws Exception {
		Configuration config = new PropertiesConfiguration("test.properties");

		locationService = new LocationService(config);

		JedisConnectionFactory.initialize(config);
		ServiceFactory.initialize(config);
		PlaceAPIFactory.initialize(config);

		
		losAngeles = TestPlaceFactory.createLosAngelesAndNeighborhoodsAndBusiness();

		locationService.create(losAngeles);

	}

	

	@Test
	public void testSyncYelpNeighborhood() {
		locationService.syncPlaces(ExternalNetwork.Yelp);
	}

}
