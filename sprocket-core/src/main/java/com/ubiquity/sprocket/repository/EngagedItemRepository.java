package com.ubiquity.sprocket.repository;

import java.util.List;

import com.niobium.repository.Repository;
import com.ubiquity.sprocket.domain.EngagedItem;


public interface EngagedItemRepository extends Repository <Long, EngagedItem>  {
	
	/**
	 * 
	 * @param group
	 * @return
	 */
	List<EngagedItem> findMeanByGroup(String group);

}
