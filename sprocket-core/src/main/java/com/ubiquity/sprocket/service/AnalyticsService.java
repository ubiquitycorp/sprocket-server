package com.ubiquity.sprocket.service;

import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.content.domain.VideoContent;
import com.ubiquity.content.repository.VideoContentRepository;
import com.ubiquity.content.repository.VideoContentRepositoryJpaImpl;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.identity.domain.User;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.Gender;
import com.ubiquity.social.repository.ActivityRepository;
import com.ubiquity.social.repository.ActivityRepositoryJpaImpl;
import com.ubiquity.sprocket.analytics.recommendation.Dimension;
import com.ubiquity.sprocket.analytics.recommendation.RecommendationEngine;
import com.ubiquity.sprocket.analytics.recommendation.RecommendationEngineSparkImpl;
import com.ubiquity.sprocket.analytics.recommendation.UserMembershipListener;
import com.ubiquity.sprocket.domain.EngagedItem;
import com.ubiquity.sprocket.repository.EngagedItemRepository;
import com.ubiquity.sprocket.repository.EngagedItemRepositoryJpaImpl;

public class AnalyticsService implements UserMembershipListener {
		
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private ActivityRepository activityRepository;
	private VideoContentRepository videoContentRepository;
	private EngagedItemRepository engagedItemRepository;
	private RecommendationEngine recommendationEngine;
	
	/***
	 * Sets up redis cache interfaces for each event type
	 * 
	 * @param configuration
	 */
	public AnalyticsService(Configuration configuration) {
		setUpRecommendationEngine(configuration);

		activityRepository = new ActivityRepositoryJpaImpl();
		videoContentRepository = new VideoContentRepositoryJpaImpl();
		engagedItemRepository = new EngagedItemRepositoryJpaImpl();
	}

	/**
	 * Tracks an engaged item by persisting to the underlying data store
	 * 
	 * @param engagedItem
	 */
	public void track(EngagedItem engagedItem) {
		log.debug("tracking activity {}", engagedItem);
		EntityManagerSupport.beginTransaction();
		engagedItemRepository.create(engagedItem);
		EntityManagerSupport.commit();
	}
	
	/***
	 * Updates a profile record in the data cluster to be used in the recommendation / clustering
	 */
	public void updateProfileRecord(Contact contact) {
		recommendationEngine.updateProfileRecord(contact);
	}
	
	
	/***
	 * Stub recommendation recommends 1 activity that is public
	 * 
	 * @param userId
	 * @return
	 */
	public List<Activity> getRecommendedActivities(Long userId) {
		return activityRepository.findAllWithoutOwner(2);
	}
	
	/***
	 * Stub recommendation recommends 1 video that is public
	 * 
	 * @param userId
	 * @return
	 */
	public List<VideoContent> getRecommendedVideos(Long userId) {
		return videoContentRepository.findAllWithoutOwner(2);
	}
	
	
	/***
	 * Entry point for running the entire cycle of recommendations: Add cases to the search space, classify, and group
	 * 
	 **/
	public void recommend() {

		// for all contacts in the system, add
		recommendationEngine.train();
		
	}
	
	
	private void setUpRecommendationEngine(Configuration configuration) {
		recommendationEngine = new RecommendationEngineSparkImpl(configuration, this);
		recommendationEngine.addDimension(Dimension.createFromEnum("gender", Gender.class));
	}

	

	@Override
	public void didAssignGlobalMembership(User user, String group) {
		log.debug("User {} assigned global membership to {}", user, group);
		
	}

	@Override
	public void didAssignMembershipForExternalNetwork(User user,
			ExternalNetwork network, String group) {
		log.debug("User {} assigned network membership to {}", user, group);
	}
	
	
	
}
