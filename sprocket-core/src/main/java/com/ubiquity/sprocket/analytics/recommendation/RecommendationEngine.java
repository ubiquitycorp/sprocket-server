package com.ubiquity.sprocket.analytics.recommendation;

import java.util.List;

import org.apache.commons.configuration.Configuration;

import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.sprocket.domain.GroupMembership;

public interface RecommendationEngine {
		
	/***
	 * Add a model by the specified context with it's own configuration
	 * 
	 * @param context
	 * @param configuration
	 */
	void addContext(ExternalNetwork context, Configuration configuration);
	
	/***
	 * Update the search space with a profile record
	 * 
	 * @param contact
	 */
	void updateProfileRecords(List<Profile> profiles);
	
	/***
	 * Clears data in memory
	 * 
	 * @param context
	 */
	void clear();
	
	/**
	 * Size of records used in computation
	 *  
	 * @return number of records
	 */
	long size();
	
	/***
	 * Adds a dimension to the global context
	 * 
	 * @param dimension
	 */
	void addDimension(Dimension dimension);
	
	/***
	 * Adds a dimension to a model for the specified context
	 * 
	 * @param dimension
	 * @param context
	 */
	void addDimension(Dimension dimension, ExternalNetwork context);
	
	/***
	 * Updates a dimension for the global context
	 * 
	 * @param dimension
	 */
	void updateDimension(Dimension dimension);
	
	/***
	 * Updates a dimension for the specified context
	 * 
	 * @param dimension
	 */
	void updateDimension(Dimension dimension, ExternalNetwork context);
	
	
	/**
	 * Trains the model for the global context
     *
	 */
	void train();
	
	/**
	 * Trains the model for specified context
     *
	 * @return
	 */
	void train(ExternalNetwork context);
	
	/***
	 * Assigns a profile to the global context
	 * 
	 * @param contacts
	 * 
	 * @return List of group membership entities ready to persist
	 */
	List<GroupMembership> assign(Profile profile);	

	/***
	 * Assigns a profile to a specific context
	 * 
	 * @param context
	 * @param profile
	 * 
	 * @return List of group membership entities ready to persist
	 */
	List<GroupMembership> assign(Profile profile, ExternalNetwork context);	
	
}
