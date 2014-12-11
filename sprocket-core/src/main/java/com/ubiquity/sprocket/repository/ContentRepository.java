package com.ubiquity.sprocket.repository;

import java.util.List;

import com.niobium.repository.Repository;
import com.ubiquity.sprocket.domain.Content;
import com.ubiquity.sprocket.domain.ContentPK;
import com.ubiquity.sprocket.domain.UserEngagement;

public interface ContentRepository extends Repository <ContentPK, Content>  {
	
	void addUserEngagement(UserEngagement engagement);
	
	Long getEngagementCount(ContentPK contentId);
	
	Long getEngagementCount(ContentPK contentId, String groupMembership);
	
	List<Content> findMostEngagedByGroup(String groupMembership, int n);
	
}
