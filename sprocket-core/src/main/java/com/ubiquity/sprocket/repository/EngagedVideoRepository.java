package com.ubiquity.sprocket.repository;

import java.util.List;

import com.niobium.repository.Repository;
import com.ubiquity.sprocket.domain.EngagedVideo;


public interface EngagedVideoRepository extends Repository <Long, EngagedVideo>  {
	
	/**
	 * 
	 * @param group
	 * @return
	 */
	List<EngagedVideo> findMeanByGroup(String group);

}
