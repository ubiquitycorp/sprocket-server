package com.ubiquity.sprocket.repository;

import java.util.List;

import com.niobium.repository.Repository;
import com.ubiquity.sprocket.domain.EngagedDocument;


public interface EngagedDocumentRepository extends Repository <Long, EngagedDocument>  {
	
	/**
	 * 
	 * @param group
	 * @return
	 */
	List<EngagedDocument> findMeanByGroup(String group);

}
