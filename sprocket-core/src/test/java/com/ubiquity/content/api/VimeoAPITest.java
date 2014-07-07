package com.ubiquity.content.api;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.content.domain.ContentNetwork;
import com.ubiquity.content.domain.VideoContent;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.ExternalIdentity;

public class VimeoAPITest {
	
	private static Logger log = LoggerFactory.getLogger(VimeoAPITest.class);
	
	private static ExternalIdentity identity;
	
	@BeforeClass
	public static void setUp() throws Exception {
		identity = new ExternalIdentity.Builder().accessToken("a5f46897abbbd2b83501ea79b4916f44").build();
		log.debug("authenticated Vimeo with identity {} ", identity);
	}
	
	@Test
	public void testFindVideosByExternalIdentity() {
		ContentAPI contentApi = ContentAPIFactory.createProvider(ContentNetwork.Vimeo, ClientPlatform.WEB);
		List<VideoContent> videos = contentApi.findVideosByExternalIdentity(identity);
		for(VideoContent video : videos) 
			log.debug("video: {}", video);
	}

	
	

}
