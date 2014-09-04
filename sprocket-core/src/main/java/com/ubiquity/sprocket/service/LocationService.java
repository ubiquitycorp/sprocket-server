package com.ubiquity.sprocket.service;

import org.apache.commons.configuration.Configuration;

import com.ubiquity.sprocket.domain.Location;
import com.ubiquity.sprocket.repository.LocationRepository;
import com.ubiquity.sprocket.repository.LocationRepositoryJpaImpl;

/***
 * Service for managing location indexing, retrieval, and geo cluster assignments
 * 
 * @author chris
 *
 */
public class LocationService {
	
	private LocationRepository locationRepository;
	
	public LocationService(Configuration configuration) {
		locationRepository = new LocationRepositoryJpaImpl();
	}
	
	/**
	 * Saves location into underlying data store
	 * 
	 * @param location
	 */
	public void updateLocation(Location location) {
		Location persisted = locationRepository.findByUserId(location.getUser().getUserId());
		if(persisted == null)
			locationRepository.create(location);
		else
			locationRepository.update(location);
	}
	
	/***
	 * 
	 * Returns the location if inserted, else it will
	 * 
	 * @param userId
	 * 
	 * @return location or null if it can't be determined
	 ***/
	public Location getLocation(Long userId) {
		return locationRepository.findByUserId(userId);
	}

}
