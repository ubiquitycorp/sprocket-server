package com.ubiquity.social.api;

import java.util.List;
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
import com.ubiquity.content.api.VimeoAPITest;
import com.ubiquity.identity.domain.Application;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.Developer;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.ExternalNetworkApplication;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.factory.TestDeveloperFactory;
import com.ubiquity.identity.factory.TestUserFactory;
import com.ubiquity.identity.repository.DeveloperRepositoryJpaImpl;
import com.ubiquity.integration.api.SocialAPI;
import com.ubiquity.integration.api.SocialAPIFactory;
import com.ubiquity.integration.domain.Activity;
import com.ubiquity.integration.domain.ActivityType;
import com.ubiquity.integration.domain.Contact;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.Message;
import com.ubiquity.integration.domain.PostActivity;
import com.ubiquity.sprocket.service.ServiceFactory;

public class MockNetworkSocialAPITest {
	private static Logger log = LoggerFactory.getLogger(VimeoAPITest.class);

	private static ExternalIdentity identity;
	private static ExternalNetworkApplication externalApplication;

	@BeforeClass
	public static void setUp() throws Exception {
		Configuration configuration = new PropertiesConfiguration(
				"test.properties");
		ServiceFactory.initialize(configuration, null);
		SocialAPIFactory.initialize(configuration);
		JedisConnectionFactory.initialize(configuration);
		
		Developer developer = TestDeveloperFactory
				.createTestDeveloperWithMinimumRequiredProperties();
		
		EntityManagerSupport.beginTransaction();
		new DeveloperRepositoryJpaImpl().create(developer);
		EntityManagerSupport.commit();
		
		Application application = ServiceFactory.getApplicationService()
				.createDefaultAppIFNotExsists(developer,UUID.randomUUID().toString(),UUID.randomUUID().toString());
		
		externalApplication = ServiceFactory.getApplicationService()
				.getExAppByAppIdAndExternalNetworkAndClientPlatform(application.getAppId(),
						identity.getExternalNetwork(), identity.getClientPlatform());
		
		User user = TestUserFactory
				.createTestUserWithMinimumRequiredProperties(null);
		identity = new ExternalIdentity.Builder()
				.clientPlatform(ClientPlatform.WEB).inUse(true).user(user)
				.accessToken(UUID.randomUUID().toString())
				.externalNetwork(ExternalNetwork.SocailMockNetwork.ordinal())
				.build();
	}

	@Test
	public void testGetContacts() {
		SocialAPI socialAPI = SocialAPIFactory.createProvider(
				ExternalNetwork.getNetworkById(identity.getExternalNetwork()),
				identity.getClientPlatform(), externalApplication);

		List<Contact> contacts = socialAPI.getContacts(identity);
		Assert.assertFalse(contacts.isEmpty());

		for (Contact contact : contacts)
			log.debug("contact: {}", contact);
	}

	@Test
	public void testGetActivities() {
		SocialAPI socialAPI = SocialAPIFactory.createProvider(
				ExternalNetwork.getNetworkById(identity.getExternalNetwork()),
				identity.getClientPlatform(), externalApplication);

		List<Activity> activities = socialAPI.listActivities(identity);
		Assert.assertFalse(activities.isEmpty());

		for (Activity activity : activities)
			log.debug("activity: {}", activity);
	}

	@Test
	public void testSearchActivities() {
		SocialAPI socialAPI = SocialAPIFactory.createProvider(
				ExternalNetwork.getNetworkById(identity.getExternalNetwork()),
				identity.getClientPlatform(), externalApplication);

		List<Activity> activities = socialAPI.searchActivities("karate", 1, 25,
				identity);
		Assert.assertFalse(activities.isEmpty());
		Assert.assertTrue(activities.size() == 25);

		for (Activity activity : activities)
			log.debug("activity: {}", activity);
	}

	@Test
	public void testGetMessages() {
		SocialAPI socialAPI = SocialAPIFactory.createProvider(
				ExternalNetwork.getNetworkById(identity.getExternalNetwork()),
				identity.getClientPlatform(), externalApplication);

		List<Message> messages = socialAPI.listMessages(identity, null, null);
		Assert.assertFalse(messages.isEmpty());

		for (Message message : messages) {
			log.debug("message {}", message);
			Assert.assertNotNull(message.getConversation()
					.getConversationIdentifier());
		}
	}

	@Test
	public void postActivityTest() {
		String body = "Test Activity " + System.currentTimeMillis();
		SocialAPI socialAPI = SocialAPIFactory.createProvider(
				ExternalNetwork.getNetworkById(identity.getExternalNetwork()),
				identity.getClientPlatform(), externalApplication);
		PostActivity postActivity = new PostActivity.Builder().body(body)
				.activityTypeId(ActivityType.STATUS.ordinal()).title("Title")
				.build();
		boolean posted = socialAPI.postActivity(identity, postActivity);

		Assert.assertTrue(posted);

		List<Activity> activities = socialAPI.listActivities(identity);
		Assert.assertFalse(activities.isEmpty());

		boolean found = false;

		for (Activity activity : activities) {
			if (activity.getBody() != null && activity.getBody().equals(body))
				found = true;
		}
		Assert.assertTrue(found);
	}
}
