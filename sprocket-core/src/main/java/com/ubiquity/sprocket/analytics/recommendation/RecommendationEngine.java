package com.ubiquity.sprocket.analytics.recommendation;

import java.util.List;

import com.ubiquity.social.domain.Contact;

public interface RecommendationEngine {
		
	/***
	 * Update the search space with a contact record
	 * 
	 * @param contact
	 */
	void updateProfileRecord(Contact contact);
	
	/***
	 * Adds a dimension to the global context
	 * 
	 * @param dimension
	 */
	void addDimension(Dimension dimension);
	
	/**
	 * Trains the model, returning the list of unique groups created
     *
	 * @return
	 */
	void train();
	
	/***
	 * Classifies all users
	 */
	void classify();

}
