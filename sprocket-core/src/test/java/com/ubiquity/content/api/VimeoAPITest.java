package com.ubiquity.content.api;

import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.redis.JedisConnectionFactory;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.ExternalNetworkApplication;
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

		identity = new ExternalIdentity.Builder()
				.accessToken("a5f46897abbbd2b83501ea79b4916f44")
				.clientPlatform(ClientPlatform.WEB)
				.externalNetwork(ExternalNetwork.YouTube.ordinal())
				.build();
		log.debug("authenticated Vimeo with identity {} ", identity);

		Configuration configuration = new PropertiesConfiguration(
				"test.properties");

		JedisConnectionFactory.initialize(configuration);
		ContentAPIFactory.initialize(configuration);
		ServiceFactory.initialize(configuration, null); 
		externalApplication = ServiceFactory.getApplicationService().getDefaultExternalApplication(
				identity.getExternalNetwork(), identity.getClientPlatform());
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
		Assert.assertTrue(videos.size() == 25); // just test the size returns
												// for now
		for (VideoContent video : videos)
			log.debug("video: {}", video);
	}

}
