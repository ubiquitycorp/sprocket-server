package com.ubiquity.content.api;

import java.util.List;
import java.util.UUID;

import com.ubiquity.identity.domain.User;

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
import com.ubiquity.identity.factory.TestDeveloperFactory;
import com.ubiquity.identity.factory.TestUserFactory;
import com.ubiquity.identity.repository.DeveloperRepositoryJpaImpl;
import com.ubiquity.integration.api.ContentAPI;
import com.ubiquity.integration.api.ContentAPIFactory;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.VideoContent;
import com.ubiquity.sprocket.service.ServiceFactory;

public class VimeoAPITest {

	private static Logger log = LoggerFactory.getLogger(VimeoAPITest.class);
	private static ExternalNetworkApplication externalApplication;
	private static ExternalIdentity identity;

	@BeforeClass
	public static void setUp() throws Exception {

		User user = TestUserFactory
				.createTestUserWithMinimumRequiredProperties(null);
		identity = new ExternalIdentity.Builder()
				.accessToken("a5f46897abbbd2b83501ea79b4916f44")
				.clientPlatform(ClientPlatform.WEB).inUse(true).user(user)
				.externalNetwork(ExternalNetwork.YouTube.ordinal()).build();
		log.debug("authenticated Vimeo with identity {} ", identity);

		Configuration configuration = new PropertiesConfiguration(
				"test.properties");

		JedisConnectionFactory.initialize(configuration);
		ContentAPIFactory.initialize(configuration);
		ServiceFactory.initialize(configuration, null);

		Developer developer = TestDeveloperFactory
				.createTestDeveloperWithMinimumRequiredProperties();

		EntityManagerSupport.beginTransaction();
		new DeveloperRepositoryJpaImpl().create(developer);
		EntityManagerSupport.commit();

		Application application = ServiceFactory.getApplicationService()
				.createDefaultAppIFNotExsists(developer,
						UUID.randomUUID().toString(),
						UUID.randomUUID().toString());

		externalApplication = ServiceFactory.getApplicationService()
				.getExAppByAppIdAndExternalNetworkAndClientPlatform(
						application.getAppId(), identity.getExternalNetwork(),
						identity.getClientPlatform());
	}

	@Test
	public void testFindVideosByExternalIdentity() {
		ContentAPI contentApi = ContentAPIFactory.createProvider(
				ExternalNetwork.getNetworkById(identity.getExternalNetwork()),
				identity.getClientPlatform(), externalApplication);
		List<VideoContent> videos = contentApi.listVideos(identity);
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
		Assert.assertEquals(25, videos.size()); // just test the size returns
												// for now
		for (VideoContent video : videos)
			log.debug("video: {}", video);
	}

}
