package com.ubiquity.social.api;

import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.niobium.repository.redis.JedisConnectionFactory;
import com.ubiquity.content.api.VimeoAPITest;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.factory.TestUserFactory;
import com.ubiquity.integration.api.SocialAPI;
import com.ubiquity.integration.api.SocialAPIFactory;
import com.ubiquity.integration.domain.Contact;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.Message;

public class FacebookApiTest {
	
private static Logger log = LoggerFactory.getLogger(VimeoAPITest.class);
	
	private static ExternalIdentity identity;
	
	@BeforeClass
	public static void setUp() throws Exception {
		
		EntityManagerSupport.beginTransaction();
		User user = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		EntityManagerSupport.commit();
		
		identity = new ExternalIdentity.Builder()
			.user(user)
			.accessToken("CAACEdEose0cBADwKBAaDVntezhdSvYJ8IM1EJX822o2UDvLlPBybcRlPI8s80FohfcaaFvFey3C42zlboHop55TNRhqysu5hPZCZAvV7gUnxsXPDbds0sIxWOecgFvXW2ZCFDDnG52im55OjcZC3L59WW5sfGZAPHZA4yGyzhgd8zwiqHpEDCd6niBOUR7lThw1wrGvIQI1KCs3KuKob24BwI5weAcd28ZD").build();
		log.debug("authenticated Facebook with identity {} ", identity);
		
		Configuration configuration = new PropertiesConfiguration("test.properties");
		
		JedisConnectionFactory.initialize(configuration);
		
		SocialAPIFactory.initialize(configuration);
	}
	
	@Test
	public void testGetMessages() {
		
		SocialAPI facebookAPI = SocialAPIFactory.createProvider(ExternalNetwork.Facebook, ClientPlatform.WEB);
		List<Message> messages = facebookAPI.listMessages(identity,null, null);
		Assert.assertFalse(messages.isEmpty());
		// all fb messages will have conversations
		for(Message message : messages) {
			log.debug("message {}", message);
			Assert.assertNotNull(message.getConversation().getConversationIdentifier());
		}
	}
	
	@Test
	public void testAuthenticateReturnsGenderAgeRangeAndLocation() {
		SocialAPI facebookAPI = SocialAPIFactory.createProvider(ExternalNetwork.Facebook, ClientPlatform.WEB);
		Contact contact = facebookAPI.authenticateUser(identity);
		Assert.assertTrue(contact.getGender() != null);	
		Assert.assertTrue(contact.getAgeRange() != null);
		Assert.assertTrue(contact.getAgeRange().getMin() == 35); // 21 would be the default, this token has an age greater than 21
		Assert.assertTrue(contact.getLocation() != null);
	}

}
