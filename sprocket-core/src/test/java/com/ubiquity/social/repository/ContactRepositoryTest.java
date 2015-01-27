package com.ubiquity.social.repository;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.factory.TestUserFactory;
import com.ubiquity.identity.repository.UserRepository;
import com.ubiquity.identity.repository.UserRepositoryJpaImpl;
import com.ubiquity.integration.domain.Contact;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.factory.TestContactFactory;
import com.ubiquity.integration.repository.ContactRepository;
import com.ubiquity.integration.repository.ContactRepositoryJpaImpl;

public class ContactRepositoryTest {
	private static ContactRepository contactRepository;
	private static UserRepository userRepository;
	private static User owner;
	private static Contact facebookContact, twitterContact, vimeoContact;

	@AfterClass
	public static void tearDown() throws Exception {
		EntityManagerSupport.closeEntityManager();
	}

	@BeforeClass
	public static void setUp() throws Exception {

		contactRepository = new ContactRepositoryJpaImpl();
		userRepository = new UserRepositoryJpaImpl();

		owner = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		
		EntityManagerSupport.beginTransaction();
		userRepository.create(owner);
		EntityManagerSupport.commit();

		Contact facebookIdentityContact = TestContactFactory
				.createContactForIdentityWithMininumRequiredFieldsAndExternalNetwork(
						owner, ExternalNetwork.Facebook);
		Contact twitterIdentityContact = TestContactFactory
				.createContactForIdentityWithMininumRequiredFieldsAndExternalNetwork(
						owner, ExternalNetwork.Twitter);
		Contact vimeoIdentityContact = TestContactFactory
				.createContactForIdentityWithMininumRequiredFieldsAndExternalNetwork(
						owner, ExternalNetwork.Vimeo);

		facebookContact = TestContactFactory
				.createContactWithMininumRequiredFieldsAndExternalNetwork(
						owner, ExternalNetwork.Facebook);
		twitterContact = TestContactFactory
				.createContactWithMininumRequiredFieldsAndExternalNetwork(
						owner, ExternalNetwork.Twitter);
		vimeoContact = TestContactFactory
				.createContactWithMininumRequiredFieldsAndExternalNetwork(
						owner, ExternalNetwork.Vimeo);

		EntityManagerSupport.beginTransaction();
		contactRepository.create(facebookIdentityContact);
		contactRepository.create(twitterIdentityContact);
		contactRepository.create(vimeoIdentityContact);

		contactRepository.create(facebookContact);
		contactRepository.create(twitterContact);
		contactRepository.create(vimeoContact);
		EntityManagerSupport.commit();
	}

	@Test
	public void testFindByOwnerIDAndExternalNetworkAndExternalIdentitfier() {
		Contact c = contactRepository
				.findByExternalIdentitfierAndExternalNetwork(twitterContact
						.getExternalIdentity().getIdentifier(),
						ExternalNetwork.Twitter);
		Assert.assertEquals(c.getContactId(), twitterContact.getContactId());
	}
	
	@Test
	public void testCountContacts() {
		int count = contactRepository
				.countAllByExternalNetwork(ExternalNetwork.Vimeo);
		Assert.assertEquals(count, 2);
	}

}
