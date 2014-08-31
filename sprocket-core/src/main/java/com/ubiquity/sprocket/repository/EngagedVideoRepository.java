package com.ubiquity.sprocket.repository;

import java.util.List;

import com.niobium.repository.Repository;
import com.ubiquity.sprocket.domain.EngagedVideo;


public interface EngagedVideoRepository extends Repository <Long, EngagedVideo>  {
	
	/**
	 * Finds the average engaged video by group name
	 * @param group
	 * 
	 * @return Averaged engaged videos
	 */
	List<EngagedVideo> findMeanByGroup(String group, Integer limit);

	
	

}
