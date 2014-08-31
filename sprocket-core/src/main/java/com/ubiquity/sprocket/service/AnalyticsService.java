package com.ubiquity.sprocket.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.ubiquity.sprocket.domain.EngagedActivity;
import com.ubiquity.sprocket.domain.EngagedItem;
import com.ubiquity.sprocket.domain.EngagedVideo;
import com.ubiquity.sprocket.domain.GroupMembership;
import com.ubiquity.sprocket.repository.EngagedActivityRepository;
import com.ubiquity.sprocket.repository.EngagedActivityRepositoryJpaImpl;
import com.ubiquity.sprocket.repository.EngagedItemRepository;
import com.ubiquity.sprocket.repository.EngagedItemRepositoryJpaImpl;
import com.ubiquity.sprocket.repository.EngagedVideoRepository;
import com.ubiquity.sprocket.repository.EngagedVideoRepositoryJpaImpl;
import com.ubiquity.sprocket.repository.GroupMembershipRepository;

public class AnalyticsService implements UserMembershipListener {

	private Logger log = LoggerFactory.getLogger(getClass());

	private ActivityRepository activityRepository;
	private VideoContentRepository videoContentRepository;
	private EngagedItemRepository engagedItemRepository;
	private EngagedActivityRepository engagedActivityRepository;
	private EngagedVideoRepository engagedVideoRepository;
	private RecommendationEngine globalRecommendationEngine;
	private GroupMembershipRepository groupMembershipRepository;
	
	// cache should be in Redis ..?
	private Map<String, List<Activity>> groupActivityRecommendationMap = new HashMap<String, List<Activity>>();
	private Map<String, List<VideoContent>> groupVideoRecommendationMap = new HashMap<String, List<VideoContent>>();

	// creates an enginer (and a search space for each network
	private Map<ExternalNetwork, RecommendationEngine> networkEngineMap = new HashMap<ExternalNetwork, RecommendationEngine>();

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
		engagedActivityRepository = new EngagedActivityRepositoryJpaImpl();
		engagedVideoRepository = new EngagedVideoRepositoryJpaImpl();

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
	 * Updates a profile record in the data cluster to be used in the recommendation / clustering across social networks
	 */
	public void updateGlobalProfileRecord(Contact contact) {
		globalRecommendationEngine.updateProfileRecord(contact);
	}

	/***
	 * Updates a profile record in the data cluster to be used in the recommendation / clustering
	 */
	public void updateProfileRecordForExternalNetwork(Contact contact) {
		// get network specific engine and update the profile
		RecommendationEngine engine = getRecommendationEngine(contact);
		engine.updateProfileRecord(contact);
	}



	/***
	 * Stub recommendation recommends 1 activity that is public
	 * 
	 * @param userId
	 * @return
	 */
	public List<Activity> findRecommendedActivities(Long userId) {
		return activityRepository.findAllWithoutOwner(2);
	}

	/**
	 * Find the median engaged activities for the specified network
	 * 
	 * @param contact
	 * 
	 * @return List of activity average engaged entities sorted by most viewed
	 */
	public List<Activity> findRecommendedActivities(ExternalNetwork network, Long userId) {
		
		GroupMembership membership = getGroupMembershipByExternalNetwork(network, userId);
		if(membership == null)
			return new LinkedList<Activity>();

		
		return groupActivityRecommendationMap.get(membership.getGroupIdentifier());
		
	}
	
	/**
	 * Gets group membership for this network
	 * 
	 * @param network
	 * @param userId
	 * @return
	 */
	private GroupMembership getGroupMembershipByExternalNetwork(ExternalNetwork network, Long userId) {
		List<GroupMembership> memberships = groupMembershipRepository.findAllByUserId(userId);
		for(GroupMembership membership : memberships) {
			if(membership.getExternalNetwork() == null)
				continue; // skip global
			if(membership.getExternalNetwork() == network) {
				return membership;
			}
		}
		return null;
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
//		globalRecommendationEngine.train();
		
		Set<ExternalNetwork> supportedNetworks = networkEngineMap.keySet();
		for(ExternalNetwork network : supportedNetworks) {
			// clear all membership
			groupMembershipRepository.deleteByExternalNetwork(network);
			
			RecommendationEngine engine = getRecommendationEngine(network);
			engine.train(); // this will trigger callbacks and complete all membership assignments	
		
			
			// now for all groups, get the average angaged activities
			Set<String> groups = groupActivityRecommendationMap.keySet();
			for(String group : groups) {
				
				List<EngagedActivity> engagedActivities = engagedActivityRepository.findMeanByGroup(group, 10);
				for(EngagedActivity engagedActivity : engagedActivities) {
					groupActivityRecommendationMap.get(group).add(engagedActivity.getActivity());
				}
				
				// now videos
				List<EngagedVideo> engagedVideos = engagedVideoRepository.findMeanByGroup(group, 10);
				for(EngagedVideo engagedVideo : engagedVideos) {
					groupVideoRecommendationMap.get(group).add(engagedVideo.getVideoContent());
				}
				

			}

		}

	}


	private void setUpRecommendationEngine(Configuration configuration) {
		globalRecommendationEngine = new RecommendationEngineSparkImpl(configuration, this);
		globalRecommendationEngine.addDimension(Dimension.createFromEnum("gender", Gender.class));

		// now do this for each network with what we've learned about our weighting
		RecommendationEngine engine = new RecommendationEngineSparkImpl(configuration, this);
		engine.addDimension(Dimension.createFromEnum("gender", Gender.class));
		networkEngineMap.put(ExternalNetwork.Facebook, engine);

		engine = new RecommendationEngineSparkImpl(configuration, this);
		engine.addDimension(Dimension.createFromEnum("gender", Gender.class));
		networkEngineMap.put(ExternalNetwork.YouTube, engine);
	}


	@Override
	public void didAssignMembership(User user,
			ExternalNetwork network, String group) {
		log.debug("User {} assigned network membership to {}", user, group);
		groupMembershipRepository.create(new GroupMembership(network, user, group));
		
		// if there is no array for this group identifier, create one
		if(!groupActivityRecommendationMap.containsKey(group))
				groupActivityRecommendationMap.put(group, new LinkedList<Activity>());
	}

	/**
	 * Get recommendation engine by network 
	 * @param contact
	 * @return Recommendation engine for this network
	 * 
	 * @throws UnsupportedOperationException if there is no engine for this network
	 */
	private RecommendationEngine getRecommendationEngine(Contact contact) {
		
		// TODO: wishlist have ref to enum and not int
		ExternalNetwork network = ExternalNetwork.getNetworkById(contact.getExternalIdentity().getExternalNetwork());
		return getRecommendationEngine(network);
	}
	

	private RecommendationEngine getRecommendationEngine(ExternalNetwork network) {
		RecommendationEngine engine = networkEngineMap.get(network);
		if(engine == null)
			throw new UnsupportedOperationException("Recommendations are not supported for this network: " + network);
		return engine;
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
