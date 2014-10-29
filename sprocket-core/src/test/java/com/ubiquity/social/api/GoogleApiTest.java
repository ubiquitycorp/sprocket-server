package com.ubiquity.social.api;

import java.util.UUID;

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
import com.ubiquity.integration.api.SocialAPI;
import com.ubiquity.integration.api.SocialAPIFactory;
import com.ubiquity.integration.domain.Contact;
import com.ubiquity.integration.domain.ExternalNetwork;

public class GoogleApiTest {
	
	private static Logger log = LoggerFactory.getLogger(GoogleApiTest.class);
	
	private static ExternalIdentity identity;
	
	@BeforeClass
	public static void setUp() throws Exception {
		identity = new ExternalIdentity.Builder().identifier(UUID.randomUUID().toString())
				.accessToken("ya29.bACf9_KhJVCIKykAAAAqXAdu9HfJhMXajQ-Kl28idRQ_tEJhHgv4IeQjPEQ87STxwi9wu6FqiX99HX4t6rQ").build();
		log.debug("authenticated google with identity {} ", identity);
		
		// intialize services
		Configuration config = new PropertiesConfiguration("test.properties");
		JedisConnectionFactory.initialize(config);
		SocialAPIFactory.initialize(config);
		
	}

	
	@Test
	public void testAuthenticatedReturnsGenderAndAgeRange() {
		SocialAPI socialApi = SocialAPIFactory.createProvider(ExternalNetwork.Gmail, ClientPlatform.Android);
		Contact contact = socialApi.authenticateUser(identity);
		Assert.assertTrue(contact.getGender() != null);	
		Assert.assertTrue(contact.getAgeRange() != null);

		
	}

	
	

}
