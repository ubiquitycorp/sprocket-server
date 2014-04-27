package com.ubiquity.social.repository;

import java.util.List;

import com.niobium.repository.Repository;
import com.ubiquity.social.domain.Event;

/***
 * 
 * Interface exposing CRUD methods for the event entity
 * 
 * @author chris
 *
 */
public interface EventRepository extends Repository <Long, Event> {
	List<Event> findByUserId(Long userId);
	
	/***
	 * List events where the start date lies in between the start and end interval
	 * 
	 * @param userId
	 * @param startInterval
	 * @param endInterval
	 * 
	 * @return
	 */
	List<Event> findByUserIdAndTimeInterval(Long userId, Long startInterval, Long endInterval);
	
    int countAllEventsByOwnerIdAndSocialIdentityProvider(Long ownerId);
}

