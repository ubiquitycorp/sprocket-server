package com.ubiquity.sprocket.service;

import org.apache.commons.configuration.Configuration;

import com.ubiquity.sprocket.domain.UserLocation;
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
	
	public LocationService(Configuration configuration) {
		locationRepository = new UserLocationRepositoryJpaImpl();
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
