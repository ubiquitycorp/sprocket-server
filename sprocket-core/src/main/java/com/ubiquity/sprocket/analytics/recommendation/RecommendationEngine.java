package com.ubiquity.sprocket.analytics.recommendation;

import com.ubiquity.social.domain.Contact;

public interface RecommendationEngine {
	
	public void updateProfileRecord(Contact contact);
	
	public void addDimension(Dimension dimension);
	
	public void train();
	
	public void classify();

}
