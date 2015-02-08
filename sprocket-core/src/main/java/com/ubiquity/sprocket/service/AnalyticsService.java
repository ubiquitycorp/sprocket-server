package com.ubiquity.sprocket.service;

import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.CollectionVariant;
import com.niobium.repository.cache.DataCacheKeys;
import com.niobium.repository.cache.DataModificationCache;
import com.niobium.repository.cache.DataModificationCacheRedisImpl;
import com.niobium.repository.cache.UserDataModificationCache;
import com.niobium.repository.cache.UserDataModificationCacheRedisImpl;
import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.identity.domain.User;
import com.ubiquity.integration.domain.Activity;
import com.ubiquity.integration.domain.AdminInterest;
import com.ubiquity.integration.domain.Contact;
import com.ubiquity.integration.domain.ExternalInterest;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.Interest;
import com.ubiquity.integration.domain.UnmappedInterest;
import com.ubiquity.integration.domain.VideoContent;
import com.ubiquity.integration.repository.ExternalInterestRepository;
import com.ubiquity.integration.repository.ExternalInterestRepositoryJpaImpl;
import com.ubiquity.integration.repository.InterestRepository;
import com.ubiquity.integration.repository.InterestRepositoryJpaImpl;
import com.ubiquity.integration.repository.UnmappedInterestRepository;
import com.ubiquity.integration.repository.UnmappedInterestRepositoryJpaImpl;
import com.ubiquity.integration.repository.cache.CacheKeys;
import com.ubiquity.sprocket.domain.Content;
import com.ubiquity.sprocket.domain.GroupMembership;
import com.ubiquity.sprocket.domain.Profile;
import com.ubiquity.sprocket.domain.ProfilePK;
import com.ubiquity.sprocket.domain.UserEngagement;
import com.ubiquity.sprocket.domain.factory.ProfileFactory;
import com.ubiquity.sprocket.repository.ContentRepository;
import com.ubiquity.sprocket.repository.ContentRepositoryHBaseImpl;
import com.ubiquity.sprocket.repository.GroupMembershipRepository;
import com.ubiquity.sprocket.repository.GroupMembershipRepositoryJpaImpl;
import com.ubiquity.sprocket.repository.ProfileRepository;
import com.ubiquity.sprocket.repository.ProfileRepositoryHBaseImpl;
import com.ubiquity.sprocket.repository.RecommendedActivityRepository;
import com.ubiquity.sprocket.repository.RecommendedActivityRepositoryJpaImpl;
import com.ubiquity.sprocket.repository.RecommendedVideoRepositoryJpaImpl;

/***
 * Service for executing tracking engagement, assigning contacts to groups, and
 * recommending content
 * 
 * @author chris
 * 
 */
public class AnalyticsService {

	@SuppressWarnings("unused")
	private Logger log = LoggerFactory.getLogger(getClass());
	private UserDataModificationCache userDataModificationCache;
	private DataModificationCache dataModificationCache;
	
	private ProfileRepository profileRepository;

	/***
	 * Sets up repositories and data modification cache
	 * 
	 * @param configuration
	 */
	public AnalyticsService(Configuration configuration) {
		
		userDataModificationCache = new UserDataModificationCacheRedisImpl(
				configuration
						.getInt(DataCacheKeys.Databases.ENDPOINT_MODIFICATION_DATABASE_GROUP));
		dataModificationCache = new DataModificationCacheRedisImpl(
				configuration
						.getInt(DataCacheKeys.Databases.ENDPOINT_MODIFICATION_DATABASE_GENERAL));
		
		profileRepository = new ProfileRepositoryHBaseImpl();
		String key = CacheKeys
				.generateCacheKeyForPlaces(CacheKeys.GlobalProperties.INTERESTS);
		Long lastModified = dataModificationCache.getLastModified(key, 0L);

		// If there is no cache entry
		if (lastModified == null) {
			if (new InterestRepositoryJpaImpl().countAllInterests() > 0) {
				resetInterestsLastModifiedCache();
			}
		}
	}


	/**
	 * Will create or update a profile if it does not exist, as well as a profile for the
	 * contact
	 * 
	 * @param user A registered user
	 * @param contact A contact
	 */
	public void createOrUpdateProfileIdentity(User user, Contact contact) {
		Profile profile = ProfileFactory.createProfile(user.getUserId(), contact);
		profileRepository.create(profile);
	}
	
	/**
	 * Returns the central Sprocket profile for a user
	 * 
	 * @param user
	 * @return
	 */
	public Profile getProfile(User user) {
		return profileRepository.read(new ProfilePK(user.getUserId()));
	}
	
	/**
	 * Persists a Sprocket profile. This will update the profile with any properties that have
	 * changed or have been added if the profile already exists.
	 * 
	 * @param profile
	 */
	public void createProfile(Profile profile) {
		profileRepository.create(profile);
	}
	
	/***
	 * Creates a Sprocket profile for this user. This will update the profile with any properties that have
	 * changed or have been added if the profile already exists.
	 * 
	 * @param user
	 */
	public void createProfile(User user) {
		ProfileRepository profileRepository = new ProfileRepositoryHBaseImpl();
		Profile profile = ProfileFactory.createProfile(user);
		profileRepository.create(profile);
	}
		
	/**
	 * Tracks the search term for this user and what network, if any, they were searching over
	 * 
	 * @param searchTerm
	 * @param userId
	 * @param network
	 */
	public void track(String searchTerm, Long userId, ExternalNetwork network) {
		ProfileRepository profileRepository = new ProfileRepositoryHBaseImpl();
		profileRepository.addToSearchHistory(new ProfilePK(network, userId), searchTerm);
	}
	
	/**
	 * Tracks the search term for this user
	 * 
	 * @param searchTerm
	 * @param userId
	 */
	public void track(String searchTerm, Long userId) {
		ProfileRepository profileRepository = new ProfileRepositoryHBaseImpl();
		profileRepository.addToSearchHistory(new ProfilePK(userId), searchTerm);
	}
	
	/**
	 * Tracks the content a user has engaged with, and when, and what the membership identifier was at the time of
	 * engagement
	 * 
	 * @param content
	 * @param userId
	 * @param timestamp
	 * @param groupMembership
	 */
	public void track(Content content, Long userId, Long timestamp, String groupMembership) {
		ContentRepository contentRepository = new ContentRepositoryHBaseImpl();
		contentRepository.create(content); // should update existing records
		contentRepository.addUserEngagement(new UserEngagement.Builder()
			.contentId(content.getContentId())
			.groupMembership(groupMembership)
			.userId(userId)
			.timestamp(timestamp)
			.build());
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

		Long lastModified = dataModificationCache.getLastModified(
				CacheKeys.GlobalProperties.INTERESTS, ifModifiedSince);

		// If there is no cache entry, there is no data
		if (lastModified == null) {
			return null;
		}

		try {
			return new CollectionVariant<Interest>(
					new InterestRepositoryJpaImpl().findTopLevel(),
					lastModified);
		} finally {
			EntityManagerSupport.closeEntityManager();
		}

	}

	public CollectionVariant<Interest> findInterestsByExternalNetworkId(
			ExternalNetwork network, Long ifModifiedSince) {

		// Long lastModified =
		// dataModificationCache.getLastModified(CacheKeys.GlobalProperties.INTERESTS,
		// ifModifiedSince);

		// If there is no cache entry, there is no data
		// if (lastModified == null) {
		// return null;
		// }

		try {
			return new CollectionVariant<Interest>(
					new ExternalInterestRepositoryJpaImpl()
							.getByDistinctInterestByExternalNetwork(network),
					null);
		} finally {
			EntityManagerSupport.closeEntityManager();
		}

	}

	public CollectionVariant<ExternalInterest> findExternalInterestsByExternalNetworkId(
			ExternalNetwork network) {
		try {
			return new CollectionVariant<ExternalInterest>(
					new ExternalInterestRepositoryJpaImpl()
							.getExternalInterestByExternalNetwork(network),
					null);
		} finally {
			EntityManagerSupport.closeEntityManager();
		}

	}

	public CollectionVariant<UnmappedInterest> findUnmappedInterestByExternalNetworkId(
			ExternalNetwork network) {
		try {
			return new CollectionVariant<UnmappedInterest>(
					new UnmappedInterestRepositoryJpaImpl()
							.getUnmappedInterestByExternalNetwork(network),
					null);
		} finally {
			EntityManagerSupport.closeEntityManager();
		}

	}

	public Boolean updateAdminInterests(List<AdminInterest> deletedInterests,
			List<AdminInterest> newInterests) {
		try {
			EntityManagerSupport.beginTransaction();
			 InterestRepository interestRepo = new InterestRepositoryJpaImpl();
			ExternalInterestRepository externalInterestRepo = new ExternalInterestRepositoryJpaImpl();
			UnmappedInterestRepository unmappedInterestRepo = new UnmappedInterestRepositoryJpaImpl();
			// delete list
			for (AdminInterest adminInterest : deletedInterests) {
				if (adminInterest instanceof Interest) {

					// interestRepo.delete((Interest)adminInterest);
				} else if (adminInterest instanceof UnmappedInterest) {
					UnmappedInterest unmappedInterest = unmappedInterestRepo
							.read(((UnmappedInterest) adminInterest)
									.getUnmappedId());
					if (unmappedInterest != null)
						unmappedInterestRepo.delete(unmappedInterest);
				} else if (adminInterest instanceof ExternalInterest) {
					ExternalInterest externalInterest = externalInterestRepo
							.read(((ExternalInterest) adminInterest)
									.getExternalInterestId());
					if (externalInterest != null)
						externalInterestRepo
								.delete((ExternalInterest) externalInterest);
				}
			}
			// insert list
			for (AdminInterest adminInterest : newInterests) {
				if (adminInterest instanceof Interest) {

					// interestRepo.delete((Interest)adminInterest);
				} else if (adminInterest instanceof UnmappedInterest) {
					UnmappedInterest unmappedinterest = (UnmappedInterest) adminInterest;
					UnmappedInterest presistdUnmappedinterest = unmappedInterestRepo
							.getByNameAndExternalNetwork(
									unmappedinterest.getName(),
									unmappedinterest.getExternalNetwork());
					if (presistdUnmappedinterest == null)
						unmappedInterestRepo.create(unmappedinterest);
				} else if (adminInterest instanceof ExternalInterest) {
					ExternalInterest externalInterest = (ExternalInterest) adminInterest;
					ExternalInterest presistdExternalInterest = externalInterestRepo
							.getByNameAndExternalNetworkAndInterestId(
									externalInterest.getName(),
									externalInterest.getExternalNetwork(),
									externalInterest.getExternalInterestId());
					
					if (presistdExternalInterest == null){
						Interest interest = interestRepo.read(externalInterest.getInterest_id());
						externalInterest.setInterest(interest);
						externalInterestRepo.create(externalInterest);
					}
				}
			}
			EntityManagerSupport.commit();
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
		return true;

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

	public void resetInterestsLastModifiedCache() {
		String key = CacheKeys
				.generateCacheKeyForPlaces(CacheKeys.GlobalProperties.INTERESTS);
		dataModificationCache.setLastModified(key, System.currentTimeMillis());
	}

}
