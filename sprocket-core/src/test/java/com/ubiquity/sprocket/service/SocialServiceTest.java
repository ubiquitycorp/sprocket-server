package com.ubiquity.sprocket.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.niobium.repository.CollectionVariant;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.factory.TestUserFactory;
import com.ubiquity.integration.domain.Activity;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.Message;
import com.ubiquity.integration.domain.SyncStatusMessage;
import com.ubiquity.integration.factory.TestPlaceFactory;
import com.ubiquity.integration.service.SocialService;
import com.ubiquity.location.domain.Place;

public class SocialServiceTest {

	private static SocialService socialService;
	private static ExternalIdentity identity;
	private static User user;

	@BeforeClass
	public static void setUp() throws Exception {

		socialService = ServiceFactory.getSocialService();
		user = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		ServiceFactory.getUserService().create(user);
		List<ExternalIdentity> externalIdentities = ServiceFactory
				.getExternalIdentityService().createOrUpdateExternalIdentity(
						user, UUID.randomUUID().toString(),
						UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), ClientPlatform.WEB,
						ExternalNetwork.Facebook, 3600L);
		identity = externalIdentities.get(0);

	}
	
	@Test
	public void SyncMessages() {
		// sync Facebook friends from sprocket mock network 
		Map<String, SyncStatusMessage> processedMessages = new HashMap<String, SyncStatusMessage>();
		ExternalNetwork externalNetwork = ExternalNetwork.getNetworkById(identity.getExternalNetwork());
		List<Message> messages =socialService.syncMessages(identity, externalNetwork, null, processedMessages);
		Assert.assertFalse(messages.isEmpty());
		// find Contacts for user
		CollectionVariant<Message> messagesCollections =socialService.findMessagesByOwnerIdAndSocialNetwork(user.getUserId(), externalNetwork, 1L, false);
		Assert.assertFalse(messagesCollections.getCollection().isEmpty());
	}
	
	@Test
	public void SyncActivities() {
		// sync Facebook friends from sprocket mock network 
		ExternalNetwork externalNetwork = ExternalNetwork.getNetworkById(identity.getExternalNetwork());
		List<Activity> activities =socialService.syncActivities(identity, externalNetwork);
		Assert.assertFalse(activities.isEmpty());
		// find Contacts for user
		CollectionVariant<Activity> activitiesCollections =socialService.findActivityByOwnerIdAndSocialNetwork(user.getUserId(), externalNetwork, 1L, false);
		Assert.assertFalse(activitiesCollections.getCollection().isEmpty());
	}
	
}
