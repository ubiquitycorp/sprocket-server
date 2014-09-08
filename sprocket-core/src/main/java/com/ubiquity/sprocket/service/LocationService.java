package com.ubiquity.sprocket.service;

import java.io.IOException;
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

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.social.api.SocialAPI;
import com.ubiquity.social.api.SocialAPIFactory;
import com.ubiquity.social.api.facebook.FacebookAPI;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.repository.ActivityRepository;
import com.ubiquity.social.repository.ActivityRepositoryJpaImpl;
import com.ubiquity.social.repository.ContactRepository;
import com.ubiquity.social.repository.ContactRepositoryJpaImpl;
import com.ubiquity.sprocket.domain.Geobox;
import com.ubiquity.sprocket.domain.Location;
import com.ubiquity.sprocket.domain.Place;
import com.ubiquity.sprocket.domain.UserLocation;
import com.ubiquity.sprocket.location.LocationConverter;
import com.ubiquity.sprocket.repository.PlaceRepository;
import com.ubiquity.sprocket.repository.PlaceRepositoryJpaImpl;
import com.ubiquity.sprocket.repository.UserLocationRepository;
import com.ubiquity.sprocket.repository.UserLocationRepositoryJpaImpl;

/***
 * Service for managing location indexing, retrieval, and geo cluster assignments
 * 
 * @author chris
 *
 */
public class LocationService {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	private GeodeticCalculator geoCalculator;
	private UserLocationRepository locationRepository;
	private PlaceRepository placeRepository;
	private ContactRepository contactRepository;
	private ActivityRepository activityRepository;
	
	public LocationService(Configuration configuration) {
		locationRepository = new UserLocationRepositoryJpaImpl();
		placeRepository = new PlaceRepositoryJpaImpl();
		geoCalculator = new GeodeticCalculator();
		contactRepository = new ContactRepositoryJpaImpl();
		activityRepository = new ActivityRepositoryJpaImpl();
	}

	/**
	 * Saves location into underlying data store (or updates it)
	 * 
	 * @param location
	 */
	public void updateLocation(UserLocation location) {
		boolean create = Boolean.FALSE;
		try {
			UserLocation persisted = locationRepository.findByUserId(location.getUser().getUserId());
			if(persisted != null)
				location.setLocationId(persisted.getLocationId());
				
		} catch (NoResultException e) {
			create = Boolean.TRUE;
		}
		location.setLastUpdated(System.currentTimeMillis());
		EntityManagerSupport.beginTransaction();
		if(create)
			locationRepository.create(location);
		else
			locationRepository.update(location);
		EntityManagerSupport.commit();
	}
	
	
	public List<Activity> syncLocalNewsFeed(ExternalIdentity identity, ExternalNetwork network) {
		SocialAPI socialApi = SocialAPIFactory.createProvider(network, null);
		List<Activity> activitiesList = null;
		if(network.equals(ExternalNetwork.Facebook)){
			UserLocation userLocation = locationRepository.findByUserId(identity.getUser().getUserId());
			Place place = userLocation.getNearestPlace();
			Double latitude = place.getBoundingBox().getCenter().getLatitude().doubleValue();
			Double longtude = place.getBoundingBox().getCenter().getLongitude().doubleValue();
			Integer distance = getDistanceBetweenTwoPoints(place.getBoundingBox().getCenter(),
					place.getBoundingBox().getUpperRight()).intValue();
					
			activitiesList = ((FacebookAPI) socialApi).getLocalPagesFeeds(null, identity, latitude, longtude, distance, 1, 50);
				
		}
		// the owner is this identitie's user
		Long ownerId = identity.getUser().getUserId();

		// Keep track of processed ids
		List<Long> processedIds = new LinkedList<Long>();

		for (Activity activity : activitiesList) {

			loadPostedByContactIntoActivity(activity);
			try {
				
				// persist it or update it if it exists already
				findOrCreate(activity);
				processedIds.add(activity.getActivityId());
			} catch (Exception e) {
				log.warn("Could not process activity, skipping", e);
			}
		}

		if (!processedIds.isEmpty()) {
			try{
				// now remove old activities
				EntityManagerSupport.beginTransaction();
				activityRepository.deleteWithoutIds(ownerId, processedIds, network);
				EntityManagerSupport.commit();
			} finally {
				EntityManagerSupport.closeEntityManager();
			}
			// update data modification cache
			/*String key = CacheKeys.generateCacheKeyForExternalNetwork(CacheKeys.UserProperties.ACTIVITIES, network);
			dataModificationCache.put(ownerId, key, System.currentTimeMillis());*/
		}
		return activitiesList;
	}
	
	
	private void loadPostedByContactIntoActivity(Activity activity) {
		// search if contact already exsists in DB
		Contact postedByContact = contactRepository
				.findBySocialNetworkAndSocialIdentitfier(activity.getPostedBy()
						.getExternalIdentity().getIdentifier(),
						activity.getExternalNetwork());
		if (postedByContact != null) {
			// set contact Id and External ID
			activity.setPostedBy(postedByContact);
		}
	}
	
	/***
	 * Creates or updates an activity based on the activity id.
	 * 
	 * @param activity
	 */
	public Activity findOrCreate(Activity activity) {

		if (activity.getActivityId() != null) {
			return activityRepository.read(activity.getActivityId());
		}

		Activity persisted;
		// read first, determining public or private based on ownership
		User owner = activity.getOwner();
		if (owner == null) {
			persisted = activityRepository.getByExternalIdentifierAndNetwork(
					activity.getExternalIdentifier(),
					activity.getExternalNetwork());
		} else {
			persisted = activityRepository
					.getByExternalIdentifierAndSocialNetwork(
							activity.getExternalIdentifier(),
							owner.getUserId(), activity.getExternalNetwork());
		}
		if (persisted == null) {
			create(activity);
			return activity;
		} else {
			return persisted;
		}

	}
	
	/***
	 * Persists a activity
	 * 
	 * @param Activity
	 */
	private void create(Activity activity) {
		try {
			EntityManagerSupport.beginTransaction();
			activityRepository.create(activity);
			EntityManagerSupport.commit();
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}

	/***
	 * Returns a place from local db or else attempts to create one from a geocoder service; note the geocoder 
	 * service can return multiple locations for a name (for example Glendale) so currently this should only be used for major cities
	 * with a state parameter. For example, Los Angeles, CA, or Chicago, IL
	 * 
	 * @param name
	 * @return A place with a geobox and center lat / lon
	 * @throws RuntimeException if the geocoder service cannot be accessed
	 * @throws IllegalArgument exception if the name is too ambiguous to return a single result
	 * 
	 */
	public Place getOrCreatePlaceByName(String name) {
		try {
			return placeRepository.findByName(name, Locale.US);
		} catch (PersistenceException e) {
			try {
				List<Geobox> geobox = LocationConverter.getInstance().convertFromLocationDescription(name, "en");
				if(geobox.isEmpty())
					return null;
				if(geobox.size() > 1)
					throw new IllegalArgumentException("Unable to disambiguate input: " + name);

				Geobox box = geobox.get(0);
				Place place = new Place.Builder().name(name).boundingBox(box).locale(Locale.US).build();
				EntityManagerSupport.beginTransaction();
				placeRepository.create(place);
				EntityManagerSupport.commit();
			} catch (IOException io) {
				throw new RuntimeException("Unable to connect to remote geocode service");
			}
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
		// TODO: set query results caching for this; we don't want to geocode in mysql
		List<Place> places = placeRepository.findAll();
		
		if(places.isEmpty())
			return null;
		
		Place closest = null;
		Double closestDistance = Double.MAX_VALUE;
		for(Place place : places) {
			// convert to model the geo lib uses
			GlobalPosition locationPoint = new GlobalPosition(location.getLatitude().doubleValue(), location.getLongitude().doubleValue(), 0.0); 
			Location center = place.getBoundingBox().getCenter();
			GlobalPosition placePoint = new GlobalPosition(center.getLatitude().doubleValue(), center.getLongitude().doubleValue(), 0.0);

			Ellipsoid reference = Ellipsoid.WGS84;  
			double distance = geoCalculator.calculateGeodeticCurve(reference, locationPoint, placePoint).getEllipsoidalDistance(); // Distance between Point A and Point B
			if(distance < closestDistance) {
				closestDistance = distance;
				closest = place;
			}
		}
		return closest;
	}
	
	
	public Double getDistanceBetweenTwoPoints(Location point1, Location point2){
		return Math.sqrt(Math.abs(point1.getLatitude().doubleValue() - point2.getLatitude().doubleValue())
				+ Math.abs(point1.getLongitude().doubleValue() - point2.getLongitude().doubleValue()));
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
		return locationRepository.findByUserId(userId);
	}

}
