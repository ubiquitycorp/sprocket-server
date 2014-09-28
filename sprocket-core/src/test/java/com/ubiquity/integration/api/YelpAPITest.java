package com.ubiquity.integration.api;

import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.integration.factory.TestPlaceFactory;
import com.ubiquity.location.domain.Place;

public class YelpAPITest {
	
	private static Logger log = LoggerFactory.getLogger(YelpAPITest.class);
		
	@BeforeClass
	public static void setUp() throws Exception {
		
		Configuration configuration = new PropertiesConfiguration("test.properties");
		PlaceAPIFactory.initialize(configuration);
		
	}
	
	@Test
	public void testSearchPlacesWithinPlace() {
		PlaceAPI placeApi = PlaceAPIFactory.createProvider(ExternalNetwork.Yelp, ClientPlatform.WEB);
		
		Place random = TestPlaceFactory.createLosAngelesWithMininumRequiredProperties();
		List<Place> places = placeApi.searchPlacesWithinPlace("Peruvian", random, null, null);
		log.info("places {}", places);
	
	}

	
	

}
