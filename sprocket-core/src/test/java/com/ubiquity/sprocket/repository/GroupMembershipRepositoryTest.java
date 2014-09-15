package com.ubiquity.sprocket.repository;

import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.factory.TestUserFactory;
import com.ubiquity.identity.repository.UserRepository;
import com.ubiquity.identity.repository.UserRepositoryJpaImpl;
import com.ubiquity.integration.factory.TestContactFactory;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.repository.ContactRepository;
import com.ubiquity.social.repository.ContactRepositoryJpaImpl;
import com.ubiquity.sprocket.domain.GroupMembership;

/***
 * Tests testing basic CRUD operations for a group membership repository
 * 
 * @author chris
 *
 */
public class GroupMembershipRepositoryTest {

	@SuppressWarnings("unused")
	private Logger log = LoggerFactory.getLogger(getClass());

	private GroupMembershipRepository membershipRepository;
	private UserRepository userRepository;
	private ContactRepository contactRepository;

	private User judy, jack;
	private GroupMembership fbMomMembership, youTubeMomMembership, youTubeSportsFanaticMembership, globalMembership;

	@After
	public void tearDown() throws Exception {
		EntityManagerSupport.closeEntityManager();
	}

	@Before
	public void setUp() throws Exception {

		membershipRepository = new GroupMembershipRepositoryJpaImpl();
		userRepository = new UserRepositoryJpaImpl();
		contactRepository = new ContactRepositoryJpaImpl();

		// create user and identity as we normally would
		judy = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		jack = TestUserFactory.createTestUserWithMinimumRequiredProperties();

		Contact judyFbContact = TestContactFactory.createContactBuilderWithMininumRequiredFieldsAndExternalNetwork(judy, ExternalNetwork.Facebook).build();
		Contact judyYouTubeContact = TestContactFactory.createContactBuilderWithMininumRequiredFieldsAndExternalNetwork(judy, ExternalNetwork.YouTube).build();
		Contact jackYouTubeContact = TestContactFactory.createContactBuilderWithMininumRequiredFieldsAndExternalNetwork(jack, ExternalNetwork.YouTube).build();

		EntityManagerSupport.beginTransaction();
		userRepository.create(judy);
		userRepository.create(jack);
		contactRepository.create(judyFbContact);
		contactRepository.create(judyYouTubeContact);
		contactRepository.create(jackYouTubeContact);

		EntityManagerSupport.commit();
		
	
		fbMomMembership = new GroupMembership(judyFbContact.getExternalIdentity(), judy, UUID.randomUUID().toString());
		youTubeMomMembership = new GroupMembership(judyYouTubeContact.getExternalIdentity(), judy, UUID.randomUUID().toString());
		youTubeSportsFanaticMembership = new GroupMembership(jackYouTubeContact.getExternalIdentity(), jack, UUID.randomUUID().toString());
		globalMembership = new GroupMembership(jack, UUID.randomUUID().toString());


		EntityManagerSupport.beginTransaction();
		membershipRepository.create(fbMomMembership);
		membershipRepository.create(youTubeMomMembership);
		membershipRepository.create(youTubeSportsFanaticMembership);
		membershipRepository.create(globalMembership);
		EntityManagerSupport.commit();



	}
	
	

	@Test
	public void testCreate() throws Exception {
		GroupMembership persisted = membershipRepository.read(fbMomMembership.getGroupMembershipId());
		Assert.assertNotNull(persisted.getGroupIdentifier());
		Assert.assertNotNull(persisted.getUser());
		Assert.assertNotNull(persisted.getExternalNetwork());
	}

	@Test
	public void testDeleteGlobalMembershipDoesNotDeleteAllRecords() {
		EntityManagerSupport.beginTransaction();
		membershipRepository.deleteWithNoNetwork();
		EntityManagerSupport.commit();
		// fb should still be around
		boolean accidentallyDeleted = Boolean.FALSE;
		try {
			membershipRepository.read(fbMomMembership.getGroupMembershipId());
			membershipRepository.read(youTubeMomMembership.getGroupMembershipId());
			membershipRepository.read(youTubeSportsFanaticMembership.getGroupMembershipId());

		} catch (IllegalArgumentException e) {
			accidentallyDeleted = Boolean.TRUE;
		}
		Assert.assertFalse(accidentallyDeleted);
	}
	
	@Test
	public void testDeleteByUserAndNetwork() {
		EntityManagerSupport.beginTransaction();
		boolean deleted = membershipRepository.deleteByExternalNetworkAndUserId(ExternalNetwork.Facebook, judy.getUserId());
		EntityManagerSupport.commit();
		Assert.assertTrue(deleted);
	}
	
	@Test
	public void testDeleteByUser() {
		EntityManagerSupport.beginTransaction();
		boolean deleted = membershipRepository.deleteByUserId(judy.getUserId());
		EntityManagerSupport.commit();
		Assert.assertTrue(deleted);
	}
	
	

	@Test
	public void testDeleteMembeship() {
		EntityManagerSupport.beginTransaction();
		membershipRepository.deleteByExternalNetwork(ExternalNetwork.Facebook);
		EntityManagerSupport.commit();
		
		// fb should still be around
		boolean accidentallyDeleted = Boolean.FALSE;
		try {
			membershipRepository.read(globalMembership.getGroupMembershipId());
			membershipRepository.read(youTubeMomMembership.getGroupMembershipId());
			membershipRepository.read(youTubeSportsFanaticMembership.getGroupMembershipId());

		} catch (IllegalArgumentException e) {
			accidentallyDeleted = Boolean.TRUE;
		}
		Assert.assertFalse(accidentallyDeleted);
	}
	
	@Test
	public void testFindByUser() {
		List<GroupMembership> membershipList = membershipRepository.findAllByUserId(judy.getUserId());
		Assert.assertTrue(membershipList.size() == 2);
		
		membershipList = membershipRepository.findAllByUserId(jack.getUserId());
		Assert.assertTrue(membershipList.size() == 2);
		
	}

}
