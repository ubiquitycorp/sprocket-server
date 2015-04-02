package com.ubiquity.social.api;

import java.util.UUID;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.niobium.repository.redis.JedisConnectionFactory;
import com.ubiquity.identity.domain.Application;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.Developer;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.ExternalNetworkApplication;
import com.ubiquity.identity.factory.TestDeveloperFactory;
import com.ubiquity.identity.repository.DeveloperRepositoryJpaImpl;
import com.ubiquity.integration.api.SocialAPI;
import com.ubiquity.integration.api.SocialAPIFactory;
import com.ubiquity.integration.domain.Contact;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.sprocket.service.ServiceFactory;

public class GoogleApiTest {

	private static Logger log = LoggerFactory.getLogger(GoogleApiTest.class);
	private static ExternalNetworkApplication externalNetworkApplication;
	private static ExternalIdentity identity;

	@BeforeClass
	public static void setUp() throws Exception {
		// TODO add vaild access and refresh token
		identity = new ExternalIdentity.Builder()
				.identifier(UUID.randomUUID().toString()).accessToken("")
				.refreshToken("").expiresAt(1L)
				.clientPlatform(ClientPlatform.WEB)
				.externalNetwork(ExternalNetwork.GooglePlus.ordinal()).build();
		log.debug("authenticated google with identity {} ", identity);

		// intialize services
		Configuration config = new PropertiesConfiguration("test.properties");
		JedisConnectionFactory.initialize(config);
		SocialAPIFactory.initialize(config);
		ServiceFactory.initialize(config, null);

		Developer developer = TestDeveloperFactory
				.createTestDeveloperWithMinimumRequiredProperties();
		
		EntityManagerSupport.beginTransaction();
		new DeveloperRepositoryJpaImpl().create(developer);
		EntityManagerSupport.commit();
		
		Application application = ServiceFactory.getApplicationService()
				.createDefaultAppIFNotExsists(developer,UUID.randomUUID().toString(),UUID.randomUUID().toString());
		
		externalNetworkApplication = ServiceFactory.getApplicationService()
				.getExAppByAppIdAndExternalNetworkAndClientPlatform(application.getAppId(),
						identity.getExternalNetwork(),
						identity.getClientPlatform());
	}

	@Test
	public void testAuthenticatedReturnsGenderAndAgeRange() {
		SocialAPI socialApi = SocialAPIFactory.createProvider(
				ExternalNetwork.getNetworkById(identity.getExternalNetwork()),
				identity.getClientPlatform(), externalNetworkApplication);
		Contact contact = socialApi.authenticateUser(identity);
		Assert.assertTrue(contact.getGender() == null);
		Assert.assertTrue(contact.getAgeRange() == null);

	}

}
