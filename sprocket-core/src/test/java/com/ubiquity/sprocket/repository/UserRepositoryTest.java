package com.ubiquity.sprocket.repository;

import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.identity.domain.Identity;
import com.ubiquity.identity.domain.NativeIdentity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.factory.TestUserFactory;
import com.ubiquity.identity.repository.UserRepository;
import com.ubiquity.identity.repository.UserRepositoryJpaImpl;

/***
 * Tests testing basic CRUD operations for a user repository
 * 
 * @author chris
 *
 */
public class UserRepositoryTest {

	private Logger log = LoggerFactory.getLogger(getClass());

	private UserRepository userRepository;
	private User user;
	private NativeIdentity identity;

	@After
	public void tearDown() throws Exception {
		EntityManagerSupport.closeEntityManager();
	}

	@Before
	public void setUp() throws Exception {

		userRepository = new UserRepositoryJpaImpl();

		user = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		identity = (NativeIdentity)user.getIdentities().iterator().next();
		
		EntityManagerSupport.beginTransaction();
		userRepository.create(user);
		EntityManagerSupport.commit();

		log.info("id {}", user.getUserId());
	}

	@Test
	public void testCreate() throws Exception {
		User persisted = userRepository.read(user.getUserId());
		Assert.assertNotNull(persisted.getUserId());
		
		Identity persistedIdentity = persisted.getIdentities().iterator().next();
		Assert.assertNotNull(persistedIdentity);
	}

	@Test
	public void testSearchByUsernameAndPassword() {
		// Now read back from db, making sure social identity was persisted
		User persisted = userRepository.searchUserByUsernameAndPassword(identity.getUsername(), identity.getPassword());
		Assert.assertNotNull(persisted);
		
		// now do a negative test by searching random junk
		 persisted = userRepository.searchUserByUsernameAndPassword(identity.getUsername(), UUID.randomUUID().toString());
		Assert.assertNull(persisted);
	}
	
	@Test
	public void testSearchByUsername() {
		// Now read back from db, making sure social identity was persisted
		User persisted = userRepository.searchUserByUsername(identity.getUsername());
		Assert.assertNotNull(persisted);
		
		// now do a negative test by searching random junk
		 persisted = userRepository.searchUserByUsername(UUID.randomUUID().toString());
		Assert.assertNull(persisted);
	}



}
