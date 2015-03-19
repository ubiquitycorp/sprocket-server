package com.ubiquity.sprocket.service;

import java.util.List;
import java.util.UUID;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.niobium.repository.CollectionVariant;
import com.ubiquity.identity.domain.Application;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.ExternalNetworkApplication;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.domain.factory.UserFactory;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.VideoContent;
import com.ubiquity.integration.service.ContentService;

public class ContentServiceTest {
	
	private static ContentService contentService;
	private static ExternalIdentity identity;
	private static ExternalNetworkApplication externalNetworkApplication;
	private static User user;

	@BeforeClass
	public static void setUp() throws Exception {
		Configuration configuration = new PropertiesConfiguration(
				"test.properties");
		
		contentService = ServiceFactory.getContentService();
		user = UserFactory
				.createUserWithRequiredFieldsUsingApplication(UUID.randomUUID()
						.toString(), ClientPlatform.WEB, true, null);
		
		ServiceFactory.getUserService().create(user);
		
		ServiceFactory.initialize(configuration, null); 
		
		Application application =  ServiceFactory.getApplicationService().loadApplicationFromConfiguration();
		externalNetworkApplication = ServiceFactory.getApplicationService().getExAppByExternalNetworkAndClientPlatform(application,
				ExternalNetwork.Facebook.ordinal(),
				ClientPlatform.WEB);
		List<ExternalIdentity> externalIdentities = ServiceFactory
				.getExternalIdentityService().createOrUpdateExternalIdentity(
						user, UUID.randomUUID().toString(),
						UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), ClientPlatform.WEB,
						ExternalNetwork.Google, 3600L, true, externalNetworkApplication);
		identity = externalIdentities.get(1);

	}

	@Test
	public void SyncVideos() {
		// sync Facebook friends from sprocket mock network
		ExternalNetwork externalNetwork = ExternalNetwork
				.getNetworkById(identity.getExternalNetwork());
		List<VideoContent> videos = contentService.sync(identity,
				externalNetwork,externalNetworkApplication);
		Assert.assertFalse(videos.isEmpty());
		// find Contacts for user
		CollectionVariant<VideoContent> videosCollections = contentService
				.findAllVideosByOwnerIdAndContentNetwork(user.getUserId(),
						externalNetwork, 1L, false);
		Assert.assertFalse(videosCollections.getCollection().isEmpty());
	}

}
