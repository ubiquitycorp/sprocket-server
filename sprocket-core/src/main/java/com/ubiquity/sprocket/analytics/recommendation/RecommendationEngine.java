package com.ubiquity.sprocket.analytics.recommendation;

import java.util.List;

import org.apache.commons.configuration.Configuration;

import com.ubiquity.social.domain.Contact;
import com.ubiquity.sprocket.domain.GroupMembership;

public interface RecommendationEngine {
		
	/***
	 * Add a model by the specified context with it's own configuration
	 * 
	 * @param context
	 * @param configuration
	 */
	void addContext(String context, Configuration configuration);
	
	/***
	 * Update the search space with a contact record for the global 
	 * 
	 * @param contact
	 */
	void updateProfileRecords(List<Contact> contacts);

	
	/***
	 * Clears data in memory
	 * 
	 * @param context
	 */
	void clear();
	
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
	void addDimension(Dimension dimension, String context);
	
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
	void updateDimension(Dimension dimension, String context);
	
	
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
	void train(String context);
	
	/***
	 * Assigns a list of contacts to their group
	 * 
	 * @param contacts
	 * 
	 * @return List of group membership entities ready to persist
	 */
	List<GroupMembership> assign(List<Contact> contacts);
	
	/***
	 * Assigns a list of contacts to their group
	 * 
	 * @param contacts
	 * 
	 * @return List of group membership entities ready to persist
	 */
	List<GroupMembership> assign(List<Contact> contacts, String context);
	

}
