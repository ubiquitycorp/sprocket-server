package com.ubiquity.sprocket.repository;

import java.util.List;

import com.niobium.repository.Repository;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.sprocket.domain.GroupMembership;


public interface GroupMembershipRepository extends Repository <Long, GroupMembership>  {
	/**
	 * Deletes records by external network
	 *  
	 * @param externalNetwork
	 * @return flag indicating if operation was successful
	 */
	boolean deleteByExternalNetwork(ExternalNetwork externalNetwork);

	/**
	 * Deletes records without an external network defined
	 * 
	 * @return flag indicating if operation was successful
	 */
	boolean deleteWithNoNetwork();
	
	/***
	 * Finds a list of global and network-specific membership by user
	 * 
	 * @param userId
	 * @return list of global and network-specific membership by user
	 * 
	 **/
	List<GroupMembership> findAllByUserId(Long userId);


	
	/***
	 * Deletes any assignments for this user and network
	 * 
	 * @param userId
	 * @param externalNetwork
	 * 
	 * @return
	 */
	boolean deleteByExternalNetworkAndUserId(ExternalNetwork network, Long userId);
	
	/***
	 * Deletes any assignments for this user and network
	 * 
	 * @param userId
	 * 
	 * @return
	 */
	boolean deleteByUserId(Long userId);
	
	
	
	
}
