package com.ubiquity.sprocket.repository;

import com.niobium.repository.Repository;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.sprocket.domain.ExternalInterest;

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
}

