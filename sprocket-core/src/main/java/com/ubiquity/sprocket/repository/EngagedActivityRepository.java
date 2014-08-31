package com.ubiquity.sprocket.repository;

import java.util.List;

import com.niobium.repository.Repository;
import com.ubiquity.sprocket.domain.EngagedActivity;


public interface EngagedActivityRepository extends Repository <Long, EngagedActivity>  {
	
	/**
	 * Returns average engaged activities
	 * 
	 * @param group
	 * @return List of average engaged activities
	 */
	List<EngagedActivity> findMeanByGroup(String group, Integer limit);

}
