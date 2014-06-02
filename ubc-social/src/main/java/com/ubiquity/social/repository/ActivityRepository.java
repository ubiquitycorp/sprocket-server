package com.ubiquity.social.repository;

import java.util.List;

import com.niobium.repository.Repository;
import com.ubiquity.social.domain.Activity;

/***
 * 
 * Interface exposing CRUD methods for the activity entity
 * 
 * @author chris
 *
 */
public interface ActivityRepository extends Repository <Long, Activity> {
	/***
	 * Finds all messages
	 * 
	 * @param ownerId
	 * 
	 * @return
	 */
	List<Activity> findByOwnerId(Long ownerId);

}

