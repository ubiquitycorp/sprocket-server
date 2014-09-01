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

		// create user and identity as we normally would
		judy = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		jack = TestUserFactory.createTestUserWithMinimumRequiredProperties();

		EntityManagerSupport.beginTransaction();
		userRepository.create(judy);
		userRepository.create(jack);


		fbMomMembership = new GroupMembership(ExternalNetwork.Facebook, judy, UUID.randomUUID().toString());
		youTubeMomMembership = new GroupMembership(ExternalNetwork.YouTube, judy, UUID.randomUUID().toString());
		youTubeSportsFanaticMembership = new GroupMembership(ExternalNetwork.YouTube, jack, UUID.randomUUID().toString());
		globalMembership = new GroupMembership(null, jack, UUID.randomUUID().toString());


		membershipRepository.create(fbMomMembership);
		membershipRepository.create(youTubeMomMembership);
		membershipRepository.create(youTubeSportsFanaticMembership);
		membershipRepository.create(globalMembership);

		EntityManagerSupport.commit();


	}
	
	@Test
	public void testFindUniqueGroups() throws Exception {
		List<String> identifiers = membershipRepository.findGroupIdentifiersByExternalNetwork(ExternalNetwork.Facebook);
		Assert.assertEquals(identifiers.get(0), fbMomMembership.getGroupIdentifier());
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
		membershipRepository.deleteWithNoNetwork();
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
	public void testDeleteMembeship() {
		membershipRepository.deleteByExternalNetwork(ExternalNetwork.Facebook);
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
