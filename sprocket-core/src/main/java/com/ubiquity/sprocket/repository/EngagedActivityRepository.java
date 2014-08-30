package com.ubiquity.sprocket.repository;

import java.util.List;

import com.niobium.repository.Repository;
import com.ubiquity.sprocket.domain.EngagedActivity;


public interface EngagedActivityRepository extends Repository <Long, EngagedActivity>  {
	
	/**
	 * 
	 * @param group
	 * @return
	 */
	List<EngagedActivity> findMeanByGroup(String group);

}
