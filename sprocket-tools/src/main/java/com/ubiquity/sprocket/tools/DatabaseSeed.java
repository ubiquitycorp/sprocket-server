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
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.integration.api.PlaceAPIFactory;
import com.ubiquity.location.domain.Place;
import com.ubiquity.social.api.SocialAPIFactory;
import com.ubiquity.social.domain.ExternalInterest;
import com.ubiquity.social.domain.Interest;
import com.ubiquity.sprocket.messaging.MessageQueueFactory;
import com.ubiquity.sprocket.service.AnalyticsService;
import com.ubiquity.sprocket.service.LocationService;
import com.ubiquity.sprocket.service.ServiceFactory;

public class DatabaseSeed {

	private static Logger log = LoggerFactory.getLogger(DatabaseSeed.class);

	public DatabaseSeed() {
	}

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
		loadYelpMappings(parent, "/yelp_restaurants.txt");

		parent = new Interest("Family", null);
		loadParentInterestWithFile(parent, "/family.txt");

		parent = new Interest("Leisure", null);
		loadParentInterestWithFile(parent, "/leisure.txt");

		parent = new Interest("Games", null);
		loadParentInterestWithFile(parent, "/games.txt");

		parent = new Interest("Connectivity", null);
		loadParentInterestWithFile(parent, "/connectivity.txt");

	}

	private void loadYelpMappings(Interest parent, String string)
			throws IOException {

		AnalyticsService analyticsService = ServiceFactory
				.getAnalyticsService();

		// get restaurants
		Interest restaurants = scanForChildInterestByName(parent, "Restaurants");
		if (restaurants == null)
			throw new IllegalArgumentException(
					"Could not find restaurant interest");

		// now read the lines, creating an external mapping for each yelp
		// category
		List<String> categories = IOUtils.readLines(this.getClass()
				.getResourceAsStream(string), "UTF-8");

		Interest major = null;
		for (String category : categories) {
			if (category.startsWith("\t")) {
				analyticsService.create(new ExternalInterest(category
						.replaceAll("\t", ""), major, ExternalNetwork.Yelp));
			} else {
				major = scanForChildInterestByName(restaurants, category);
				if (major == null) {
					throw new IllegalArgumentException(
							"Could not find internal mapping for:" + category);
				}
			}
		}
	}

	/**
	 * Returns an interest by name in the child list of interests
	 * 
	 * @param parent
	 * @param name
	 * @return
	 */
	private Interest scanForChildInterestByName(Interest parent, String name) {
		Interest found = null;
		for (Interest child : parent.getChildren()) {
			if (child.getName().equals(name)) {
				found = child;
				break;
			}
		}
		return found;
	}

	/***
	 * Loads places from a feed of neighborhoods
	 * 
	 * @throws IOException
	 */
	protected void seedPlacesFromNeighborhoodsFeed() throws IOException {

		LocationService locationService = ServiceFactory.getLocationService();

		List<String> neighborhoods = IOUtils.readLines(this.getClass()
				.getResourceAsStream("/neighborhoods.txt"), "UTF-8");

		// strip
		Place currentCity = null;

		String locator = null;
		for (String neighborhood : neighborhoods) {

			int comma = neighborhood.indexOf(",");
			if (comma > 0) {
				// we have a city; split it
				String city = neighborhood.substring(0, comma);
				String state = neighborhood.substring(comma + 1,
						neighborhood.length()).replaceAll(" ", "");
				log.info("Processing city \"{}\" and state \"{}\"", city, state);

				locator = city + ", " + state;
				try {
					try {
						Thread.sleep(200l);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					currentCity = locationService.getOrCreatePlaceByName(city,
							locator, null, "locality");

				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					log.error("Failed to process \"{}\"", locator);
				}

			} else {
				try {
					if (currentCity == null)
						continue;

					

					try {
						Thread.sleep(200l);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					// if place is not null, get the neighborhood
					String description = neighborhood + ", " + locator;
					Place place = locationService.getOrCreatePlaceByName(
							neighborhood, description, null, new String[] {
									"neighborhood", "locality" });
					if (place == null) {
						log.info(
								"failed to create neighborhood by description {} in {}",
								description, locator);
					} else {
						// if this has no id already, add a proxy
						place.setParent(currentCity);
						log.info("created neighborhood {}", place);
						locationService.updatePlace(place);
					}
				} catch (IllegalArgumentException e) {
					log.error(
							"Failed to process neighborhood \"{}\" with locator {}",
							neighborhood, locator);
				} catch (Exception e) {
					log.error("Unkonwn error: ", e);
				}

			}
		}
	}

	private void startServices(Configuration configuration) throws IOException {
		ServiceFactory.initialize(configuration, null);
		JedisConnectionFactory.initialize(configuration);
		MessageQueueFactory.initialize(configuration);
		SocialAPIFactory.initialize(configuration);
		PlaceAPIFactory.initialize(configuration);
	}

	private void stopServices() {
		JedisConnectionFactory.destroyPool();
		EntityManagerSupport.closeEntityManagerFactory();
	}

	public static void main(String[] args) {
		final DatabaseSeed loader = new DatabaseSeed();
		try {
			loader.initialize(new PropertiesConfiguration("tools.properties"));
			//loader.seedPlacesFromNeighborhoodsFeed();
			//loader.seedInterests();
			loader.goober();
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

	private void goober() {
		LocationService locationService = ServiceFactory.getLocationService();
		locationService.syncPlaces(ExternalNetwork.Yelp);
	}

	private void loadParentInterestWithFile(Interest parent, String resource)
			throws IOException {

		AnalyticsService analyticsService = ServiceFactory
				.getAnalyticsService();

		List<String> lines = IOUtils.readLines(this.getClass()
				.getResourceAsStream(resource), "UTF-8");

		Interest major = null;
		for (String line : lines) {
			if (!line.startsWith("\t")) {
				major = new Interest(line);
				parent.addChild(major);
			} else {
				Interest minor = new Interest(line.replaceAll("\t", ""));
				major.addChild(minor);
			}
		}
		analyticsService.create(parent);
	}

}
