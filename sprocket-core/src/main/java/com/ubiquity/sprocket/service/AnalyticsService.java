package com.ubiquity.sprocket.service;

import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.content.domain.VideoContent;
import com.ubiquity.content.repository.VideoContentRepository;
import com.ubiquity.content.repository.VideoContentRepositoryJpaImpl;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.repository.ActivityRepository;
import com.ubiquity.social.repository.ActivityRepositoryJpaImpl;
import com.ubiquity.sprocket.domain.EngagedItem;
import com.ubiquity.sprocket.repository.EngagedItemRepository;
import com.ubiquity.sprocket.repository.EngagedItemRepositoryJpaImpl;

public class AnalyticsService {
		
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private ActivityRepository activityRepository;
	private VideoContentRepository videoContentRepository;
	private EngagedItemRepository engagedItemRepository;
	
	/***
	 * Sets up redis cache interfaces for each event type
	 * 
	 * @param configuration
	 */
	public AnalyticsService(Configuration configuration) {
		activityRepository = new ActivityRepositoryJpaImpl();
		videoContentRepository = new VideoContentRepositoryJpaImpl();
		engagedItemRepository = new EngagedItemRepositoryJpaImpl();
	}

	public void track(EngagedItem engagedItem) {
		log.debug("tracking activity {}", engagedItem);
		EntityManagerSupport.beginTransaction();
		engagedItemRepository.create(engagedItem);
		EntityManagerSupport.commit();
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
	
	
	
}
