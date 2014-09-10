package com.ubiquity.sprocket.service;

import java.util.HashSet;
import java.util.LinkedList;
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
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.repository.UserRepository;
import com.ubiquity.location.repository.UserLocationRepository;
import com.ubiquity.location.repository.UserLocationRepositoryJpaImpl;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.Gender;
import com.ubiquity.social.repository.ContactRepository;
import com.ubiquity.social.repository.ContactRepositoryJpaImpl;
import com.ubiquity.sprocket.analytics.recommendation.Dimension;
import com.ubiquity.sprocket.analytics.recommendation.Profile;
import com.ubiquity.sprocket.analytics.recommendation.RecommendationEngine;
import com.ubiquity.sprocket.analytics.recommendation.RecommendationEngineSparkImpl;
import com.ubiquity.sprocket.domain.EngagedActivity;
import com.ubiquity.sprocket.domain.EngagedDocument;
import com.ubiquity.sprocket.domain.EngagedItem;
import com.ubiquity.sprocket.domain.EngagedVideo;
import com.ubiquity.sprocket.domain.GroupMembership;
import com.ubiquity.sprocket.domain.RecommendedActivity;
import com.ubiquity.sprocket.domain.RecommendedVideo;
import com.ubiquity.location.domain.UserLocation;
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
import com.ubiquity.sprocket.repository.RecommendedVideoRepository;

/***
 * Service for executing tracking engagement, assigning contacts to groups, and recommending content
 * 
 * @author chris
 *
 */
public class AnalyticsService {

	private Logger log = LoggerFactory.getLogger(getClass());

	private VideoContentRepository videoContentRepository;
	private EngagedItemRepository engagedItemRepository;
	private EngagedActivityRepository engagedActivityRepository;
	private EngagedDocumentRepository engagedDocumentRepository;
	private EngagedVideoRepository engagedVideoRepository;
	private GroupMembershipRepository groupMembershipRepository;
	private ContactRepository contactRepository;
	private RecommendedActivityRepository recommendedActivityRepository;
	private RecommendedVideoRepository recommendedVideoRepository;
	private UserRepository userRepository;
	private UserLocationRepository locationRepository;

	private UserDataModificationCache dataModificationCache;

	private RecommendationEngine recommendationEngine;


	/***
	 * Sets up repositories and data modification cache
	 * 
	 * @param configuration
	 */
	public AnalyticsService(Configuration configuration) {
		setUpRecommendationEngine(configuration);


		videoContentRepository = new VideoContentRepositoryJpaImpl();
		engagedItemRepository = new EngagedItemRepositoryJpaImpl();
		engagedActivityRepository = new EngagedActivityRepositoryJpaImpl();
		engagedDocumentRepository = new EngagedDocumentRepositoryJpaImpl();
		engagedVideoRepository = new EngagedVideoRepositoryJpaImpl();
		groupMembershipRepository = new GroupMembershipRepositoryJpaImpl();
		contactRepository = new ContactRepositoryJpaImpl();
		recommendedActivityRepository = new RecommendedActivityRepositoryJpaImpl();
		locationRepository = new UserLocationRepositoryJpaImpl();

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

	
	public void assign(User user) {
		
	}
	/***
	 * Recommends assigns the profile to a group for all networks
	 * 
	 * @param profile
	 * 
	 */
	public void assign(Profile profile) {

		List<GroupMembership> membershipList = recommendationEngine.assign(profile);

		// delete membership for this profile

		// persisting this for now but we may not need to in the future
		for(GroupMembership membership : membershipList) {
			EntityManagerSupport.beginTransaction();
			groupMembershipRepository.create(membership);
			EntityManagerSupport.commit();
		}

	}

	/***
	 * Entry point for running the entire cycle of recommendations: add cases to the search space, classify, and group
	 * 
	 **/
	public void assignGroupsAndCreateRecommendedContent() {

		// clear distributed data store for all contexts
		recommendationEngine.clear();

		Set<String> groups = assignGroups(ExternalNetwork.Facebook);
		createRecommendedActivities(groups, ExternalNetwork.Facebook);

		groups = assignGroups(ExternalNetwork.Google);
		createRecommendedVideos(groups, ExternalNetwork.YouTube);


	}


	private void createRecommendedVideos(Set<String> groups, ExternalNetwork network) {
		for(String group : groups) {
			List<EngagedVideo> engagedVideos = engagedVideoRepository.findMeanByGroup(group, 10);
			List<EngagedDocument> engagedDocuments = engagedDocumentRepository.findMeanByGroup(group, 10);
			for(EngagedVideo engagedVideo : engagedVideos) {
				EntityManagerSupport.beginTransaction();
				recommendedVideoRepository.create(new RecommendedVideo(engagedVideo.getVideoContent(), group));
				EntityManagerSupport.commit();
			}
			// these will be activities clicked on from search results
			for(EngagedDocument engagedDocument : engagedDocuments) {
				VideoContent videoContent = engagedDocument.getVideoContent();
				if(videoContent != null) {
					EntityManagerSupport.beginTransaction();
					recommendedVideoRepository.create(new RecommendedVideo(videoContent, group));
					EntityManagerSupport.commit();
				}
			}

			String key = CacheKeys.generateCacheKeyForExternalNetwork(CacheKeys.GroupProperties.RECOMMENDED_ACTIVITIES, network);
			dataModificationCache.put(Long.parseLong(group), key, System.currentTimeMillis());


		}
	}

	private void createRecommendedActivities(Set<String> groups, ExternalNetwork network) {
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

			String key = CacheKeys.generateCacheKeyForExternalNetwork(CacheKeys.GroupProperties.RECOMMENDED_ACTIVITIES, network);
			dataModificationCache.put(Long.parseLong(group), key, System.currentTimeMillis());


		}
	}

	/***
	 * Returns video content or null if there is no entry for this user in the cache
	 * 
	 * @param ownerId
	 * @param ifModifiedSince
	 * @return
	 */
	public CollectionVariant<VideoContent> findAllRecommendedVideos(Long ownerId, ExternalNetwork network, Long ifModifiedSince) {

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
		String key = CacheKeys.generateCacheKeyForExternalNetwork(CacheKeys.GroupProperties.RECOMMENDED_VIDEOS, network);
		Long lastModified = dataModificationCache.getLastModified((long)network.ordinal(), key, ifModifiedSince);

		// If there is no cache entry, there is no data
		if(lastModified == null) {
			return null;
		}

		List<VideoContent> videos = recommendedVideoRepository.findRecommendedVideosByGroup(groupMembership.getGroupIdentifier());

		return new CollectionVariant<VideoContent>(videos, lastModified);
	}


	private void updateDataStoreWithLatestProfileData() {
//		// track the unique set of group names
//		Set<String> groups = new HashSet<String>();

		// remove all assignments in db by this network
		List<User> users = userRepository.findAll();
		List<Profile> profiles = new LinkedList<Profile>();

		for(User user : users) {
			List<Contact> contacts = contactRepository.findByOwnerId(user.getUserId(), Boolean.TRUE);
			UserLocation location = locationRepository.findByUserId(user.getUserId());
			Profile profile = new Profile(user, location);
			profile.getContacts().addAll(contacts);
			profiles.add(profile);
		}

		// update instance space
		recommendationEngine.updateProfileRecords(profiles);
		
		// now train global and FB
		recommendationEngine.train();
		recommendationEngine.train(ExternalNetwork.Facebook);
	}
	
	
	private Set<String> assignGroups(ExternalNetwork network) {
		// track the unique set of group names
		Set<String> groups = new HashSet<String>();

		// remove all assignments in db by this network
		groupMembershipRepository.deleteByExternalNetwork(network);

		// query all contacts
		List<Contact> contacts = contactRepository.findByExternalNetwork(network);
		for(Contact contact : contacts) {
			User owner = contact.getOwner();
			UserLocation location = locationRepository.findByUserId(owner.getUserId());
			Profile profile = new Profile(owner, location);
			// only add this contact for the assignment
			profile.getContacts().add(contact);
					
			List<GroupMembership> membershipList = recommendationEngine.assign(profile, network);
			// persist assignments
			for(GroupMembership membership : membershipList) {
				EntityManagerSupport.beginTransaction();
				groupMembershipRepository.create(membership);
				EntityManagerSupport.commit();

				// add to groups
				groups.add(membership.getGroupIdentifier());
			}
		}
		return groups;

	}



	private void setUpRecommendationEngine(Configuration configuration) {
		recommendationEngine = new RecommendationEngineSparkImpl(configuration);

		// add dimension to global context with all weight values at 1
		recommendationEngine.addDimension(Dimension.createFromEnum("gender", Gender.class));
		recommendationEngine.addDimension(new Dimension("ageRange", Range.between(0.0, 120.0), 1.0));

		// create FB specific context, with dimensions where gender does not matter much but age range does
		recommendationEngine.addContext(ExternalNetwork.Facebook, configuration);
		recommendationEngine.addDimension(Dimension.createFromEnum("gender", Gender.class, 0.1), ExternalNetwork.Facebook);
		recommendationEngine.addDimension(new Dimension("ageRange", Range.between(0.0, 120.0), 1.0), ExternalNetwork.Facebook);

		// create Google specific context with equal weights to recommend YouTube videos
		recommendationEngine.addContext(ExternalNetwork.Google, configuration);
		recommendationEngine.addDimension(Dimension.createFromEnum("gender", Gender.class, 1.0), ExternalNetwork.Google);
		recommendationEngine.addDimension(new Dimension("ageRange", Range.between(0.0, 120.0), 1.0), ExternalNetwork.Google);

	}

}
