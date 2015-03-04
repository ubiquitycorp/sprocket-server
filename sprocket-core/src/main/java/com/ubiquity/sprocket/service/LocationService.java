package com.ubiquity.sprocket.service;

import java.io.IOException;
import java.util.List;

import javax.persistence.NoResultException;

import org.apache.commons.configuration.Configuration;
import org.gavaghan.geodesy.DistanceCalculator;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GlobalPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.CollectionVariant;
import com.niobium.repository.cache.DataCacheKeys;
import com.niobium.repository.cache.DataModificationCache;
import com.niobium.repository.cache.DataModificationCacheRedisImpl;
import com.niobium.repository.cache.UserDataModificationCache;
import com.niobium.repository.cache.UserDataModificationCacheRedisImpl;
import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.identity.domain.ExternalNetworkApplication;
import com.ubiquity.integration.api.PlaceAPI;
import com.ubiquity.integration.api.PlaceAPIFactory;
import com.ubiquity.integration.api.exception.ExternalNetworkException;
import com.ubiquity.integration.domain.ExternalInterest;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.repository.ExternalInterestRepository;
import com.ubiquity.integration.repository.ExternalInterestRepositoryJpaImpl;
import com.ubiquity.integration.repository.cache.CacheKeys;
import com.ubiquity.location.LocationConverter;
import com.ubiquity.location.domain.Geobox;
import com.ubiquity.location.domain.Location;
import com.ubiquity.location.domain.Place;
import com.ubiquity.location.domain.UserLocation;
import com.ubiquity.location.repository.PlaceRepository;
import com.ubiquity.location.repository.PlaceRepositoryJpaImpl;
import com.ubiquity.location.repository.UserLocationRepository;
import com.ubiquity.location.repository.UserLocationRepositoryJpaImpl;

/***
 * Service for managing location indexing, retrieval, and geo cluster
 * assignments
 * 
 * @author chris
 * 
 */
public class LocationService {

	private Logger log = LoggerFactory.getLogger(getClass());

	private GeodeticCalculator geoCalculator;
	private DataModificationCache dataModificationCache;
	private UserDataModificationCache userLocationModificationCache;
	
	public LocationService(Configuration configuration) {
		geoCalculator = new GeodeticCalculator();
		
		userLocationModificationCache = new UserDataModificationCacheRedisImpl(
				configuration
						.getInt(DataCacheKeys.Databases.ENDPOINT_MODIFICATION_DATABASE_USER));
		dataModificationCache = new DataModificationCacheRedisImpl(
				configuration
						.getInt(DataCacheKeys.Databases.ENDPOINT_MODIFICATION_DATABASE_GENERAL));
		String key = CacheKeys
				.generateCacheKeyForPlaces(CacheKeys.GlobalProperties.PLACES);
		Long lastModified = dataModificationCache.getLastModified(key, 0L);

		// If there is no cache entry
		if (lastModified == null) {
			if(new PlaceRepositoryJpaImpl().countAllPlaces()>0){
				resetPlaceLastModifiedCache();
			}
		}
	}

	/**
	 * Updates the place record, persisting (or removing) any entries in the child property
	 * 
	 * @param place
	 */
	public void updatePlace(Place place) {
		try {
			place.setLastUpdated(System.currentTimeMillis());
			///place.ensureDefaults(); // needed when updating
			EntityManagerSupport.beginTransaction();
			new PlaceRepositoryJpaImpl().update(place);
			EntityManagerSupport.commit();
		}  finally {
			EntityManagerSupport.closeEntityManager();
		}
	}
	/**
	 * Saves location into underlying data store (or updates it)
	 * 
	 * @param location
	 */
	public void updateLocation(UserLocation location) {
		boolean create = Boolean.FALSE;

		UserLocationRepository locationRepository = null;
		try {
			try {
				locationRepository = new UserLocationRepositoryJpaImpl();
				UserLocation persisted = locationRepository.findByUserId(location
						.getUser().getUserId());
				if (persisted != null)
					location.setLocationId(persisted.getLocationId());

			} catch (NoResultException e) {
				create = Boolean.TRUE;
			} 

			location.setLastUpdated(System.currentTimeMillis());

			EntityManagerSupport.beginTransaction();

			if (create)
				locationRepository.create(location);
			else
				locationRepository.update(location);


			EntityManagerSupport.commit();

		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}

	/***
	 * Returns a place from local db or else attempts to create one from a
	 * geocoder service; note the geocoder service can return multiple locations
	 * for a name (for example Glendale) so currently this should only be used
	 * for major cities with a state parameter. For example, Los Angeles, CA, or
	 * Chicago, IL
	 * 
	 * @param name
	 * @param locator long description of the place, passed to geolocator library to narrow down the list of returned locations
	 * @param granularity (neighborhood, locality) needed to disambiguate input
	 * 
	 * @return A place with a geobox and center lat / lon
	 * 
	 * @throws RuntimeException
	 *             if the geocoder service cannot be accessed
	 * @throws IllegalArgument
	 *             exception if the name is too ambiguous to return a single
	 *             result
	 * 
	 */
	public Place getOrCreatePlaceByName(String name, String locator, String region, ExternalNetwork network, String[] granularity) {
		Place place = null;
		try {
			PlaceRepository placeRepository = new PlaceRepositoryJpaImpl();
			place = placeRepository.getByLocatorAndExternalNetwork(locator, network);
			if(place == null) {
				try {
					List<Geobox> geobox = LocationConverter.getInstance()
							.convertFromLocationDescription(locator, region, granularity);
					if (geobox.isEmpty())
						return null;

					if (geobox.size() > 1)
						throw new IllegalArgumentException(
								"Unable to disambiguate input: " + name);

					Geobox box = geobox.get(0);
					place = new Place.Builder().externalNetwork(null).locator(locator).name(name).boundingBox(box).region(region).lastUpdated(System.currentTimeMillis()).build();

					EntityManagerSupport.beginTransaction();
					placeRepository.create(place);
					EntityManagerSupport.commit();
				} catch (IOException io) {
					throw new RuntimeException(
							"Unable to connect to remote geocode service", io);
				}
			}
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
		return place;
	}
	/***
	 * Returns a place from local db or else attempts to create one from a
	 * geocoder service; note the geocoder service can return multiple locations
	 * for a name (for example Glendale) so currently this should only be used
	 * for major cities with a state parameter. For example, Los Angeles, CA, or
	 * Chicago, IL
	 * 
	 * @param name
	 * @param description long description of the place, passed to geolocator library to narrow down the list of returned locations
	 * @param granularity (neighborhood, locality) needed to disambiguate input
	 * 
	 * @return A place with a geobox and center lat / lon
	 * 
	 * @throws RuntimeException
	 *             if the geocoder service cannot be accessed
	 * @throws IllegalArgument
	 *             exception if the name is too ambiguous to return a single
	 *             result
	 * 
	 */
	public Place getOrCreatePlaceByName(String name, String description, String region, ExternalNetwork network, String granularity) {
		return getOrCreatePlaceByName(name, description, region, network, new String[] { granularity });
	}


	public void syncPlaces(ExternalNetwork network,ExternalNetworkApplication externalNetworkApplication ) {

		if(network.equals(ExternalNetwork.Yelp)) {

			PlaceAPI placeAPI = PlaceAPIFactory.createProvider(ExternalNetwork.Yelp, null, externalNetworkApplication);

			try {

				int processed = 0;
				PlaceRepository placeRepository = new PlaceRepositoryJpaImpl();
				List<Place> places = placeRepository.findLastLevelWithoutNetwork(); // gets all neighborhoods
				for(Place neighborhood : places) {
					
					log.info("Synchronizing neighborhood {}", neighborhood.getName());
					if(neighborhood.getLocator().contains(", CA")) 
						continue;
					
					// check to see if we have something in the db already for this neighborhood....
					if(!placeRepository.findChildrenForPlace(neighborhood).isEmpty()) {
						log.info("Found businsesses for neighborhood {}, skipping", neighborhood);
						continue;
					}
					
					int page = 0;
					Boolean paging = Boolean.TRUE;
					do {
						
						List<Place> results =  null;
						try {
							page++;
							results = placeAPI.searchPlacesWithinPlace("", neighborhood, null, page, 20);
						} catch (ExternalNetworkException e) {
							paging = Boolean.FALSE;
							continue;
						}
						// set paging control
						for(Place business : results) {
							
							// check to see if we have a dupe
							Place persisted = placeRepository.getByLocatorAndExternalNetwork(business.getLocator(), network);
							if(persisted != null) {
								log.info("already have this business {}, skipping persist...", persisted.getName());
								continue;
							}
							
							processed++;
							if(!business.getTags().isEmpty()) {
								// clear out tags in business
								List<ExternalInterest> mappings = new ExternalInterestRepositoryJpaImpl().findByNamesAndExternalNetwork(business.getTags(), network);
								for(ExternalInterest mapping : mappings) {
									// interests hashcode/equals is set to interest id, so dupes won't be added, but this will effectively update the interest collection
									business.getInterests().add(mapping.getInterest());
								}
							}
							
							// update the place with location data that we've derived or augmented
							business.setBoundingBox(null);
							business.setParent(neighborhood);
							business.setLastUpdated(System.currentTimeMillis());
							create(business);
						}
					} while (paging);
				

					log.info("processed {}", processed);

				}

			} finally {
				EntityManagerSupport.closeEntityManager();
			}
		}
	}

	public List<Place> liveSearch(String searchTerm, Long placeID, List<Long> interestIds, ExternalNetwork network, ExternalNetworkApplication externalNetworkApplication)
	{
		if(network.equals(ExternalNetwork.Yelp)) {
			
			PlaceAPI placeAPI = PlaceAPIFactory.createProvider(ExternalNetwork.Yelp, null,externalNetworkApplication);
			PlaceRepository placeRepository = new PlaceRepositoryJpaImpl();
			Place place =  placeRepository.read(placeID);
			ExternalInterestRepository externalRepository = new ExternalInterestRepositoryJpaImpl();
			
			List<ExternalInterest> interests = externalRepository.findByInterestIDsAndExternalNetwork(interestIds,	ExternalNetwork.Yelp);
			return placeAPI.searchPlacesWithinPlace(searchTerm, place, interests, 1, 20);
		}
		return null;
	}

	/**
	 * Returns place with the center point closest to this location
	 * 
	 * @param location
	 * 
	 * @return place or null if there are no places nearby
	 */
	public Place getClosestPlaceLocationIsWithin(Location location) {
		// TODO: set query results caching for this; we don't want to geocode in
		// mysql

		Place closest = null;

		try {

			PlaceRepository placeRepository = new PlaceRepositoryJpaImpl();

			List<Place> places = placeRepository.findAll();

			if (places.isEmpty())
				return null;


			Double closestDistance = Double.MAX_VALUE;
			for (Place place : places) {
				Location center = place.getBoundingBox().getCenter();
				double distance = DistanceCalculator.calculateGeodeticCurve(location, center);
				//Distance between point A and point B
				if (distance < closestDistance) {
					closestDistance = distance;
					closest = place;
				}
			}

		} finally {
			EntityManagerSupport.closeEntityManager();
		}
		return closest;
	}
	/**
	 * Returns place with the center point closest to this location
	 * 
	 * @param location
	 * 
	 * @return place or null if there are no places nearby
	 */
	public Place getClosestNeighborhoodIsWithin(Location location) {
		
		Place closest = null;

		try {

			PlaceRepository placeRepository = new PlaceRepositoryJpaImpl();

			List<Place> places = placeRepository.findLastLevelWithoutNetwork();

			if (places.isEmpty())
				return null;


			Double closestDistance = Double.MAX_VALUE;
			for (Place place : places) {
				// convert to model the geo lib uses
				GlobalPosition locationPoint = new GlobalPosition(location
						.getLatitude().doubleValue(), location.getLongitude()
						.doubleValue(), 0.0);
				Location center = place.getBoundingBox().getCenter();
				GlobalPosition placePoint = new GlobalPosition(center.getLatitude()
						.doubleValue(), center.getLongitude().doubleValue(), 0.0);

				Ellipsoid reference = Ellipsoid.WGS84;
				double distance = geoCalculator.calculateGeodeticCurve(reference,
						locationPoint, placePoint).getEllipsoidalDistance(); // Distance
				// between
				// Point
				// A
				// and
				// Point
				// B
				if (distance < closestDistance) {
					closestDistance = distance;
					closest = place;
				}
			}

		} finally {
			EntityManagerSupport.closeEntityManager();
		}
		return closest;
	}

	/***
	 * 
	 * Returns the location if inserted, else it will
	 * 
	 * @param userId
	 * 
	 * @return location or null if it can't be determined
	 ***/
	public UserLocation getLocation(Long userId) {
		try {
			return new UserLocationRepositoryJpaImpl().findByUserId(userId);
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}

	public void create(Place place) {
		try {
			EntityManagerSupport.beginTransaction();
			new PlaceRepositoryJpaImpl().create(place);
			EntityManagerSupport.commit();
		} finally {
			EntityManagerSupport.closeEntityManager();
		}

	}
	
	public CollectionVariant<Place> getAllCitiesAndNeighborhoods(String region,
			Long ifModifiedSince, Boolean delta) {
		String key = CacheKeys
				.generateCacheKeyForPlaces(CacheKeys.GlobalProperties.PLACES);
		Long lastModified = dataModificationCache.getLastModified(key,
				ifModifiedSince);

		// If there is no cache entry, there is no data
		if (lastModified == null) {
			return null;
		}
		try {
			PlaceRepository placeRepository = new PlaceRepositoryJpaImpl();
			List<Place> places;
			if (delta == null || !delta) {
				places = placeRepository.findLastLevelWithoutNetwork();
			} else {
				places = placeRepository
						.findLastLevelWithoutNetworkWithModifiedSince(ifModifiedSince);
			}
			return new CollectionVariant<Place>(places, lastModified);
			//return new CollectionVariant<Place>(places,null);
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}

	public List<Place> findPlacesByInterestId(Long placeId ,List<Long> interestId,
			ExternalNetwork externalNetwork) {

		try {
			PlaceRepository placeRepository = new PlaceRepositoryJpaImpl();
			return placeRepository.findPlacesByInterestIdAndProvider(placeId ,
					interestId, externalNetwork);
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}

	public void resetPlaceLastModifiedCache() {
		String key = CacheKeys
				.generateCacheKeyForPlaces(CacheKeys.GlobalProperties.PLACES);
		dataModificationCache.setLastModified(key, System.currentTimeMillis());
	}
	/***
	 * find or create place
	 * @param place
	 * @return
	 */
	public Place findOrCreate(Place place) {
		
		PlaceRepositoryJpaImpl placeRepository = new PlaceRepositoryJpaImpl();
		if(place.getPlaceId() != null) {
			return placeRepository.read(place.getPlaceId());
		}else if (place.getParent() != null && place.getParent().getPlaceId() !=null){
			place.setParent(placeRepository.read(place.getParent().getPlaceId()));
			create(place);
		}else
		{
			create(place);
		}
		return place;
	}
	/***
	 * 
	 * @param placeID
	 * @return
	 */
	public Place getPlaceByID(long placeID){
		return new PlaceRepositoryJpaImpl().read(placeID);
	}

	public Boolean addUpdateLocationInCache(Long userId) {
		String key = CacheKeys
				.generateCacheKeyForPlaces(CacheKeys.UserProperties.LOCATION);
		userLocationModificationCache.put(userId, key, System.currentTimeMillis());
		return true;
	}
	
	public Long checkUpdateLocationInProgress(Long userId) {
		String key = CacheKeys
				.generateCacheKeyForPlaces(CacheKeys.UserProperties.LOCATION);
		return userLocationModificationCache.getLastModified(userId, key,null);
		
	}

}
