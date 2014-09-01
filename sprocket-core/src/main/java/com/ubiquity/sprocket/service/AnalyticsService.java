package com.ubiquity.sprocket.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.CollectionVariant;
import com.niobium.repository.cache.DataCacheKeys;
import com.niobium.repository.cache.UserDataModificationCache;
import com.niobium.repository.cache.UserDataModificationCacheRedisImpl;
import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.content.domain.VideoContent;
import com.ubiquity.content.repository.VideoContentRepository;
import com.ubiquity.content.repository.VideoContentRepositoryJpaImpl;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.external.repository.cache.CacheKeys;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.Gender;
import com.ubiquity.social.repository.ActivityRepository;
import com.ubiquity.social.repository.ActivityRepositoryJpaImpl;
import com.ubiquity.social.repository.ContactRepository;
import com.ubiquity.social.repository.ContactRepositoryJpaImpl;
import com.ubiquity.sprocket.analytics.recommendation.Dimension;
import com.ubiquity.sprocket.analytics.recommendation.RecommendationEngine;
import com.ubiquity.sprocket.analytics.recommendation.RecommendationEngineSparkImpl;
import com.ubiquity.sprocket.domain.EngagedActivity;
import com.ubiquity.sprocket.domain.EngagedDocument;
import com.ubiquity.sprocket.domain.EngagedItem;
import com.ubiquity.sprocket.domain.GroupMembership;
import com.ubiquity.sprocket.domain.RecommendedActivity;
import com.ubiquity.sprocket.repository.EngagedActivityRepository;
import com.ubiquity.sprocket.repository.EngagedActivityRepositoryJpaImpl;
import com.ubiquity.sprocket.repository.EngagedDocumentRepository;
import com.ubiquity.sprocket.repository.EngagedDocumentRepositoryJpaImpl;
import com.ubiquity.sprocket.repository.EngagedItemRepository;
import com.ubiquity.sprocket.repository.EngagedItemRepositoryJpaImpl;
import com.ubiquity.sprocket.repository.EngagedVideoRepository;
import com.ubiquity.sprocket.repository.EngagedVideoRepositoryJpaImpl;
import com.ubiquity.sprocket.repository.GroupMembershipRepository;
import com.ubiquity.sprocket.repository.GroupMembershipRepositoryJpaImpl;
import com.ubiquity.sprocket.repository.RecommendedActivityRepository;
import com.ubiquity.sprocket.repository.RecommendedActivityRepositoryJpaImpl;

public class AnalyticsService {

	private Logger log = LoggerFactory.getLogger(getClass());

	private ActivityRepository activityRepository;
	private VideoContentRepository videoContentRepository;
	private EngagedItemRepository engagedItemRepository;
	private EngagedActivityRepository engagedActivityRepository;
	private EngagedDocumentRepository engagedDocumentRepository;
	private EngagedVideoRepository engagedVideoRepository;
	private GroupMembershipRepository groupMembershipRepository;
	private ContactRepository contactRepository;
	private RecommendedActivityRepository recommendedActivityRepository;
	
	private UserDataModificationCache dataModificationCache;
	
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
		engagedActivityRepository = new EngagedActivityRepositoryJpaImpl();
		engagedDocumentRepository = new EngagedDocumentRepositoryJpaImpl();
		engagedVideoRepository = new EngagedVideoRepositoryJpaImpl();
		groupMembershipRepository = new GroupMembershipRepositoryJpaImpl();
		contactRepository = new ContactRepositoryJpaImpl();
		recommendedActivityRepository = new RecommendedActivityRepositoryJpaImpl();
		
		dataModificationCache = new UserDataModificationCacheRedisImpl(
				configuration
				.getInt(DataCacheKeys.Databases.ENDPOINT_MODIFICATION_DATABASE_GROUP));		

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
	 * Stub recommendation recommends 1 video that is public
	 * 
	 * @param userId
	 * @return
	 */
	public List<VideoContent> getRecommendedVideos(Long userId) {
		return videoContentRepository.findAllWithoutOwner(2);
	}
	
	
	/***
	 * Returns video content or null if there is no entry for this user in the cache
	 * 
	 * @param ownerId
	 * @param ifModifiedSince
	 * @return
	 */
	public CollectionVariant<Activity> findAllRecommendedActivities(Long ownerId, ExternalNetwork network, Long ifModifiedSince) {
		
		// get the group map for this user
		List<GroupMembership> groupMembershipList = groupMembershipRepository.findAllByUserId(ownerId);
		
		GroupMembership groupMembership = null;
		for(GroupMembership assigned : groupMembershipList) {
			if(assigned.getExternalNetwork() == network) {
				groupMembership = assigned;
				break;
			}
		}
		// if nothing is assigned, then return nothing
		if(groupMembership == null) {
			return null;
		}
		String key = CacheKeys.generateCacheKeyForExternalNetwork(CacheKeys.GroupProperties.RECOMMENDED_ACTIVITIES, network);
		Long lastModified = dataModificationCache.getLastModified((long)network.ordinal(), key, ifModifiedSince);

		// If there is no cache entry, there is no data
		if(lastModified == null) {
			return null;
		}
		
		List<Activity> activities = recommendedActivityRepository.findRecommendedActivitiesByGroup(groupMembership.getGroupIdentifier());
		
		return new CollectionVariant<Activity>(activities, lastModified);
	}
	/***
	 * Entry point for running the entire cycle of recommendations: add cases to the search space, classify, and group
	 * 
	 **/
	public void recommend() {
		
		recommendationEngine.clear();
		
		// remove all FB assignments
		groupMembershipRepository.deleteByExternalNetwork(ExternalNetwork.Facebook);

		// query all FB contacts
		List<Contact> fbContacts = contactRepository.findByExternalNetwork(ExternalNetwork.Facebook);
		// update instance space
		recommendationEngine.updateProfileRecords(fbContacts);
		
		String fbContext = ExternalNetwork.Facebook.toString();
		// train model
		recommendationEngine.train(fbContext);
		
		// cluster FB users
		List<GroupMembership> membershipList = recommendationEngine.assign(fbContacts, fbContext);
		// track the unique set of group names
		Set<String> groups = new HashSet<String>();
		// persist assignments
		for(GroupMembership membership : membershipList) {
			EntityManagerSupport.beginTransaction();
			groupMembershipRepository.create(membership);
			EntityManagerSupport.commit();
			
			// add to groups
			groups.add(membership.getGroupIdentifier());
		}
				
		for(String group : groups) {
			List<EngagedActivity> engagedActivities = engagedActivityRepository.findMeanByGroup(group, 10);
			List<EngagedDocument> engagedDocuments = engagedDocumentRepository.findMeanByGroup(group, 10);
			for(EngagedActivity engagedActivity : engagedActivities) {
				EntityManagerSupport.beginTransaction();
				recommendedActivityRepository.create(new RecommendedActivity(engagedActivity.getActivity(), group));
				EntityManagerSupport.commit();

			}
			// these will be activities clicked on from search results
			for(EngagedDocument engagedDocument : engagedDocuments) {
				Activity activity = engagedDocument.getActivity();
				if(activity != null) {
					EntityManagerSupport.beginTransaction();
					recommendedActivityRepository.create(new RecommendedActivity(engagedDocument.getActivity(), group));
					EntityManagerSupport.commit();
				}
			}
			
			String key = CacheKeys.generateCacheKeyForExternalNetwork(CacheKeys.GroupProperties.RECOMMENDED_ACTIVITIES, ExternalNetwork.Facebook);
			dataModificationCache.put(Long.parseLong(group), key, System.currentTimeMillis());
		}
		
		

	}


	private void setUpRecommendationEngine(Configuration configuration) {
		recommendationEngine = new RecommendationEngineSparkImpl(configuration);

		// add dimension to global context with all weight values at 1
		recommendationEngine.addDimension(Dimension.createFromEnum("gender", Gender.class));
		recommendationEngine.addDimension(new Dimension("ageRange", Range.between(0.0, 120.0), 1.0));

		// create fb specific context, with dimensions where
		String fbContext = ExternalNetwork.Facebook.toString();
		recommendationEngine.addContext(fbContext, configuration);
		recommendationEngine.addDimension(Dimension.createFromEnum("gender", Gender.class, 0.1), fbContext);
		recommendationEngine.addDimension(new Dimension("ageRange", Range.between(0.0, 120.0), 1.0), fbContext);

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
