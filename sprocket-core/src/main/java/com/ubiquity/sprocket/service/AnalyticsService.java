package com.ubiquity.sprocket.service;

import java.util.List;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.CollectionVariant;
import com.niobium.repository.cache.DataCacheKeys;
import com.niobium.repository.cache.DataModificationCache;
import com.niobium.repository.cache.DataModificationCacheRedisImpl;
import com.niobium.repository.cache.UserDataModificationCache;
import com.niobium.repository.cache.UserDataModificationCacheRedisImpl;
import com.niobium.repository.jpa.EntityManagerSupport;
import com.niobium.repository.lily.LilyRepositoryFactory;
import com.ubiquity.identity.domain.User;
import com.ubiquity.integration.domain.Activity;
import com.ubiquity.integration.domain.Contact;
import com.ubiquity.integration.domain.ExternalInterest;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.Gender;
import com.ubiquity.integration.domain.Interest;
import com.ubiquity.integration.domain.VideoContent;
import com.ubiquity.integration.repository.ContactRepositoryJpaImpl;
import com.ubiquity.integration.repository.ExternalInterestRepositoryJpaImpl;
import com.ubiquity.integration.repository.InterestRepositoryJpaImpl;
import com.ubiquity.integration.repository.cache.CacheKeys;
import com.ubiquity.sprocket.analytics.recommendation.Dimension;
import com.ubiquity.sprocket.analytics.recommendation.RecommendationEngine;
import com.ubiquity.sprocket.analytics.recommendation.RecommendationEngineSparkImpl;
import com.ubiquity.sprocket.domain.Content;
import com.ubiquity.sprocket.domain.GroupMembership;
import com.ubiquity.sprocket.domain.Profile;
import com.ubiquity.sprocket.domain.RecommendedActivity;
import com.ubiquity.sprocket.domain.RecommendedVideo;
import com.ubiquity.sprocket.repository.ContentRepository;
import com.ubiquity.sprocket.repository.ContentRepositoryLilyImpl;
import com.ubiquity.sprocket.repository.GroupMembershipRepository;
import com.ubiquity.sprocket.repository.GroupMembershipRepositoryJpaImpl;
import com.ubiquity.sprocket.repository.ProfileRepository;
import com.ubiquity.sprocket.repository.ProfileRepositoryLilyImpl;
import com.ubiquity.sprocket.repository.RecommendedActivityRepository;
import com.ubiquity.sprocket.repository.RecommendedActivityRepositoryJpaImpl;
import com.ubiquity.sprocket.repository.RecommendedVideoRepository;
import com.ubiquity.sprocket.repository.RecommendedVideoRepositoryJpaImpl;

/***
 * Service for executing tracking engagement, assigning contacts to groups, and
 * recommending content
 * 
 * @author chris
 * 
 */
public class AnalyticsService {

	private Logger log = LoggerFactory.getLogger(getClass());
	private UserDataModificationCache userDataModificationCache;
	private DataModificationCache dataModificationCache;
	private RecommendationEngine recommendationEngine;
    private String namespace;
	
	/***
	 * Sets up repositories and data modification cache
	 * 
	 * @param configuration
	 */
	public AnalyticsService(Configuration configuration) {
		setUpLily(configuration);
		
		
		
//		setUpRecommendationEngine(configuration);

		userDataModificationCache = new UserDataModificationCacheRedisImpl(
				configuration
				.getInt(DataCacheKeys.Databases.ENDPOINT_MODIFICATION_DATABASE_GROUP));
		dataModificationCache = new DataModificationCacheRedisImpl(
				configuration.getInt(DataCacheKeys.Databases.ENDPOINT_MODIFICATION_DATABASE_GENERAL));
		String key = CacheKeys
				.generateCacheKeyForPlaces(CacheKeys.GlobalProperties.INTERESTS);
		Long lastModified = dataModificationCache.getLastModified(key, 0L);

		// If there is no cache entry
		if (lastModified == null) {
			if(new InterestRepositoryJpaImpl().countAllInterests()>0){
				resetInterestsLastModifiedCache();
			}
		}
	}

	public Profile createProfile(User user) {
		ProfileRepository profileRepository = new ProfileRepositoryLilyImpl(namespace, LilyRepositoryFactory.createRepository());
		Profile profile = new Profile.Builder().profileId(user.getUserId().toString()).build();
		profileRepository.create(profile);
		return profile;
	}
	
	public void create(Profile profile) {
		ProfileRepository profileRepository = new ProfileRepositoryLilyImpl(namespace, LilyRepositoryFactory.createRepository());
		profileRepository.create(profile);
	}
	
	public void track(String searchTerm, User user) {
		ProfileRepository profileRepository = new ProfileRepositoryLilyImpl(namespace, LilyRepositoryFactory.createRepository());
		Profile profile = profileRepository.read(user.getUserId().toString());
		profile.getSearchHistory().add(searchTerm);
		profileRepository.update(profile);
	}
	
	/***
	 * Tracks content by persisting it to the underlying data store
	 * 
	 * @param content
	 */
	public void track(Content content) {
		ContentRepository contentRepository = new ContentRepositoryLilyImpl(namespace, LilyRepositoryFactory.createRepository());
		contentRepository.create(content);
	}
	
	public void track(Content content, Long userId, Long timestamp) {
		
	}
	
	
	/**
	 * Save a user's search term to the data warehouse
	 * 
	 * @param user
	 * @param searchTerm
	 */
	public void track(User user, String searchTerm) {
		
	}

	
	public void create(Interest interest) {
		try {
			EntityManagerSupport.beginTransaction();
			new InterestRepositoryJpaImpl().create(interest);
			EntityManagerSupport.commit();
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}
	public void create(ExternalInterest externalInterest) {
		try {
			EntityManagerSupport.beginTransaction();
			new ExternalInterestRepositoryJpaImpl().create(externalInterest);
			EntityManagerSupport.commit();
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}
	public CollectionVariant<Interest> findInterests(Long ifModifiedSince) {

		Long lastModified = dataModificationCache.getLastModified(CacheKeys.GlobalProperties.INTERESTS, ifModifiedSince);

		// If there is no cache entry, there is no data
		if (lastModified == null) {
			return null;
		}

		try {
			return new CollectionVariant<Interest>(new InterestRepositoryJpaImpl().findTopLevel(), lastModified);
		} finally {
			EntityManagerSupport.closeEntityManager();
		}

	}
	
	
	public CollectionVariant<Interest> findInterestsByExternalNetworkId(ExternalNetwork network, Long ifModifiedSince ) {

//		Long lastModified = dataModificationCache.getLastModified(CacheKeys.GlobalProperties.INTERESTS, ifModifiedSince);

		// If there is no cache entry, there is no data
//		if (lastModified == null) {
//			return null;
//		}

		try {
			return new CollectionVariant<Interest>(new ExternalInterestRepositoryJpaImpl().getByDistinctInterestByExternalNetwork(network),null);
		} finally {
			EntityManagerSupport.closeEntityManager();
		}

	}



	/***
	 * Returns video content or null if there is no entry for this user in the
	 * cache
	 * 
	 * @param ownerId
	 * @param ifModifiedSince
	 * @return
	 */
	public CollectionVariant<Activity> findAllRecommendedActivities(
			Long ownerId, ExternalNetwork network, Long ifModifiedSince) {

		try {

			RecommendedActivityRepository recommendedActivityRepository = new RecommendedActivityRepositoryJpaImpl();
			GroupMembershipRepository groupMembershipRepository = new GroupMembershipRepositoryJpaImpl();
			// get the group map for this user
			List<GroupMembership> groupMembershipList = groupMembershipRepository
					.findAllByUserId(ownerId);

			GroupMembership groupMembership = null;
			for (GroupMembership assigned : groupMembershipList) {
				if (assigned.getExternalNetwork() == network) {
					groupMembership = assigned;
					break;
				}
			}
			// if nothing is assigned, then return nothing
			if (groupMembership == null) {
				return null;
			}
			String key = CacheKeys.generateCacheKeyForExternalNetwork(
					CacheKeys.GroupProperties.RECOMMENDED_ACTIVITIES, network);
			Long lastModified = userDataModificationCache.getLastModified(
					Long.valueOf(groupMembership.getGroupIdentifier()), key,
					ifModifiedSince);

			// If there is no cache entry, there is no data
			if (lastModified == null) {
				return null;
			}

			List<Activity> activities = recommendedActivityRepository
					.findRecommendedActivitiesByGroup(
							groupMembership.getGroupIdentifier(), network);
			return new CollectionVariant<Activity>(activities, lastModified);
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}

	/***
	 * Will create assignments for all contexts
	 */
	public void assignAll() {

//		try {
//			UserRepository userRepository = new UserRepositoryJpaImpl();
//			UserLocationRepository locationRepository = new UserLocationRepositoryJpaImpl();
//			ContactRepository contactRepository = new ContactRepositoryJpaImpl();
//
//			List<User> allUsers = userRepository.findAll();
//			for (User user : allUsers) {
//				Profile profile = new Profile(user,
//						locationRepository.findByUserId(user.getUserId()));
//				profile.getContacts().addAll(
//						contactRepository.findByOwnerId(user.getUserId(),
//								Boolean.TRUE));
//				// just assign contexts we have built so far
//				for (Contact contact : profile.getContacts()) {
//					ExternalNetwork network = ExternalNetwork
//							.getNetworkById(contact.getExternalIdentity()
//									.getExternalNetwork());
//					if (network == ExternalNetwork.Facebook
//							|| network == ExternalNetwork.Google)
//						recommendationEngine.assign(profile, network);
//				}
//			}
//		} finally {
//			EntityManagerSupport.closeEntityManager();
//		}
	}

	/***
	 * Recommends assigns the profile to a group for all networks
	 * 
	 * @param profile
	 * 
	 */
	public void assign(Profile profile) {

		List<GroupMembership> membershipList = recommendationEngine
				.assign(profile);

		try {
			GroupMembershipRepository groupMembershipRepository = new GroupMembershipRepositoryJpaImpl();

			EntityManagerSupport.beginTransaction();
			groupMembershipRepository.deleteByUserId(profile.getUserId());

			// persisting this for now but we may not need to in the future
			for (GroupMembership membership : membershipList) {
				groupMembershipRepository.create(membership);
			}
			EntityManagerSupport.commit();

		} finally {
			EntityManagerSupport.closeEntityManager();
		}

	}

	/**
	 * Trains the model for a context
	 * 
	 * @param context
	 */
	private void train(ExternalNetwork context) {
		try {
			
			// check to see if there is any data for this context; if not,return
			if (new ContactRepositoryJpaImpl()
			.countAllByExternalNetwork(context) == 0) {
				log.warn("Skipping train on context: {} because no users have signed in yet for it");
				return;
			}

			recommendationEngine.train(context);
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}

	/**
	 * Trains the model for the global context
	 * 
	 * @param context
	 */
	@SuppressWarnings("unused")
	private void train() {
		recommendationEngine.train();
	}

	/***
	 * Trains each network and assigns all groups, then creates recommended
	 * content
	 * 
	 **/
	public void assignGroupsAndCreateRecommendedContent() {

		train(ExternalNetwork.Facebook);
//		Set<String> groups = assignGroups(ExternalNetwork.Facebook);
//		createRecommendedActivities(groups, ExternalNetwork.Facebook);
//
//		train(ExternalNetwork.Google);
//		groups = assignGroups(ExternalNetwork.Google);
//		createRecommendedVideos(groups, ExternalNetwork.YouTube);

	}

	
	
	
	/***
	 * Creates an assignment (or re-assign) for this and external network
	 * 
	 * @param contact
	 */
	public void assign(Long userId, ExternalNetwork network) {
		try {
			List<Contact> contacts = new ContactRepositoryJpaImpl()
			.findByOwnerIdExternalNetwork(userId, network);
			// TODO: do we have multiple contacts? we should not allow this any
			// more
			for (Contact contact : contacts) {
				assign(contact);
			}
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}

	/***
	 * Creates an assignment (or re-assign) for this contact
	 * 
	 * @param contact
	 */
	public void assign(Contact contact) {

//		try {
//			User user = contact.getOwner();
//			Profile profile = new Profile(user,
//					new UserLocationRepositoryJpaImpl().findByUserId(user
//							.getUserId()));
//			profile.getContacts().add(contact);
//			ExternalNetwork network = ExternalNetwork.getNetworkById(contact
//					.getExternalIdentity().getExternalNetwork());
//			List<GroupMembership> membershipList = recommendationEngine.assign(
//					profile, network);
//
//			log.info("assigning emembership: {}", membershipList);
//			// save to DB
//
//			GroupMembershipRepository groupMembershipRepository = new GroupMembershipRepositoryJpaImpl();
//			EntityManagerSupport.beginTransaction();
//			groupMembershipRepository.deleteByExternalNetworkAndUserId(network,
//					user.getUserId());
//			for (GroupMembership membership : membershipList) {
//				groupMembershipRepository.create(membership);
//			}
//			EntityManagerSupport.commit();
//		} finally {
//			EntityManagerSupport.closeEntityManager();
//		}
	}

	private void createRecommendedVideos(Set<String> groups,
			ExternalNetwork network) {

		try {
			RecommendedVideoRepository recommendedVideoRepository = new RecommendedVideoRepositoryJpaImpl();
//			EngagedDocumentRepository engagedDocumentRepository = new EngagedDocumentRepositoryJpaImpl();
//			EngagedVideoRepository engagedVideoRepository = new EngagedVideoRepositoryJpaImpl();

			EntityManagerSupport.beginTransaction();
			List<RecommendedVideo> recommended = recommendedVideoRepository
					.findAllByExternalNetwork(network);
			for (RecommendedVideo video : recommended)
				recommendedVideoRepository.delete(video);
			EntityManagerSupport.commit();

			for (String group : groups) {
//				List<EngagedVideo> engagedVideos = engagedVideoRepository
//						.findMeanByGroup(group, 10);
//				List<EngagedDocument> engagedDocuments = engagedDocumentRepository
//						.findMeanByGroup(group, 10);
//				for (EngagedVideo engagedVideo : engagedVideos) {
//					EntityManagerSupport.beginTransaction();
//					recommendedVideoRepository.create(new RecommendedVideo(
//							engagedVideo.getVideoContent(), group));
//					EntityManagerSupport.commit();
//				}
				// these will be activities clicked on from search results
//				for (EngagedDocument engagedDocument : engagedDocuments) {
//					VideoContent videoContent = engagedDocument
//							.getVideoContent();
//					if (videoContent != null) {
//						EntityManagerSupport.beginTransaction();
//						recommendedVideoRepository.create(new RecommendedVideo(
//								videoContent, group));
//						EntityManagerSupport.commit();
//					}
//				}

				String key = CacheKeys.generateCacheKeyForExternalNetwork(
						CacheKeys.GroupProperties.RECOMMENDED_VIDEOS, network);
				userDataModificationCache.put(Long.parseLong(group), key,
						System.currentTimeMillis());

			}

		} finally {
			EntityManagerSupport.closeEntityManager();
		}

	}

	private void createRecommendedActivities(Set<String> groups,
			ExternalNetwork network) {

		try {
			RecommendedActivityRepository recommendedActivityRepository = new RecommendedActivityRepositoryJpaImpl();
//			EngagedDocumentRepository engagedDocumentRepository = new EngagedDocumentRepositoryJpaImpl();
//			EngagedActivityRepository engagedActivityRepository = new EngagedActivityRepositoryJpaImpl();

			EntityManagerSupport.beginTransaction();
			List<RecommendedActivity> recommended = recommendedActivityRepository
					.findAllByExternalNetwork(network);
			for (RecommendedActivity ra : recommended)
				recommendedActivityRepository.delete(ra);
			EntityManagerSupport.commit();

			for (String group : groups) {
//				List<EngagedActivity> engagedActivities = engagedActivityRepository
//						.findMeanByGroup(group, 10);
//				List<EngagedDocument> engagedDocuments = engagedDocumentRepository
//						.findMeanByGroup(group, 10);
//				for (EngagedActivity engagedActivity : engagedActivities) {
//					EntityManagerSupport.beginTransaction();
//					recommendedActivityRepository
//					.create(new RecommendedActivity(engagedActivity
//							.getActivity(), group));
//					EntityManagerSupport.commit();
//				}
				// these will be activities clicked on from search results
//				for (EngagedDocument engagedDocument : engagedDocuments) {
//					Activity activity = engagedDocument.getActivity();
//					if (activity != null) {
//						EntityManagerSupport.beginTransaction();
//						recommendedActivityRepository
//						.create(new RecommendedActivity(activity, group));
//						EntityManagerSupport.commit();
//					}
//				}

				String key = CacheKeys.generateCacheKeyForExternalNetwork(
						CacheKeys.GroupProperties.RECOMMENDED_ACTIVITIES,
						network);
				userDataModificationCache.put(Long.parseLong(group), key,
						System.currentTimeMillis());

			}
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}

	/***
	 * Returns video content or null if there is no entry for this user in the
	 * cache
	 * 
	 * @param ownerId
	 * @param ifModifiedSince
	 * @return
	 */
	public CollectionVariant<VideoContent> findAllRecommendedVideos(
			Long ownerId, ExternalNetwork network, Long ifModifiedSince) {

		try {

			// get the group map for this user
			List<GroupMembership> groupMembershipList = new GroupMembershipRepositoryJpaImpl()
			.findAllByUserId(ownerId);

			GroupMembership groupMembership = null;
			for (GroupMembership assigned : groupMembershipList) {
				if (assigned.getExternalNetwork() == network) {
					groupMembership = assigned;
					break;
				}
			}
			// if nothing is assigned, then return nothing
			if (groupMembership == null) {
				return null;
			}
			String key = CacheKeys.generateCacheKeyForExternalNetwork(
					CacheKeys.GroupProperties.RECOMMENDED_VIDEOS, network);
			Long lastModified = userDataModificationCache.getLastModified(
					Long.valueOf(groupMembership.getGroupIdentifier()), key,
					ifModifiedSince);

			// If there is no cache entry, there is no data
			if (lastModified == null) {
				return null;
			}

			List<VideoContent> videos = new RecommendedVideoRepositoryJpaImpl()
			.findRecommendedVideosByGroup(groupMembership
					.getGroupIdentifier());

			return new CollectionVariant<VideoContent>(videos, lastModified);

		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}

	private Set<String> assignGroups(ExternalNetwork network) {
//		// track the unique set of group names
//		Set<String> groups = new HashSet<String>();
//
//		GroupMembershipRepository groupMembershipRepository = new GroupMembershipRepositoryJpaImpl();
//
//		// remove all assignments in db by this network
//		EntityManagerSupport.beginTransaction();
//		groupMembershipRepository.deleteByExternalNetwork(network);
//		EntityManagerSupport.commit();
//
//		// query all contacts
//		List<Contact> contacts = new ContactRepositoryJpaImpl()
//		.findByExternalNetwork(network);
//		for (Contact contact : contacts) {
//
//			// load in user we have one
//			User user = new UserRepositoryJpaImpl().getByIdentityId(contact
//					.getExternalIdentity().getIdentityId());
//			UserLocation location = null;
//			if (user != null)
//				location = new UserLocationRepositoryJpaImpl()
//			.findByUserId(user.getUserId());
//
//			// this profile may have both values as null; that's ok for now
//			Profile profile = new Profile(user, location);
//			// only add this contact for the assignment
//			profile.getContacts().add(contact);
//
//			List<GroupMembership> membershipList = recommendationEngine.assign(
//					profile, network);
//			// persist assignments
//			for (GroupMembership membership : membershipList) {
//				EntityManagerSupport.beginTransaction();
//				groupMembershipRepository.create(membership);
//				EntityManagerSupport.commit();
//
//				// add to groups
//				groups.add(membership.getGroupIdentifier());
//			}
//		}
//		return groups;
		return null;
	}

	@SuppressWarnings("unused")
	private void setUpRecommendationEngine(Configuration configuration) {
		if (configuration.getString("recommendation.engine.hadoop.master") == null) {
			log.info("Skipping recommendation engine");
			return;
		}
		recommendationEngine = new RecommendationEngineSparkImpl(configuration);

		// add dimension to global context with all weight values at 1
		recommendationEngine.addDimension(Dimension.createFromEnum("gender",
				Gender.class, 0.0));
		recommendationEngine.addDimension(new Dimension("ageRange", Range
				.between(0.0, 100.0), 0.0));
		recommendationEngine.addDimension(new Dimension("lat", Range.between(
				-90.0, 90.0), 1.0)); // only location important
		recommendationEngine.addDimension(new Dimension("lon", Range.between(
				-180.0, 180.0), 1.0));

		// create fb specific context, with dimensions where
		recommendationEngine
		.addContext(ExternalNetwork.Facebook, configuration);
		recommendationEngine.addDimension(
				Dimension.createFromEnum("gender", Gender.class, 1.0),
				ExternalNetwork.Facebook);
		recommendationEngine.addDimension(
				new Dimension("ageRange", Range.between(0.0, 100.0), 1.0),
				ExternalNetwork.Facebook);
		recommendationEngine.addDimension(new Dimension("lat", Range.between(
				-90.0, 90.0), 0.0)); // location we don't care about because we
		// have location filter
		recommendationEngine.addDimension(new Dimension("lon", Range.between(
				-180.0, 180.0), 0.0));

		// create google specific context, with dimensions where
		recommendationEngine.addContext(ExternalNetwork.Google, configuration);
		recommendationEngine.addDimension(
				Dimension.createFromEnum("gender", Gender.class, 1.0),
				ExternalNetwork.Google);
		recommendationEngine.addDimension(
				new Dimension("ageRange", Range.between(0.0, 100.0), 1.0),
				ExternalNetwork.Google);
		recommendationEngine.addDimension(new Dimension("lat", Range.between(
				-90.0, 90.0), 0.5)); // location so / so
		recommendationEngine.addDimension(new Dimension("lon", Range.between(
				-180.0, 180.0), 0.5));

	}
	
	private void setUpLily(Configuration configuration) {
		namespace = configuration.getString("hbase.sprocket.namespace");
		LilyRepositoryFactory.initialize(configuration);
	}
	
	public void resetInterestsLastModifiedCache() {
		String key = CacheKeys
				.generateCacheKeyForPlaces(CacheKeys.GlobalProperties.INTERESTS);
		dataModificationCache.setLastModified(key, System.currentTimeMillis());
	}

}
