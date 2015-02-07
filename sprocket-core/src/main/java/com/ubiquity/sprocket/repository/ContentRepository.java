package com.ubiquity.sprocket.repository;

import java.util.List;

import com.niobium.repository.Repository;
import com.ubiquity.sprocket.domain.Content;
import com.ubiquity.sprocket.domain.ContentPK;
import com.ubiquity.sprocket.domain.UserEngagement;

/**
 * Repository for CRUD operations on a content entity
 * 
 * @author chris
 *
 */
public interface ContentRepository extends Repository <ContentPK, Content>  {
	
	/**
	 * Create an engagement record
	 * 
	 * @param engagement entity containing the content record a user engaged with
	 */
	void addUserEngagement(UserEngagement engagement);
	
	/**
	 * Returns the number of times content has been engaged with
	 * 
	 * @param contentId
	 * 
	 * @return the count
	 */
	Long getEngagementCount(ContentPK contentId);
	
	/**
	 * Returns the number of times content has been engaged with by a particular group
	 * 
	 * @param contentId
	 * @param groupMembership
	 * @return
	 */
	Long getEngagementCount(ContentPK contentId, String groupMembership);
	
	/**
	 * Returns the top n content records a group most engaged with based on the total number of engagements
	 * 
	 * @param groupMembership
	 * @param n
	 * 
	 * @return the top n engaged records
	 */
	List<Content> findMostEngagedByGroup(String groupMembership, int n);
	
}
