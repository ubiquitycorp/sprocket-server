package com.ubiquity.sprocket.service;

import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.factory.TestUserFactory;
import com.ubiquity.integration.domain.Contact;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.service.ContactService;

public class ContactServiceTest {
	
	private static ContactService contactService;
	private static ExternalIdentity identity;
	private static User user ;
	@SuppressWarnings("unused")
	private Logger log = LoggerFactory.getLogger(getClass());

	@BeforeClass
	public static void setUp() throws Exception {
		
		contactService = ServiceFactory.getContactService();
		user = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		ServiceFactory.getUserService().create(user);
		List<ExternalIdentity> externalIdentities = ServiceFactory.getExternalIdentityService().createOrUpdateExternalIdentity(user, UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), ClientPlatform.WEB, ExternalNetwork.Facebook, 3600L);
		identity = externalIdentities.get(0);

	}

	@Test
	public void SyncContacts() {
		// sync Facebook friends from sprocket mock network 
		List<Contact> contacts =contactService.syncContacts(identity);
		Assert.assertFalse(contacts.isEmpty());
		// find Contacts for user
		contacts =contactService.findContactsForActiveNetworksByOwnerId(user.getUserId());
		Assert.assertTrue(contacts.size()>1);
	}
	
	@Test
	public void FindAllContactByUserIdentities() {
		List<Contact> contacts =contactService.findAllContactByUserIdentities(user.getUserId());
		Assert.assertEquals(1,contacts.size());
	}

}
