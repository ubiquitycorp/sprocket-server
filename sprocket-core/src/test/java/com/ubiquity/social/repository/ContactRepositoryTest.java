package com.ubiquity.social.repository;

import java.util.List;

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
	public void testFindByOwnerId() {
		List<Contact> contacts = contactRepository.findByOwnerId(owner
				.getUserId());
		Assert.assertEquals(contacts.size(), 5);
	}

	@Test
	public void testFindByOwnerIdAndExternalNetwork() {
		List<Contact> contacts = contactRepository
				.findByOwnerIdAndExternalNetwork(owner.getUserId(),
						ExternalNetwork.Facebook);
		Assert.assertEquals(contacts.size(), 2);

		contacts = contactRepository.findByOwnerIdAndExternalNetwork(
				owner.getUserId(), ExternalNetwork.YouTube);
		Assert.assertEquals(contacts.size(), 0);

		EntityManagerSupport.beginTransaction();
		facebookContact.isDeleted(true);
		contactRepository.update(facebookContact);
		EntityManagerSupport.commit();

		contacts = contactRepository.findContactsForActiveNetworksByOwnerId(owner
				.getUserId());
		Assert.assertEquals(contacts.size(), 5);
	}

	@Test
	public void testFindByOwnerIDAndExternalNetworkAndExternalIdentitfier() {
		Contact c = contactRepository
				.findByOwnerIDAndExternalNetworkAndExternalIdentitfier(owner,
						twitterContact.getExternalIdentity().getIdentifier(),
						ExternalNetwork.Twitter);
		Assert.assertEquals(c.getContactId(), twitterContact.getContactId());
		;
	}

	@Test
	public void testCountContacts() {
		int count = contactRepository
				.countAllActiveContactsByOwnerIdAndExternalNetwork(
						owner.getUserId(), ExternalNetwork.Twitter);
		Assert.assertEquals(count, 2);

		count = contactRepository
				.countAllByExternalNetwork(ExternalNetwork.Vimeo);
		Assert.assertEquals(count, 2);
	}

	@Test
	public void testFindByUserIdAndExternalIdentityId() {
		Contact c = contactRepository.findByUserIdAndExternalIdentityId(owner
				.getUserId(), twitterContact.getExternalIdentity()
				.getIdentityId());
		Assert.assertNotNull(c);
	}

	public void testRemoveAllContacts() {
		List<Contact> contacts = contactRepository
				.findByOwnerIdAndExternalNetwork(owner.getUserId(),
						ExternalNetwork.Twitter);
		Assert.assertEquals(contacts.size(), 2);

		contactRepository.removeAllContacts(owner.getUserId(),
				ExternalNetwork.Twitter);

		contacts = contactRepository.findByOwnerIdAndExternalNetwork(
				owner.getUserId(), ExternalNetwork.Twitter);
		Assert.assertEquals(contacts.size(), 0);
	}

	public void testFindAllContactsOfActiveUserIdentities() {
		List<Contact> contacts = contactRepository
				.findAllContactsOfActiveUserIdentities(owner.getUserId());
		Assert.assertNotEquals(contacts.size(), 3);
	}
}
