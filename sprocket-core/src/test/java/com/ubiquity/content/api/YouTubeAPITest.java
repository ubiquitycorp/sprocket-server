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

import com.niobium.repository.redis.JedisConnectionFactory;
import com.ubiquity.content.domain.VideoContent;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.sprocket.service.SearchService;
import com.ubiquity.sprocket.service.ServiceFactory;

public class YouTubeAPITest {
	
	private static Logger log = LoggerFactory.getLogger(YouTubeAPITest.class);
	
	private static ExternalIdentity identity;
	
	@BeforeClass
	public static void setUp() throws Exception {
		identity = new ExternalIdentity.Builder().identifier(UUID.randomUUID().toString())
				.accessToken("ya29.gABsJPzir1n64pM3V2afo7TfbVUl-3V6zW_B5k9xlcStyAV_vl6Mlqdp")
				.clientPlatform(ClientPlatform.WEB).build();
		log.debug("authenticated YouTube with identity {} ", identity);
		
		// intialize services
		Configuration config = new PropertiesConfiguration("test.properties");
		Configuration errorsConfig = new PropertiesConfiguration("messages.properties");
		JedisConnectionFactory.initialize(config);
		ContentAPIFactory.initialize(config);
		ServiceFactory.initialize(config, errorsConfig);
	}

	
	@Test
	public void testFindVideosByExternalIdentityAndPagination() {
		SearchService searchService=ServiceFactory.getSearchService(); 
		List<VideoContent> videos = searchService.searchLiveVedios("Karate",identity , ExternalNetwork.YouTube, 1);
		Assert.assertTrue(videos.size() == 25);
		
		VideoContent firstFromPageOne = videos.get(0);
		
		// should be completeley different set
		videos = searchService.searchLiveVedios("Karate",identity , ExternalNetwork.YouTube, 2);
		// compare the first of each
		VideoContent firstFromPageTwo = videos.get(0);
		Assert.assertFalse(firstFromPageOne.getVideo().getItemKey().equals(firstFromPageTwo.getVideo().getItemKey()));
		
		// get first page again, make sure it's the same as the reference to the first item when we first retrieved the first page
		videos = searchService.searchLiveVedios("Karate",identity , ExternalNetwork.YouTube, 1);
		Assert.assertTrue(firstFromPageOne.getVideo().getItemKey().equals(videos.get(0).getVideo().getItemKey()));
		
		// try reading ahead 2 pages, which is not supported
		boolean exceptionThrown = Boolean.FALSE;
		try {
			videos = searchService.searchLiveVedios("Karate",identity , ExternalNetwork.YouTube, 3);
		} catch (IllegalArgumentException e) {
			exceptionThrown = Boolean.TRUE;
		}
		Assert.assertTrue(exceptionThrown);
		
	}

	
	

}
