package com.ubiquity.social.api;

import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.content.api.VimeoAPITest;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.social.domain.Message;
import com.ubiquity.external.domain.ExternalNetwork;

public class FacebookApiTest {
	
private static Logger log = LoggerFactory.getLogger(VimeoAPITest.class);
	
	private static ExternalIdentity identity;
	
	@BeforeClass
	public static void setUp() throws Exception {
		identity = new ExternalIdentity.Builder().accessToken("CAACEdEose0cBAGpCYAWZBAGypC6aLgD9cE1v5j9UW7ZBLmPKK9e4sZARrkZBws5MZCkIEFiCLhRohlSnobd1ggaYNOTaT3DrAEltfLhH9DtT4gMgZCBWwayOZC9bf1kZCDRtcT5GNkgQRCjipQkMjfo7QDGcjVWZBT2ian5wQsQzZB4Thyv6kOJoa2beYF4GQkpk0ZD").build();
		log.debug("authenticated Facebook with identity {} ", identity);
	}
	
	@Test
	public void testGetMessages() {
		
		SocialAPI facebookAPI = SocialAPIFactory.createProvider(ExternalNetwork.Facebook, ClientPlatform.WEB);
		List<Message> messages = facebookAPI.listMessages(identity);
		Assert.assertFalse(messages.isEmpty());
		// all fb messages will have conversations
		for(Message message : messages) {
			log.debug("message {}", message);
			Assert.assertNotNull(message.getConversation().getConversationIdentifier());
		}
		
	}

}
