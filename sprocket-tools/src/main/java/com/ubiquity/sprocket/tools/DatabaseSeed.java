package com.ubiquity.sprocket.tools;

import java.io.IOException;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.niobium.repository.redis.JedisConnectionFactory;
import com.ubiquity.location.domain.Place;
import com.ubiquity.social.api.SocialAPIFactory;
import com.ubiquity.social.domain.Interest;
import com.ubiquity.sprocket.messaging.MessageQueueFactory;
import com.ubiquity.sprocket.service.AnalyticsService;
import com.ubiquity.sprocket.service.LocationService;
import com.ubiquity.sprocket.service.ServiceFactory;

public class DatabaseSeed {

	private static Logger log = LoggerFactory.getLogger(DatabaseSeed.class);

	public DatabaseSeed() {}

	public void initialize(Configuration configuration) throws IOException {
		startServices(configuration);
	}


	protected void seedInterests() throws IOException {

		// entertainment
		Interest parent = new Interest("Entertainment", null);
		loadParentInterestWithFile(parent, "/entertainment.txt");

		parent = new Interest("Sports", null);
		loadParentInterestWithFile(parent, "/sports.txt");

		parent = new Interest("Food", null);
		loadParentInterestWithFile(parent, "/food.txt");
		
		parent = new Interest("Family", null);
		loadParentInterestWithFile(parent, "/family.txt");
		
		parent = new Interest("Leisure", null);
		loadParentInterestWithFile(parent, "/leisure.txt");
		
		parent = new Interest("Games", null);
		loadParentInterestWithFile(parent, "/games.txt");
		
		parent = new Interest("Connectivity", null);
		loadParentInterestWithFile(parent, "/connectivity.txt");
		
		
		
		
		//		// added some external Interest 
		//		analyticsService.create(new ExternalInterest("Music", interestMusic, ExternalNetwork.Twitter));
		//		analyticsService.create(new ExternalInterest("Movies", interestMovies, ExternalNetwork.Twitter));
		//		analyticsService.create(new ExternalInterest("Theater", interestTheater, ExternalNetwork.Twitter));

	}

	/***
	 * Loads places from a feed of neighborhoods
	 * @throws IOException 
	 */
	protected void seedPlacesFromNeighborhoodsFeed() throws IOException {

		LocationService locationService = ServiceFactory.getLocationService();

		int i = 0;
		List<String> neighborhoods = IOUtils.readLines(
				this.getClass().getResourceAsStream("/neighborhoods.txt"),
				"UTF-8"
				);

		// strip 
		Place currentCity = null;
		String city = null;
		String state = null;
		for(String neighborhood : neighborhoods) {

			i++;
			if(i < 2790)
				continue;

			try {
				Thread.sleep(200L);
			} catch (InterruptedException e) {}

			int comma = neighborhood.indexOf(",");
			if(comma > 0) {
				// we have a city; split it
				city = neighborhood.substring(0, comma);
				state = neighborhood.substring(comma + 1, neighborhood.length()).replaceAll(" ", "");

				log.info("Processing city \"{}\" and state \"{}\"", city, state);
				// create the city

				try {
					currentCity = locationService.getOrCreatePlaceByName(city, city + ", " + state, "locality");
					log.info("created city {}", currentCity);


				} catch (IllegalArgumentException e) {
					log.error("Failed to process city \"{}\" and state \"{}\"", city, state);
				}
			} else {


				try {
					if(currentCity == null)
						continue;
					// if place is not null, get the neighborhood
					String description = neighborhood + ", " + city + ", " + state;
					Place place = locationService.getOrCreatePlaceByName(neighborhood, description, "neighborhood");
					if(place == null) {
						log.info("failed to create neighborhood by description {}", description);
					}
					else {
						log.info("created neighborhood {}", place);
					}
				} catch (IllegalArgumentException e) {
					log.error("Failed to process neighborhood \"{}\"", neighborhood);
				}

			}
		}
	}



	private void startServices(Configuration configuration) throws IOException {
		ServiceFactory.initialize(configuration, null);
		JedisConnectionFactory.initialize(configuration);
		MessageQueueFactory.initialize(configuration);
		SocialAPIFactory.initialize(configuration);
	}

	private void stopServices() {
		JedisConnectionFactory.destroyPool();
		EntityManagerSupport.closeEntityManagerFactory();
	}

	public static void main(String[] args) {
		final DatabaseSeed loader = new DatabaseSeed();
		try {
			loader.initialize(new PropertiesConfiguration("tools.properties"));
			loader.seedPlacesFromNeighborhoodsFeed();
			//loader.seedInterests();
		} catch (ConfigurationException e) {
			log.error("Unable to configure service", e);
			System.exit(-1);
		} catch (IOException e) {
			log.error("IO problem running service", e);
			System.exit(-1);
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				log.info("Received shutdown signal");
				loader.stopServices();
			}
		});
	}

	private void loadParentInterestWithFile(Interest parent, String resource) throws IOException {

		AnalyticsService analyticsService = ServiceFactory.getAnalyticsService();

		List<String> lines = IOUtils.readLines(
				this.getClass().getResourceAsStream(resource),
				"UTF-8"
				);

		Interest major = null;
		for(String line : lines) {
			if(!line.startsWith("\t")) {
				major = new Interest(line.replaceAll(" ", ""));
				parent.addChild(major);
			} else {
				Interest minor = new Interest(line.replaceAll("\t", ""));
				major.addChild(minor);
			}
		}
		analyticsService.create(parent);
	}

}
