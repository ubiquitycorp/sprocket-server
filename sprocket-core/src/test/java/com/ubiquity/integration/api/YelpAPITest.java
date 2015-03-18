package com.ubiquity.integration.api;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.identity.domain.Application;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.ExternalNetworkApplication;
import com.ubiquity.integration.domain.ExternalInterest;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.factory.TestPlaceFactory;
import com.ubiquity.location.domain.Place;
import com.ubiquity.sprocket.service.ServiceFactory;

public class YelpAPITest {
	
	private static Logger log = LoggerFactory.getLogger(YelpAPITest.class);
	private static ExternalNetworkApplication externalNetworkApplication ;
	@BeforeClass
	public static void setUp() throws Exception {
		
		Configuration configuration = new PropertiesConfiguration("test.properties");
		PlaceAPIFactory.initialize(configuration);
		ServiceFactory.initialize(configuration, null);
		Application application =  ServiceFactory.getApplicationService().loadApplicationFromConfiguration();
		externalNetworkApplication = ServiceFactory.getApplicationService().getExAppByExternalNetworkAndClientPlatform(application,
				ExternalNetwork.Yelp.ordinal(), ClientPlatform.WEB);
	}
	
	@Test
	public void testSearchPlacesWithinPlace() {
		PlaceAPI placeApi = PlaceAPIFactory.createProvider(ExternalNetwork.Yelp, ClientPlatform.WEB,externalNetworkApplication);
				
		ExternalInterest ex = new ExternalInterest("sushi", null, null);
		Place random = TestPlaceFactory.createLosAngelesAndNeighborhoodsAndBusiness();
		List<Place> places = placeApi.searchPlacesWithinPlace("Restaurants", random.getChildren().iterator().next(), Arrays.asList(new ExternalInterest[] { ex }), 1, 20);
		log.info("places {}", places);
	}

	
	

}
