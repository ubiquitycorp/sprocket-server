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

public class YouTubeAPITest {
	
	private static Logger log = LoggerFactory.getLogger(YouTubeAPITest.class);
	
	private static ExternalIdentity identity;
	
	@BeforeClass
	public static void setUp() throws Exception {
		identity = new ExternalIdentity.Builder().identifier(UUID.randomUUID().toString()).accessToken("ya29.SACmu4A3pfy-EhwAAAAZoPKNcOtbhrq-fDPXXIJ7Zl6AvtyMJPhuD-Tlu6M2rw").build();
		log.debug("authenticated YouTube with identity {} ", identity);
		
		// intialize services
		Configuration config = new PropertiesConfiguration("test.properties");
		JedisConnectionFactory.initialize(config);
		ContentAPIFactory.initialize(config);
		
		
	}
	
	@Test
	public void testFindVideosByExternalIdentityAndPagination() {
		ContentAPI contentApi = ContentAPIFactory.createProvider(ExternalNetwork.YouTube, ClientPlatform.WEB);
		List<VideoContent> videos = contentApi.searchVideos("Karate", 1, 25, identity);
		Assert.assertTrue(videos.size() == 25);
		
		VideoContent firstFromPageOne = videos.get(0);
		
		// should be completeley different set
		videos = contentApi.searchVideos("Karate", 2, 25, identity);
		// compare the first of each
		VideoContent firstFromPageTwo = videos.get(0);
		Assert.assertFalse(firstFromPageOne.getVideo().getItemKey().equals(firstFromPageTwo.getVideo().getItemKey()));
		
		// get first page again, make sure it's the same as the reference to the first item when we first retrieved the first page
		videos = contentApi.searchVideos("Karate", 1, 25, identity);
		Assert.assertTrue(firstFromPageOne.getVideo().getItemKey().equals(videos.get(0).getVideo().getItemKey()));
		
		// try reading ahead 2 pages, which isnot supported
		boolean exceptionThrown = Boolean.FALSE;
		try {
			videos = contentApi.searchVideos("Karate", 3, 25, identity);
		} catch (IllegalArgumentException e) {
			exceptionThrown = Boolean.TRUE;
		}
		Assert.assertTrue(exceptionThrown);
		
	}

	
	

}
