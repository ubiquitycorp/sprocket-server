package com.ubiquity.sprocket.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;

import org.apache.commons.configuration.Configuration;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GlobalPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.CollectionVariant;
import com.niobium.repository.cache.DataCacheKeys;
import com.niobium.repository.cache.DataModificationCache;
import com.niobium.repository.cache.DataModificationCacheRedisImpl;
import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.external.repository.ExternalInterestRepository;
import com.ubiquity.external.repository.ExternalInterestRepositoryJpaImpl;
import com.ubiquity.external.repository.cache.CacheKeys;
import com.ubiquity.integration.api.PlaceAPI;
import com.ubiquity.integration.api.PlaceAPIFactory;
import com.ubiquity.location.LocationConverter;
import com.ubiquity.location.domain.Geobox;
import com.ubiquity.location.domain.Location;
import com.ubiquity.location.domain.Place;
import com.ubiquity.location.domain.UserLocation;
import com.ubiquity.location.repository.PlaceRepository;
import com.ubiquity.location.repository.PlaceRepositoryJpaImpl;
import com.ubiquity.location.repository.UserLocationRepository;
import com.ubiquity.location.repository.UserLocationRepositoryJpaImpl;
import com.ubiquity.social.domain.ExternalInterest;

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
	
	public LocationService(Configuration configuration) {
		geoCalculator = new GeodeticCalculator();
		dataModificationCache = new DataModificationCacheRedisImpl(
				configuration
						.getInt(DataCacheKeys.Databases.ENDPOINT_MODIFICATION_DATABASE_GENERAL));
	}

	/**
	 * Updates the place record, persisting (or removing) any entries in the child property
	 * 
	 * @param place
	 */
	public void updatePlace(Place place) {
		try {
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
	public Place getOrCreatePlaceByName(String name, String description, ExternalNetwork network, String[] granularity) {
		Place place = null;
		try {
			PlaceRepository placeRepository = new PlaceRepositoryJpaImpl();
			try {
				return placeRepository.findByName(name, network, "us");
			} catch (PersistenceException e) {
				try {
					List<Geobox> geobox = LocationConverter.getInstance()
							.convertFromLocationDescription(description, "en", granularity);
					if (geobox.isEmpty())
						return null;

					if (geobox.size() > 1)
						throw new IllegalArgumentException(
								"Unable to disambiguate input: " + name);

					Geobox box = geobox.get(0);
					place = new Place.Builder().name(name).boundingBox(box).region("us").lastUpdated(System.currentTimeMillis()).build();
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
	public Place getOrCreatePlaceByName(String name, String description, ExternalNetwork network, String granularity) {
		return getOrCreatePlaceByName(name, description, network, new String[] { granularity });
	}


	public void syncPlaces(ExternalNetwork network) {

		if(network.equals(ExternalNetwork.Yelp)) {

			PlaceAPI placeAPI = PlaceAPIFactory.createProvider(ExternalNetwork.Yelp, null);
			
			try {
				
				// get external mappings for yelp
				List<ExternalInterest> externalInterests = new ExternalInterestRepositoryJpaImpl().findByExternalNetwork(ExternalNetwork.Yelp);
				
				PlaceRepository placeRepository = new PlaceRepositoryJpaImpl();
				List<Place> places = placeRepository.findWithNoChildren(Locale.US);
				
				// go through these and for each neighborhood, execute a query..., and then also for the parent, but only once.
				for(Place place : places) {
					log.info("looking for yelp stuff in {}", place);
					
					// for each external interest for Yelp, do a search
					for(ExternalInterest externalInterest : externalInterests) {
						log.info("looking for {} stuff", externalInterest.getName());
						List<Place> businesses = placeAPI.searchPlacesWithinPlace("", place, Arrays.asList(new ExternalInterest[] { externalInterest }), 20);
						log.info("places found {}", businesses);
						for(Place business : businesses) {
							Place persisted = placeRepository.getByExernalIdentifierAndNetwork(place.getExternalIdentitifer(), network);
							if(persisted == null) {
								// getting geo info for place
								
								try {
									List<Geobox> boxes = LocationConverter.getInstance().convertFromAddress(business.getAddress(), "en", null);
									if(boxes.size() == 1) {
										business.setBoundingBox(boxes.get(0));
										business.setParent(place);
									} else {
										log.error("Unable to get a specific location for place", business);
										continue;
									}
								} catch (Exception e) {
									log.error("Unable to get coordinates for place", business);
									continue;
								}
								
								log.info("creating place {}", business);
								create(business);
							} else {
								log.info("We already have business {}, skipping...", business);
							}
						}
						
						
						// save only if it does not exist, else update
						
					}
					// find by external interest, provider
					// loop through thouse
					
					// find all external interest by category
					// loop through
					// do a yelp search as seen below
					
					//List<Place> businesses = placeAPI.searchPlacesWithinPlace("", place, null, 5); // search it all in culver city
					//log.info("businessess {}", businesses);
				}
				
			} finally {
				EntityManagerSupport.closeEntityManager();
			}
		}
	}

	public List<Place> liveSearch(String searchTerm,Long placeID, List<Long> interestIds, ExternalNetwork network)
	{
		if(network.equals(ExternalNetwork.Yelp)) {
			
			PlaceAPI placeAPI = PlaceAPIFactory.createProvider(ExternalNetwork.Yelp, null);
			PlaceRepository placeRepository = new PlaceRepositoryJpaImpl();
			Place place =  placeRepository.read(placeID);
			ExternalInterestRepository externalRepository = new ExternalInterestRepositoryJpaImpl();
			
			List<ExternalInterest> interests = externalRepository.findByInterestIDsAndExternalNetwork(interestIds,	ExternalNetwork.Yelp);
			return placeAPI.searchPlacesWithinPlace(searchTerm, place, interests, 25);
		}
		return null;
	}

		PlaceAPI placeAPI = PlaceAPIFactory.createProvider(ExternalNetwork.Yelp, null);
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
	/**
	 * Returns place with the center point closest to this location
	 * 
	 * @param location
	 * 
	 * @return place or null if there are no places nearby
	 */
	public Place getClosestNeighborhoodIsWithin(Location location) {
		// TODO: set query results caching for this; we don't want to geocode in
		// mysql

		Place closest = null;

		try {

			PlaceRepository placeRepository = new PlaceRepositoryJpaImpl();

			List<Place> places = placeRepository.getAllNeighborhoods();

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
	
	public CollectionVariant<Place> getAllCitiesAndNeighborhoods(Locale locale,
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
				places = placeRepository.getAllCitiesAndNeighborhoods();
			} else {
				places = placeRepository
						.getAllCitiesAndNeighborhoodsWithModifiedSince(ifModifiedSince);
			}
			return new CollectionVariant<Place>(places, lastModified);
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
		}else{
			create(place);
		}
		return place;
	}

}
