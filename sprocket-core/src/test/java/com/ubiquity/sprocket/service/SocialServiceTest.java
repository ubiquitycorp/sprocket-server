package com.ubiquity.sprocket.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.niobium.repository.CollectionVariant;
import com.niobium.repository.jpa.EntityManagerSupport;
import com.niobium.repository.redis.JedisConnectionFactory;
import com.ubiquity.identity.domain.Application;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.Developer;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.ExternalNetworkApplication;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.domain.factory.UserFactory;
import com.ubiquity.identity.factory.TestDeveloperFactory;
import com.ubiquity.identity.repository.DeveloperRepositoryJpaImpl;
import com.ubiquity.integration.api.SocialAPIFactory;
import com.ubiquity.integration.domain.Activity;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.Message;
import com.ubiquity.integration.domain.SyncStatusMessage;
import com.ubiquity.integration.factory.TestPlaceFactory;
import com.ubiquity.integration.service.SocialService;
import com.ubiquity.location.domain.Location;
import com.ubiquity.location.domain.Place;
import com.ubiquity.location.domain.UserLocation;

public class SocialServiceTest {

	private static SocialService socialService;
	private static ExternalIdentity identity;
	private static ExternalNetworkApplication externalNetworkApplication;
	private static User user;

	@BeforeClass
	public static void setUp() throws Exception {
		Configuration configuration = new PropertiesConfiguration(
				"test.properties");
		ServiceFactory.initialize(configuration, null);
		SocialAPIFactory.initialize(configuration);
		JedisConnectionFactory.initialize(configuration);
		socialService = ServiceFactory.getSocialService();
		Developer developer = TestDeveloperFactory
				.createTestDeveloperWithMinimumRequiredProperties();
		
		EntityManagerSupport.beginTransaction();
		new DeveloperRepositoryJpaImpl().create(developer);
		EntityManagerSupport.commit();
		
		Application application = ServiceFactory.getApplicationService()
				.createDefaultAppIFNotExsists(developer,UUID.randomUUID().toString(),UUID.randomUUID().toString());
		
		user = UserFactory
				.createUserWithRequiredFieldsUsingApplication(UUID.randomUUID()
						.toString(), ClientPlatform.WEB, true, application);
		ServiceFactory.getUserService().create(user);
		
		externalNetworkApplication = ServiceFactory.getApplicationService()
				.getExAppByAppIdAndExternalNetworkAndClientPlatform(application.getAppId(),
						ExternalNetwork.Facebook.ordinal(), ClientPlatform.WEB);

		List<ExternalIdentity> externalIdentities = ServiceFactory
				.getExternalIdentityService().createOrUpdateExternalIdentity(
						user, UUID.randomUUID().toString(),
						UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), ClientPlatform.WEB,
						ExternalNetwork.Facebook, 3600L, true,
						externalNetworkApplication);
		identity = externalIdentities.get(0);

	}

	@Test
	public void SyncMessages() {
		// sync Facebook messages from sprocket mock network
		Map<String, SyncStatusMessage> processedMessages = new HashMap<String, SyncStatusMessage>();
		ExternalNetwork externalNetwork = ExternalNetwork
				.getNetworkById(identity.getExternalNetwork());
		List<Message> messages = socialService.syncMessages(identity,
				externalNetwork, null, processedMessages,
				externalNetworkApplication);
		Assert.assertFalse(messages.isEmpty());
		// find Contacts for user
		CollectionVariant<Message> messagesCollections = socialService
				.findMessagesByOwnerIdAndSocialNetwork(user.getUserId(),
						externalNetwork, 1L, false);
		Assert.assertFalse(messagesCollections.getCollection().isEmpty());
	}

	@Test
	public void SyncActivities() {
		// sync Facebook activities from sprocket mock network
		ExternalNetwork externalNetwork = ExternalNetwork
				.getNetworkById(identity.getExternalNetwork());
		List<Activity> activities = socialService.syncActivities(identity,
				externalNetwork, externalNetworkApplication);
		Assert.assertFalse(activities.isEmpty());
		// find Contacts for user
		CollectionVariant<Activity> activitiesCollections = socialService
				.findActivityByOwnerIdAndSocialNetwork(user.getUserId(),
						externalNetwork, 1L);
		Assert.assertFalse(activitiesCollections.getCollection().isEmpty());
	}

	@Test
	public void AddUserLocationAndSyncLocalActivities() {
		// sync Local Facebook Local activities from sprocket mock network
		ExternalNetwork externalNetwork = ExternalNetwork
				.getNetworkById(identity.getExternalNetwork());
		Place losAngeles = TestPlaceFactory
				.createLosAngelesAndNeighborhoodsAndBusiness();
		Place neighbourhood = ((Place) losAngeles.getChildren().toArray()[0]);
		ServiceFactory.getLocationService().create(losAngeles);
		UserLocation userLocation = new UserLocation.Builder()
				.user(user)
				.location(
						new Location.Builder()
								.latitude(
										losAngeles.getBoundingBox().getCenter()
												.getLongitude())
								.longitude(
										losAngeles.getBoundingBox().getCenter()
												.getLongitude())
								.altitude(
										losAngeles.getBoundingBox().getCenter()
												.getAltitude()).build())
				.timestamp(System.currentTimeMillis())
				.lastUpdated(System.currentTimeMillis())
				.horizontalAccuracy(new Random().nextDouble())
				.verticalAccuracy(new Random().nextDouble()).build();
		// Get nearest place to the new user's location
		Place nearestPlace = ServiceFactory.getLocationService()
				.getClosestNeighborhoodIsWithin(userLocation.getLocation());
		userLocation.setNearestPlace(nearestPlace);

		Assert.assertEquals(neighbourhood.getPlaceId(),
				nearestPlace.getPlaceId());
		// this will update the user's location in the SQL data store
		ServiceFactory.getLocationService().updateLocation(userLocation);

		List<Activity> activities = socialService.syncLocalNewsFeed(identity,
				externalNetwork, false, externalNetworkApplication);
		Assert.assertFalse(activities.isEmpty());
		// find Contacts for user
		CollectionVariant<Activity> activitiesCollections = socialService
				.findActivityByPlaceIdAndSocialNetwork(
						neighbourhood.getPlaceId(), externalNetwork, 1L, false);
		Assert.assertFalse(activitiesCollections.getCollection().isEmpty());
	}

}
