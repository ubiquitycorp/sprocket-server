package com.ubiquity.sprocket.service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.persistence.PersistenceException;

import org.apache.commons.configuration.Configuration;

import com.niobium.repository.jpa.EntityManagerSupport;
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
	
	private UserLocationRepository locationRepository;
	private PlaceRepository placeRepository;
	
	public LocationService(Configuration configuration) {
		locationRepository = new UserLocationRepositoryJpaImpl();
		placeRepository = new PlaceRepositoryJpaImpl();
	}
	
	/**
	 * Saves location into underlying data store
	 * 
	 * @param location
	 */
	public void updateLocation(UserLocation location) {
		UserLocation persisted = locationRepository.findByUserId(location.getUser().getUserId());
		if(persisted == null)
			locationRepository.create(location);
		else
			locationRepository.update(location);
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
	 * @return
	 */
	public Place getClosestPlaceLocationIsWithin(Location location) {
		return null;
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
