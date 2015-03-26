package com.ubiquity.content.api;

import java.util.List;
import java.util.UUID;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.niobium.repository.redis.JedisConnectionFactory;
import com.ubiquity.identity.domain.Application;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.Developer;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.ExternalNetworkApplication;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.factory.TestDeveloperFactory;
import com.ubiquity.identity.factory.TestUserFactory;
import com.ubiquity.identity.repository.DeveloperRepositoryJpaImpl;
import com.ubiquity.integration.api.ContentAPI;
import com.ubiquity.integration.api.ContentAPIFactory;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.VideoContent;
import com.ubiquity.sprocket.service.ServiceFactory;

public class MockNetworkContentAPITest {

	private static Logger log = LoggerFactory
			.getLogger(MockNetworkContentAPITest.class);
	private static ExternalNetworkApplication externalApplication;
	private static ExternalIdentity identity;
	private static int videosLimitPerPage;
	private static int numOfPages;

	@BeforeClass
	public static void setUp() throws Exception {
		Configuration configuration = new PropertiesConfiguration(
				"test.properties");
		ServiceFactory.initialize(configuration, null);
		ContentAPIFactory.initialize(configuration);
		JedisConnectionFactory.initialize(configuration);
		
		Developer developer = TestDeveloperFactory
				.createTestDeveloperWithMinimumRequiredProperties();
		
		EntityManagerSupport.beginTransaction();
		new DeveloperRepositoryJpaImpl().create(developer);
		EntityManagerSupport.commit();
		
		Application application = ServiceFactory.getApplicationService()
				.createDefaultAppIFNotExsists(developer,UUID.randomUUID().toString(),UUID.randomUUID().toString());
		
		User user = TestUserFactory
				.createTestUserWithMinimumRequiredProperties(null);
		identity = new ExternalIdentity.Builder()
				.clientPlatform(ClientPlatform.Android).inUse(true).user(user)
				.accessToken(UUID.randomUUID().toString())
				.externalNetwork(ExternalNetwork.ContentMockNetwork.ordinal())
				.build();
		
		externalApplication = ServiceFactory.getApplicationService()
				.getExAppByAppIdAndExternalNetworkAndClientPlatform(application.getAppId(),
						identity.getExternalNetwork(), identity.getClientPlatform());
		videosLimitPerPage = configuration.getInt("videos.limit.perpage");
		numOfPages = configuration.getInt("linkedin.activity.limit");
	}

	@Test
	public void testFindVideosByExternalIdentity() {
		ContentAPI contentApi = ContentAPIFactory.createProvider(
				ExternalNetwork.getNetworkById(identity.getExternalNetwork()),
				identity.getClientPlatform(), externalApplication);
		
		List<VideoContent> videos = contentApi.listVideos(identity, videosLimitPerPage, numOfPages);
		for (VideoContent video : videos)
			log.debug("video: {}", video);
	}

	@Test
	public void testSearchVideos() {
		ContentAPI contentApi = ContentAPIFactory.createProvider(
				ExternalNetwork.getNetworkById(identity.getExternalNetwork()),
				identity.getClientPlatform(), externalApplication);
		List<VideoContent> videos = contentApi.searchVideos("karate", 1, 25,
				identity);
		Assert.assertTrue(videos.size() == 25); // just test the size returns
												// for now
		for (VideoContent video : videos)
			log.debug("video: {}", video);
	}
}
