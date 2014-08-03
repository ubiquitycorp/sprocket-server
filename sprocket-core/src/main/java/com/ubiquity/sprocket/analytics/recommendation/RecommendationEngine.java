package com.ubiquity.sprocket.analytics.recommendation;

import java.util.List;

import com.ubiquity.content.domain.VideoContent;
import com.ubiquity.identity.domain.User;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.domain.Contact;

public interface RecommendationEngine {
	
	public List<VideoContent> recommendVideoContent(User user);
	
	public List<Activity> recommendActivity(User user);
	
	public void updateProfileRecord(Contact contact);
	
	public void addDimension(Dimension dimension);
	
	public void train();
	
	public void classify();

}
