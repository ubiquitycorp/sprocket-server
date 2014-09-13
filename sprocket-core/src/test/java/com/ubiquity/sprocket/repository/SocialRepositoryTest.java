package com.ubiquity.sprocket.repository;

import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.factory.TestUserFactory;
import com.ubiquity.identity.repository.UserRepository;
import com.ubiquity.identity.repository.UserRepositoryJpaImpl;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.external.repository.ExternalIdentityRepository;
import com.ubiquity.external.repository.ExternalIdentityRepositoryJpaImpl;

/***
 * Tests testing basic CRUD operations for a user repository
 * 
 * @author chris
 *
 */
public class SocialRepositoryTest {

	private Logger log = LoggerFactory.getLogger(getClass());

	private ExternalIdentityRepository socialRepository;
	private UserRepository userRepository;
	
	private User user;
	private ExternalIdentity identity;

	@After
	public void tearDown() throws Exception {
		EntityManagerSupport.closeEntityManager();
	}

	@Before
	public void setUp() throws Exception {

		socialRepository = new ExternalIdentityRepositoryJpaImpl();
		userRepository = new UserRepositoryJpaImpl();
		
		// create user and identity as we normally would
		user = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		
		identity = new ExternalIdentity.Builder()
			.isActive(Boolean.TRUE)
			.lastUpdated(System.currentTimeMillis())
			.user(user)
			.identifier(UUID.randomUUID().toString())
			.accessToken(UUID.randomUUID().toString())
			.externalNetwork(ExternalNetwork.Facebook.ordinal())
			.build();
		
		user.getIdentities().add(identity);
		
		EntityManagerSupport.beginTransaction();
		userRepository.create(user);
		EntityManagerSupport.commit();

		
		log.info("id {}", user.getUserId());
	}

	@Test
	public void testFindSocialIdentityByUserIdAndProvider() throws Exception {
		ExternalIdentity persisted = socialRepository.getExternalIdentityByUserAndNetwork(user.getUserId(), ExternalNetwork.Facebook);
		Assert.assertNotNull(persisted);
		Assert.assertEquals(persisted.getIdentityId(), identity.getIdentityId());
	
	}
	
	@Test
	public void testUpdateSocialIdentity() throws Exception {
		ExternalIdentity persisted = socialRepository.getExternalIdentityByUserAndNetwork(user.getUserId(), ExternalNetwork.Facebook);
		String newToken = UUID.randomUUID().toString();
		persisted.setAccessToken(newToken);
		
		EntityManagerSupport.beginTransaction();
		userRepository.update(user);
		EntityManagerSupport.commit();
		
		persisted = socialRepository.getExternalIdentityByUserAndNetwork(user.getUserId(), ExternalNetwork.Facebook);
		Assert.assertEquals(persisted.getAccessToken(), newToken);

		
		
	}

	



}
