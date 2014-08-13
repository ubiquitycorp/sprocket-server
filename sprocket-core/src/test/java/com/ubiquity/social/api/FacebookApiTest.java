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
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.factory.TestUserFactory;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.Gender;
import com.ubiquity.social.domain.Message;
import com.ubiquity.external.domain.ExternalNetwork;

public class FacebookApiTest {
	
private static Logger log = LoggerFactory.getLogger(VimeoAPITest.class);
	
	private static ExternalIdentity identity;
	
	@BeforeClass
	public static void setUp() throws Exception {
		
		User user = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		identity = new ExternalIdentity.Builder().user(user).accessToken("CAACEdEose0cBAI3v23IiMqfqQjqaNAMbi7ZBEij505CbZCPGWZAS3ZA7c5jZAYxSlRpmUaBKQLlnsCFy3dJ4kqwaGahDkq7pLTiHU85flw64XqcU1JdWaLPmkmsYxCO1wLKd7ZCTI0kIvuDCh2IA3xllR1wjlZALBRNHZCz0Ly0Y7CZAbzETzJdZC4wgsZCR7PS0XmbmWFxA5oDbC316w5GZBNRM").build();
		
		
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
	
	@Test
	public void testAuthenticateReturnsGenderAgeRange() {
		SocialAPI facebookAPI = SocialAPIFactory.createProvider(ExternalNetwork.Facebook, ClientPlatform.WEB);
		Contact contact = facebookAPI.authenticateUser(identity);
		Assert.assertTrue(contact.getGender() != null);	
		Assert.assertTrue(contact.getAgeRange() != null);
	}

}
