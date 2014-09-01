package com.ubiquity.sprocket.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.common.serialize.JsonConverter;
import com.niobium.repository.Cache;
import com.niobium.repository.CacheRedisHashImpl;
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
import com.ubiquity.sprocket.analytics.recommendation.Dimension;
import com.ubiquity.sprocket.analytics.recommendation.RecommendationEngine;
import com.ubiquity.sprocket.analytics.recommendation.RecommendationEngineSparkImpl;
import com.ubiquity.sprocket.domain.EngagedActivity;
import com.ubiquity.sprocket.domain.EngagedDocument;
import com.ubiquity.sprocket.domain.EngagedItem;
import com.ubiquity.sprocket.domain.GroupMembership;
import com.ubiquity.sprocket.repository.EngagedActivityRepository;
import com.ubiquity.sprocket.repository.EngagedActivityRepositoryJpaImpl;
import com.ubiquity.sprocket.repository.EngagedDocumentRepository;
import com.ubiquity.sprocket.repository.EngagedItemRepository;
import com.ubiquity.sprocket.repository.EngagedItemRepositoryJpaImpl;
import com.ubiquity.sprocket.repository.EngagedVideoRepository;
import com.ubiquity.sprocket.repository.EngagedVideoRepositoryJpaImpl;
import com.ubiquity.sprocket.repository.GroupMembershipRepository;

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
	
	private UserDataModificationCache dataModificationCache;
	private Cache recommendedActivitiesCache;


	// in-memory cache will keep this 
	private Map<String, List<Activity>> groupActivityRecommendationMap = new HashMap<String, List<Activity>>();

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
		engagedVideoRepository = new EngagedVideoRepositoryJpaImpl();
		
		dataModificationCache = new UserDataModificationCacheRedisImpl(
				configuration
				.getInt(DataCacheKeys.Databases.ENDPOINT_MODIFICATION_DATABASE_GROUP));
			
		recommendedActivitiesCache = new CacheRedisHashImpl(CacheKeys.GroupProperties.RECOMMENDED_ACTIVITIES, 15); // TODO: changeme
		

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
		//globalRecommendationEngine.updateProfileRecord(contact);
	}

	/***
	 * Updates a profile record in the data cluster to be used in the recommendation / clustering
	 */
	public void updateProfileRecordForExternalNetwork(Contact contact) {
		//		// get network specific engine and update the profile
		//		RecommendationEngine engine = getRecommendationEngine(contact);
		//		engine.updateProfileRecord(contact);
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
		
		String cached = recommendedActivitiesCache.get(groupMembership.getGroupIdentifier());
		List<Activity> activities = JsonConverter.getInstance().convertToListFromPayload(cached, Activity.class);

		return new CollectionVariant<Activity>(activities, lastModified);
	}
	/***
	 * Entry point for running the entire cycle of recommendations: add cases to the search space, classify, and group
	 * 
	 **/
	public void startRecommendationCycle() {
		
		recommendationEngine.clear();
		
		// remove all FB assignments
		groupMembershipRepository.deleteByExternalNetwork(ExternalNetwork.Facebook);

		// query all FB contacts
		List<Contact> fbContacts = contactRepository.findByExternalNetwork(ExternalNetwork.Facebook);
		// update instance space
		recommendationEngine.updateProfileRecords(fbContacts);
		
		// train model
		recommendationEngine.train();
		
		// cluster FB users
		recommendationEngine.assign(fbContacts, ExternalNetwork.Facebook.toString());
		
		// now get the unique set of group names (they are also the clusters) for FB
		List<String> groups = groupMembershipRepository.findGroupIdentifiersByExternalNetwork(ExternalNetwork.Facebook);
		
		
		for(String group : groups) {
			List<EngagedActivity> engagedActivities = engagedActivityRepository.findMeanByGroup(group, 10);
			List<EngagedDocument> engagedDocuments = engagedDocumentRepository.findMeanByGroup(group, 10);
			for(EngagedActivity engagedActivity : engagedActivities) {
				recommendedActivitiesCache.put(group, JsonConverter.getInstance().convertToPayload(engagedActivity.getActivity()));
			}
			// these will be activities clicked on from search results
			for(EngagedDocument engagedDocument : engagedDocuments) {
				Activity activity = engagedDocument.getActivity();
				if(activity != null)
					recommendedActivitiesCache.put(group, JsonConverter.getInstance().convertToPayload(engagedDocument.getActivity()));
			}
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
