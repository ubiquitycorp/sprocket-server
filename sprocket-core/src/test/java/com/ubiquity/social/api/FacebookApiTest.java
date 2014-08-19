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
		identity = new ExternalIdentity.Builder().accessToken("CAACEdEose0cBANZAbbx6aoU1euJGZCVZCC8kmPpZC2jKAWurSuPcL6uvSh9WxYChg7JLlFemDjojsHzDWH0GSqPkj2RHvWZBDbSczDhu5JcrIQDwZCgaX7SYa7HWhxANhBnbcVZAB849GfVZAaqhhbMJK8kpHEZAFyuZAs6K3Dt4jcGxIXr8BcY2ZBRu9YIIgtKD0uUFyMUGH48mahBzOWXx8lQ").build();
		log.debug("authenticated Facebook with identity {} ", identity);
		Configuration configuration = new PropertiesConfiguration(
				"test.properties");
		
		JedisConnectionFactory.initialize(configuration);
		
		SocialAPIFactory.initialize(configuration);
	}
	
	@Test
	public void testGetMessages() {
		
		SocialAPI facebookAPI = SocialAPIFactory.createProvider(ExternalNetwork.Facebook, ClientPlatform.WEB);
		List<Message> messages = facebookAPI.listMessages(identity,null);
		Assert.assertFalse(messages.isEmpty());
		// all fb messages will have conversations
		for(Message message : messages) {
			log.debug("message {}", message);
			Assert.assertNotNull(message.getConversation().getConversationIdentifier());
		}
		
	}

}
