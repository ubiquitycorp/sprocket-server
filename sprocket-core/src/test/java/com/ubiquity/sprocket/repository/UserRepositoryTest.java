package com.ubiquity.sprocket.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.identity.domain.Application;
import com.ubiquity.identity.domain.Developer;
import com.ubiquity.identity.domain.Identity;
import com.ubiquity.identity.domain.NativeIdentity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.factory.TestApplicationFactory;
import com.ubiquity.identity.factory.TestDeveloperFactory;
import com.ubiquity.identity.repository.ApplicationRepositoryJpaImpl;
import com.ubiquity.identity.repository.DeveloperRepositoryJpaImpl;
import com.ubiquity.sprocket.factory.TestSprocketUserFactory;

/***
 * Tests testing basic CRUD operations for a user repository
 * 
 * @author chris
 * 
 */
public class UserRepositoryTest {

	private static Logger log = LoggerFactory
			.getLogger(UserRepositoryTest.class);

	private static SprocketUserRepository userRepository;
	private static User user;
	private static NativeIdentity identity;

	@After
	public void tearDown() throws Exception {
		EntityManagerSupport.closeEntityManager();
	}

	@BeforeClass
	public static void setUp() throws Exception {
		
		userRepository = new SprocketUserRepositoryJpaImpl();
		
		user = TestSprocketUserFactory
				.createTestUserWithMinimumRequiredProperties(null);
		identity = (NativeIdentity) user.getIdentities().iterator().next();
		
		
		EntityManagerSupport.beginTransaction();
		userRepository.create(user);
		EntityManagerSupport.commit();
		log.info("id {}", user.getUserId());
	}

	@Test
	public void testCreate() throws Exception {
		User persisted = userRepository.read(user.getUserId());
		Assert.assertNotNull(persisted.getUserId());

		Identity persistedIdentity = persisted.getIdentities().iterator()
				.next();
		Assert.assertNotNull(persistedIdentity);
	}

	@Test
	public void testSearchByUsernameAndPassword() {
		// Now read back from db, making sure social identity was persisted
		User persisted = userRepository.searchUserByUsernameAndPassword(
				identity.getUsername(), identity.getPassword());
		Assert.assertNotNull(persisted);

		// now do a negative test by searching random junk
		persisted = userRepository.searchUserByUsernameAndPassword(
				identity.getUsername(), UUID.randomUUID().toString());
		Assert.assertNull(persisted);
	}

	@Test
	public void testSearchByUsername() {
		// Now read back from db, making sure social identity was persisted
		User persisted = userRepository.searchUserByUsername(identity
				.getUsername());
		Assert.assertNotNull(persisted);

		// now do a negative test by searching random junk
		persisted = userRepository.searchUserByUsername(UUID.randomUUID()
				.toString());
		Assert.assertNull(persisted);
	}

	@Test
	public void testFindAllActiveSprocketUser() {
		Developer developer =  TestDeveloperFactory.createTestDeveloperWithMinimumRequiredProperties();
		EntityManagerSupport.beginTransaction();
		new DeveloperRepositoryJpaImpl().create(developer);
		EntityManagerSupport.commit();
		Application application = TestApplicationFactory.createTestApplicationWithMinimumRequiredProperties(developer);
		EntityManagerSupport.beginTransaction();
		new ApplicationRepositoryJpaImpl().create(application);
		EntityManagerSupport.commit();
		
		User user2 = TestSprocketUserFactory
				.createTestUserWithMinimumRequiredProperties(application);
		
		EntityManagerSupport.beginTransaction();
		userRepository.create(user2);
		EntityManagerSupport.commit();
		
		
		List<BigDecimal[]>  users = userRepository
				.findAllActiveSprocketUserIds();
		Assert.assertEquals(2, users.size());
		
		user.setLastLogin(System.currentTimeMillis()
				- (14 * 24 * 60 * 60 * 1000) - 10000);
		
		EntityManagerSupport.beginTransaction();
		userRepository.update(user);
		EntityManagerSupport.commit();
		
		users = userRepository
				.findAllActiveSprocketUserIds();
		Assert.assertEquals(1, users.size());

	}

}
