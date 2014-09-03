package com.ubiquity.sprocket.service;

import java.util.List;

import org.apache.commons.configuration.Configuration;

import com.ubiquity.sprocket.domain.GroupMembership;
import com.ubiquity.sprocket.domain.Location;
import com.ubiquity.sprocket.location.LocationEngine;
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
	private LocationEngine locationEngine;
	
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
	
	/***
	 * Will look up this location's geo cluster via location engine
	 * 
	 * @param location
	 * 
	 * @return GroupAssignment
	 */
	public GroupMembership assign(Location location) {
		return locationEngine.assign(location);
	}

	/***
	 * Assigns users to clusters based on their last known location
	 */
	public void assignGeoClusters() {
		// do a local of all location records and then build the model around it; if we have > 1MM records we can batch these queries
		List<Location> loci = locationRepository.findAll();
		locationEngine.updateLocationRecords(loci);
		locationEngine.map();
		
		// assignments 
	}

}
