package com.ubiquity.sprocket.location;

import java.util.List;

import com.ubiquity.sprocket.domain.GroupMembership;
import com.ubiquity.sprocket.domain.Location;

/**
 * Core engine responsible for creating geo areas based on group proximity
 * 
 * @author chris
 *
 */
public interface LocationEngine {
	
	/***
	 * Updates a user's location
	 * 
	 * @param identifier
	 * @param location
	 */
	void updateLocationRecords(List<Location> location);
	

	/***
	 * Assigns group membership based on the location
	 * 
	 * @param location
	 * 
	 * @return List of group membership entities ready to persist
	 */
	GroupMembership assign(Location location);
	
	/***
	 * Assigns group membership based on the location
	 * 
	 * @param loci
	 * 
	 * @return List of group membership entities ready to persist
	 */
	List<GroupMembership> assign(List<Location> loci);
	
	

	/**
	 * Draws a map based on current records
	 */
	void map();
	
	

}
