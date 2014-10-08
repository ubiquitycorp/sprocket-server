package com.ubiquity.sprocket.repository;

import java.util.List;

import com.niobium.repository.Repository;
import com.ubiquity.integration.domain.Activity;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.sprocket.domain.RecommendedActivity;


public interface RecommendedActivityRepository extends Repository <Long, RecommendedActivity>  {
	
	/**
	 * Returns recommended activities
	 * 
	 * @param group
	 * @return List of average engaged activities
	 */
	List<Activity> findRecommendedActivitiesByGroup(String group, ExternalNetwork network);
	
	/**
	 * Returns recommended activities
	 * 
	 * @param group
	 * @return List of average engaged activities
	 */
	List<Activity> findRecommendedActivitiesByGroup(String group);

	/***
	 * Finds all recommended by network
	 * 
	 * @param network
	 */
	List<RecommendedActivity> findAllByExternalNetwork(ExternalNetwork network);


}
