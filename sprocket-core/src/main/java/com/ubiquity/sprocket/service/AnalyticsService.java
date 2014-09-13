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
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.external.repository.cache.CacheKeys;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.Identity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.repository.UserRepository;
import com.ubiquity.identity.repository.UserRepositoryJpaImpl;
import com.ubiquity.location.domain.UserLocation;
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

		engagedItemRepository = new EngagedItemRepositoryJpaImpl();
		engagedActivityRepository = new EngagedActivityRepositoryJpaImpl();
		engagedDocumentRepository = new EngagedDocumentRepositoryJpaImpl();
		engagedVideoRepository = new EngagedVideoRepositoryJpaImpl();
		groupMembershipRepository = new GroupMembershipRepositoryJpaImpl();
		contactRepository = new ContactRepositoryJpaImpl();
		recommendedActivityRepository = new RecommendedActivityRepositoryJpaImpl();
		locationRepository = new UserLocationRepositoryJpaImpl();
		userRepository = new UserRepositoryJpaImpl();

		
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

		List<Activity> activities = recommendedActivityRepository.findRecommendedActivitiesByGroup(groupMembership.getGroupIdentifier(), network);
		return new CollectionVariant<Activity>(activities, lastModified);
	}

	/***
	 * Will create assignments for all contexts
	 */
	public void assignAll() {
		List<User> allUsers = userRepository.findAll();
		for(User user : allUsers) {
			Profile profile = new Profile(user, locationRepository.findByUserId(user.getUserId()));
			profile.getContacts().addAll(contactRepository.findByOwnerId(user.getUserId(), Boolean.TRUE));
			// just assign contexts we have built so far
			for(Contact contact : profile.getContacts()) {
				ExternalNetwork network = ExternalNetwork.getNetworkById(contact.getExternalIdentity().getExternalNetwork());
				if(network == ExternalNetwork.Facebook || network == ExternalNetwork.Google)
					recommendationEngine.assign(profile, network);
			}
		}
	}
	
	/***
	 * Recommends assigns the profile to a group for all networks
	 * 
	 * @param profile
	 * 
	 */
	public void assign(Profile profile) {

		List<GroupMembership> membershipList = recommendationEngine.assign(profile);

		EntityManagerSupport.beginTransaction();
		groupMembershipRepository.deleteByUserId(profile.getUser().getUserId());

		// persisting this for now but we may not need to in the future
		for(GroupMembership membership : membershipList) {
			groupMembershipRepository.create(membership);
		}
		EntityManagerSupport.commit();


	}

	/**
	 * Trains the model for a context
	 * 
	 * @param context
	 */
	private void train(ExternalNetwork context) {
		// check to see if there is any data for this context; if not, return
		if(contactRepository.countAllByExternalNetwork(context) == 0) {
			log.warn("Skipping train on context: {} because no users have signed in yet for it");
			return;
		}
		recommendationEngine.train(context);
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
	 * Removes records from instance space and adds a new batch of users; in next version this loading will be from Hadoop
	 */
	public void refreshProfileRecords() {
		// clear distributed data store for all contexts
		recommendationEngine.clear();
		List<User> users = userRepository.findAll();
		List<Profile> profiles = new LinkedList<Profile>();
		for(User user : users) {
			// create profile with all contacts and the last known location
			
			Profile profile = new Profile(user, locationRepository.findByUserId(user.getUserId()));
			Set<Identity> identities = user.getIdentities();
			for(Identity identity : identities) {
				if(identity instanceof ExternalIdentity) {
					// get own contact by the identity id
					Contact contact = contactRepository.getByExternalIdentityId(identity.getIdentityId());
					if(contact != null) // TODO: find underlying reason why this can happen
						profile.getContacts().add(contact);
				}
			}
			profiles.add(profile);
		}
		recommendationEngine.updateProfileRecords(profiles);
	}
	
	
	/***
	 * Trains each network and assigns all groups, then creates recommended content
	 * 
	 **/
	public void assignGroupsAndCreateRecommendedContent() {

		train(ExternalNetwork.Facebook);
		Set<String> groups = assignGroups(ExternalNetwork.Facebook);
		createRecommendedActivities(groups, ExternalNetwork.Facebook);

		train(ExternalNetwork.Google);
		groups = assignGroups(ExternalNetwork.Google);
		createRecommendedVideos(groups, ExternalNetwork.YouTube);

	}
	
	/***
	 * Creates an assignment (or re-assign) for this and external network 
	 * 
	 * @param contact
	 */
	public void assign(Long userId, ExternalNetwork network) {
		List<Contact> contacts = contactRepository.findByOwnerIdExternalNetwork(userId, network);
		// TODO: do we have multiple contacts? we should not allow this any more
		for(Contact contact :  contacts) {
			assign(contact);
		}
	}
	/***
	 * Creates an assignment (or re-assign) for this contact 
	 * 
	 * @param contact
	 */
	public void assign(Contact contact) {
		User user = contact.getOwner();
		Profile profile = new Profile(user, locationRepository.findByUserId(user.getUserId()));
		profile.getContacts().add(contact);
		ExternalNetwork network = ExternalNetwork.getNetworkById(contact.getExternalIdentity().getExternalNetwork());
		List<GroupMembership> membershipList = recommendationEngine.assign(profile, network);
		
		log.info("assigning emembership: {}", membershipList);
		// save to DB
		EntityManagerSupport.beginTransaction();
		groupMembershipRepository.deleteByExternalNetworkAndUserId(network, user.getUserId());
		for(GroupMembership membership : membershipList) {
			groupMembershipRepository.create(membership);
		}
		EntityManagerSupport.commit();
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
		
		EntityManagerSupport.beginTransaction();
		List<RecommendedActivity> recommended = recommendedActivityRepository.findAllByExternalNetwork(network);
		for(RecommendedActivity ra : recommended)
			recommendedActivityRepository.delete(ra);
		EntityManagerSupport.commit();
		
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
	
	
	private Set<String> assignGroups(ExternalNetwork network) {
		// track the unique set of group names
		Set<String> groups = new HashSet<String>();

		// remove all assignments in db by this network
		EntityManagerSupport.beginTransaction();
		groupMembershipRepository.deleteByExternalNetwork(network);
		EntityManagerSupport.commit();
		
		// query all contacts
		List<Contact> contacts = contactRepository.findByExternalNetwork(network);
		for(Contact contact : contacts) {
			User owner = contact.getOwner();
			if(owner == null) // only users in the system
				continue;
			
			UserLocation location = locationRepository.findByUserId(owner.getUserId());
			// if location is null, then use the location of the contact if possible
			
			
			
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
		recommendationEngine.addDimension(Dimension.createFromEnum("gender", Gender.class, 0.0));
		recommendationEngine.addDimension(new Dimension("ageRange", Range.between(0.0, 100.0), 0.0));
		recommendationEngine.addDimension(new Dimension("lat", Range.between(-90.0, 90.0), 1.0)); // only location important
		recommendationEngine.addDimension(new Dimension("lon", Range.between(-180.0, 180.0), 1.0));

		// create fb specific context, with dimensions where
		recommendationEngine.addContext(ExternalNetwork.Facebook, configuration);
		recommendationEngine.addDimension(Dimension.createFromEnum("gender", Gender.class, 0.1), ExternalNetwork.Facebook);
		recommendationEngine.addDimension(new Dimension("ageRange", Range.between(0.0, 100.0), 1.0), ExternalNetwork.Facebook);
		recommendationEngine.addDimension(new Dimension("lat", Range.between(-90.0, 90.0), 0.0)); // location we don't care about
		recommendationEngine.addDimension(new Dimension("lon", Range.between(-180.0, 180.0), 0.0)); 
		
		// create google specific context, with dimensions where
		recommendationEngine.addContext(ExternalNetwork.Google, configuration);
		recommendationEngine.addDimension(Dimension.createFromEnum("gender", Gender.class, 1.0), ExternalNetwork.Google);
		recommendationEngine.addDimension(new Dimension("ageRange", Range.between(0.0, 100.0), 1.0), ExternalNetwork.Google);
		recommendationEngine.addDimension(new Dimension("lat", Range.between(-90.0, 90.0), 0.5)); // location so / so
		recommendationEngine.addDimension(new Dimension("lon", Range.between(-180.0, 180.0), 0.5)); 
		

	}

}
