package com.ubiquity.sprocket.service;

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
import com.ubiquity.identity.domain.Application;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.Developer;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.ExternalNetworkApplication;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.domain.factory.UserFactory;
import com.ubiquity.identity.factory.TestDeveloperFactory;
import com.ubiquity.identity.repository.DeveloperRepositoryJpaImpl;
import com.ubiquity.integration.api.SocialAPIFactory;
import com.ubiquity.integration.domain.Contact;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.service.ContactService;

public class ContactServiceTest {

	private static ContactService contactService;
	private static ExternalIdentity identity;
	private static ExternalNetworkApplication externalNetworkApplication;
	// private static Application application;
	private static User user;
	@SuppressWarnings("unused")
	private Logger log = LoggerFactory.getLogger(getClass());

	@BeforeClass
	public static void setUp() throws Exception {

		Configuration config = new PropertiesConfiguration("test.properties");
		ServiceFactory.initialize(config, null);
		SocialAPIFactory.initialize(config);
		JedisConnectionFactory.initialize(config);
		contactService = ServiceFactory.getContactService();
		Developer developer = TestDeveloperFactory
				.createTestDeveloperWithMinimumRequiredProperties();
		
		EntityManagerSupport.beginTransaction();
		new DeveloperRepositoryJpaImpl().create(developer);
		EntityManagerSupport.commit();
		
		Application application = ServiceFactory.getApplicationService()
				.createDefaultAppIFNotExsists(developer,UUID.randomUUID().toString(),UUID.randomUUID().toString());
		user = UserFactory
				.createUserWithRequiredFieldsUsingApplication(UUID.randomUUID()
						.toString(), ClientPlatform.WEB, true, application);
		ServiceFactory.getUserService().create(user);

		externalNetworkApplication = ServiceFactory.getApplicationService()
				.getExAppByAppIdAndExternalNetworkAndClientPlatform(application.getAppId(),
						ExternalNetwork.Facebook.ordinal(), ClientPlatform.WEB);
		identity = ServiceFactory
				.getExternalIdentityService().createOrUpdateExternalIdentity(
						user, UUID.randomUUID().toString(),
						UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), ClientPlatform.WEB,
						ExternalNetwork.Facebook, 3600L,
						externalNetworkApplication);
	}

	@Test
	public void SyncContacts() {
		// sync Facebook friends from sprocket mock network
		List<Contact> contacts = contactService.syncContacts(identity,
				externalNetworkApplication);
		Assert.assertFalse(contacts.isEmpty());
		// find Contacts for user
		contacts = contactService.findContactsForActiveNetworksByOwnerId(user
				.getUserId());
		Assert.assertTrue(contacts.size() > 1);
	}

	@Test
	public void FindAllContactByUserIdentities() {
		List<Contact> contacts = contactService
				.findAllContactByUserIdentities(user.getUserId());
		Assert.assertEquals(1, contacts.size());
	}

}
