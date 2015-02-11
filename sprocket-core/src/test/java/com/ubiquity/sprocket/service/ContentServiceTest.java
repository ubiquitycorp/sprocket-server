package com.ubiquity.sprocket.service;

import java.util.List;
import java.util.UUID;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.niobium.repository.CollectionVariant;
import com.niobium.repository.redis.JedisConnectionFactory;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.factory.TestUserFactory;
import com.ubiquity.integration.api.SocialAPIFactory;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.VideoContent;
import com.ubiquity.integration.service.ContentService;

public class ContentServiceTest {
	
	private static ContentService contentService;
	private static ExternalIdentity identity;
	private static User user;

	@BeforeClass
	public static void setUp() throws Exception {
		Configuration configuration = new PropertiesConfiguration(
				"test.properties");
		// Start a connection pool to redis
		JedisConnectionFactory.initialize(configuration);
		//HBaseConnectionFactory.initialize(configuration);
		ServiceFactory.initialize(configuration, null);
		SocialAPIFactory.initialize(configuration);
		
		contentService = ServiceFactory.getContentService();
		user = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		ServiceFactory.getUserService().create(user);
		List<ExternalIdentity> externalIdentities = ServiceFactory
				.getExternalIdentityService().createOrUpdateExternalIdentity(
						user, UUID.randomUUID().toString(),
						UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), ClientPlatform.WEB,
						ExternalNetwork.Google, 3600L);
		identity = externalIdentities.get(1);

	}

	@Test
	public void SyncVideos() {
		// sync Facebook friends from sprocket mock network
		ExternalNetwork externalNetwork = ExternalNetwork
				.getNetworkById(identity.getExternalNetwork());
		List<VideoContent> videos = contentService.sync(identity,
				externalNetwork);
		Assert.assertFalse(videos.isEmpty());
		// find Contacts for user
		CollectionVariant<VideoContent> videosCollections = contentService
				.findAllVideosByOwnerIdAndContentNetwork(user.getUserId(),
						externalNetwork, 1L, false);
		Assert.assertFalse(videosCollections.getCollection().isEmpty());
	}

}
