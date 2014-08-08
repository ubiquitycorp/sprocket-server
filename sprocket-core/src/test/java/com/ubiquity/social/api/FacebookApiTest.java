package com.ubiquity.social.api;

import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.redis.JedisConnectionFactory;
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
		identity = new ExternalIdentity.Builder().accessToken("CAACEdEose0cBAJ4aTuAzGhp5s7Hb7Sc487AWjOIYNsv7rTkaKfsQ8seo2xaH7RR54HtBz1UIZCdztqcwluBI9MeW0lLGaDZAgxZBrONsCArQCfFDexiNfhfOZC79lKI2jLYnbYN1lIfvuLuecP9nSZAKtssaa1iDQhcNzkui0wUEyNz3Imc5LbZCsJA1IQMdzLQDFLOZAyDPIZBqlayRZCMXZA").build();
		log.debug("authenticated Facebook with identity {} ", identity);
		Configuration configuration = new PropertiesConfiguration(
				"test.properties");
		
		JedisConnectionFactory.initialize(configuration);
		
		SocialAPIFactory.initialize(configuration);
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
