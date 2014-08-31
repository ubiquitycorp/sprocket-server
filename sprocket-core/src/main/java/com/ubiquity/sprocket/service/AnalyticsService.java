package com.ubiquity.sprocket.service;

import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.content.domain.VideoContent;
import com.ubiquity.content.repository.VideoContentRepository;
import com.ubiquity.content.repository.VideoContentRepositoryJpaImpl;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.identity.domain.User;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.repository.ActivityRepository;
import com.ubiquity.social.repository.ActivityRepositoryJpaImpl;

public class AnalyticsService {
		
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private ActivityRepository activityRepository;
	private VideoContentRepository videoContentRepository;
	
	/***
	 * Sets up redis cache interfaces for each event type
	 * 
	 * @param configuration
	 */
	public AnalyticsService(Configuration configuration) {
		activityRepository = new ActivityRepositoryJpaImpl();
		videoContentRepository = new VideoContentRepositoryJpaImpl();

	}

	public void track(User user, Activity activity) {
		log.debug("tracking activity {}", activity);
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
	 * Stub recommendation recommends top 20 public activities in this provider
	 * 
	 * @param userId
	 * @return
	 */
	public List<Activity> getRecommendedActivities(Long userId, ExternalNetwork externalNetwork) {
		return activityRepository.findAllWithoutOwnerBySocialNetwork(20, externalNetwork);
	}
	
	/***
	 * Stub recommendation recommends top 20 public videos in this provider
	 * 
	 * @param userId
	 * @return
	 */
	public List<VideoContent> getRecommendedVideos(Long userId, ExternalNetwork externalNetwork) {
		return videoContentRepository.findAllWithoutOwnerByContentNetwork(20, externalNetwork);
	}
	
	
	
}
