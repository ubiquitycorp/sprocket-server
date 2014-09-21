package com.ubiquity.sprocket.repository;

import java.util.List;
import java.util.Set;

import com.niobium.repository.Repository;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.social.domain.ExternalInterest;

public interface ExternalInterestRepository extends Repository <Long, ExternalInterest>  {
	/**
	 * Retrieves an external mapping by this name
	 * 
	 * @param name
	 * @param network
	 * 
	 * @return mapping from db or null if one does not exist
	 */
	ExternalInterest getByNameAndExternalNetwork(String name, ExternalNetwork network);
	
	/***
	 * Retrieves external mappings by a list of names
	 * 
	 * @param names
	 * @param network
	 * 
	 * @return mappings or an empty list
	 */
	List<ExternalInterest> findByNamesAndExternalNetwork(Set<String> names, ExternalNetwork network);
}

