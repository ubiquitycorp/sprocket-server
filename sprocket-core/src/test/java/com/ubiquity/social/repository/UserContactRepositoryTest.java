package com.ubiquity.social.repository;

import java.util.LinkedList;
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
import com.ubiquity.integration.domain.UserContact;
import com.ubiquity.integration.factory.TestContactFactory;
import com.ubiquity.integration.repository.ContactRepository;
import com.ubiquity.integration.repository.ContactRepositoryJpaImpl;
import com.ubiquity.integration.repository.UserContactRepository;
import com.ubiquity.integration.repository.UserContactRepositoryJpaImpl;

public class UserContactRepositoryTest {
	private static UserContactRepository userContactRepository;
	private static UserRepository userRepository;
	private static ContactRepository contactRepository;
	private static User owner;
	private static Contact facebookContact, twitterContact, vimeoContact;
	private static UserContact facebookUserContact, twitterUserContact,
			vimeoUserContact;

	@AfterClass
	public static void tearDown() throws Exception {
		EntityManagerSupport.closeEntityManager();
	}

	@BeforeClass
	public static void setUp() throws Exception {

		userContactRepository = new UserContactRepositoryJpaImpl();
		userRepository = new UserRepositoryJpaImpl();
		contactRepository = new ContactRepositoryJpaImpl();

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
				.createContactWithMininumRequiredFieldsAndExternalNetwork(null,
						ExternalNetwork.Facebook);
		twitterContact = TestContactFactory
				.createContactWithMininumRequiredFieldsAndExternalNetwork(null,
						ExternalNetwork.Twitter);
		vimeoContact = TestContactFactory
				.createContactWithMininumRequiredFieldsAndExternalNetwork(null,
						ExternalNetwork.Vimeo);

		UserContact facebookIdentityUserContact = new UserContact.Builder()
				.contact(facebookIdentityContact).user(owner).isDeleted(false)
				.lastUpdated(System.currentTimeMillis()).build();
		UserContact twitterIdentityUserContact = new UserContact.Builder()
				.contact(twitterIdentityContact).user(owner).isDeleted(false)
				.lastUpdated(System.currentTimeMillis()).build();
		UserContact vimeoIdentityUserContact = new UserContact.Builder()
				.contact(vimeoIdentityContact).user(owner).isDeleted(false)
				.lastUpdated(System.currentTimeMillis()).build();

		facebookUserContact = new UserContact.Builder()
				.contact(facebookContact).user(owner).isDeleted(false)
				.lastUpdated(System.currentTimeMillis()).build();
		twitterUserContact = new UserContact.Builder().contact(twitterContact)
				.user(owner).isDeleted(false)
				.lastUpdated(System.currentTimeMillis()).build();
		vimeoUserContact = new UserContact.Builder().contact(vimeoContact)
				.user(owner).isDeleted(false)
				.lastUpdated(System.currentTimeMillis()).build();
		EntityManagerSupport.beginTransaction();
		contactRepository.create(facebookContact);
		contactRepository.create(twitterContact);
		contactRepository.create(vimeoContact);

		contactRepository.create(facebookIdentityContact);
		contactRepository.create(twitterIdentityContact);
		contactRepository.create(vimeoIdentityContact);

		userContactRepository.create(facebookIdentityUserContact);
		userContactRepository.create(twitterIdentityUserContact);
		userContactRepository.create(vimeoIdentityUserContact);

		userContactRepository.create(facebookUserContact);
		userContactRepository.create(twitterUserContact);
		userContactRepository.create(vimeoUserContact);
		EntityManagerSupport.commit();
	}

	@Test
	public void testDeleteWithoutIds() {
		List<Contact> contacts = userContactRepository
				.findByOwnerIdAndExternalNetwork(owner.getUserId(),
						ExternalNetwork.Twitter);
		Assert.assertEquals(contacts.size(), 2);

		List<Long> contactsIds = new LinkedList<Long>();
		contactsIds.add(twitterUserContact.getContact().getContactId());
		EntityManagerSupport.beginTransaction();
		userContactRepository.deleteWithoutIds(owner.getUserId(), contactsIds,
				ExternalNetwork.Twitter);
		EntityManagerSupport.commit();

		contacts = userContactRepository.findByOwnerIdAndExternalNetwork(
				owner.getUserId(), ExternalNetwork.Twitter);
		Assert.assertEquals(contacts.size(), 1);
	}

	@Test
	public void testFindByOwnerId() {
		List<Contact> contacts = userContactRepository.findByOwnerId(owner
				.getUserId());
		Assert.assertEquals(contacts.size(), 3);
	}

	@Test
	public void testFindByOwnerIdAndExternalNetwork() {
		List<Contact> contacts = userContactRepository
				.findByOwnerIdAndExternalNetwork(owner.getUserId(),
						ExternalNetwork.Facebook);
		Assert.assertEquals(contacts.size(), 2);

		contacts = userContactRepository.findByOwnerIdAndExternalNetwork(
				owner.getUserId(), ExternalNetwork.YouTube);
		Assert.assertEquals(contacts.size(), 0);

		try {
			EntityManagerSupport.beginTransaction();
			facebookUserContact.setIsDeleted(true);
			userContactRepository.update(facebookUserContact);
			EntityManagerSupport.commit();
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
		contacts = userContactRepository.findByOwnerIdAndExternalNetwork(
				owner.getUserId(), ExternalNetwork.Facebook);
		Assert.assertEquals(contacts.size(), 1);
	}

	@Test
	public void testFindByOwnerIDAndExternalNetworkAndExternalIdentitfier() {
		UserContact c = userContactRepository
				.findUserContactByOwnerIdAndExternalIdentifierAndExternalNetwork(
						owner.getUserId(), twitterContact.getExternalIdentity()
								.getIdentifier(), ExternalNetwork.Twitter);
		Assert.assertEquals(c.getContact().getContactId(),
				twitterContact.getContactId());
	}

	@Test
	public void testCountContacts() {
		int count = userContactRepository
				.countAllActiveContactsByOwnerIdAndExternalNetwork(
						owner.getUserId(), ExternalNetwork.Twitter);
		Assert.assertEquals(count, 1);
	}

	@Test
	public void testFindByUserIdAndExternalIdentityId() {
		Contact c = userContactRepository.findByUserIdAndExternalIdentityId(
				owner.getUserId(), twitterContact.getExternalIdentity()
						.getIdentityId());
		Assert.assertNotNull(c);
	}

	@Test
	public void testFindContactsForActiveNetworksByOwnerId() {
		List<Contact> contacts = userContactRepository
				.findContactsForActiveNetworksByOwnerId(owner.getUserId());
		Assert.assertEquals(contacts.size(), 6);
		vimeoUserContact.setIsDeleted(true);
		EntityManagerSupport.beginTransaction();
		userContactRepository.update(vimeoUserContact);
		EntityManagerSupport.commit();
		contacts = userContactRepository
				.findContactsForActiveNetworksByOwnerId(owner.getUserId());
		Assert.assertEquals(contacts.size(), 5);

	}
}
