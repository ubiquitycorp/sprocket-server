package com.ubiquity.sprocket.repository;

import java.util.List;

import com.niobium.repository.Repository;
import com.ubiquity.sprocket.domain.UserLocation;


public interface UserLocationRepository extends Repository <Long, UserLocation> {
	
	/***
	 * Returns user location record or null if one does not exist
	 * 
	 * @param userId
	 * @return
	 */
	UserLocation findByUserId(Long userId);
	
	List<UserLocation> findAll();
}
