package com.ubiquity.identity.repository;

import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.social.domain.SocialNetwork;
import com.ubiquity.social.repository.SocialIdentityRepository;
import com.ubiquity.social.repository.SocialIdentityRepositoryJpaImpl;

/***
 * Tests testing basic CRUD operations for a user repository
 * 
 * @author chris
 *
 */
@Ignore
public class SocialRepositoryTest {

	private Logger log = LoggerFactory.getLogger(getClass());

	private SocialIdentityRepository socialRepository;
	private UserRepository userRepository;
	
	private User user;
	private ExternalIdentity identity;

	@After
	public void tearDown() throws Exception {
		EntityManagerSupport.closeEntityManager();
	}

	@Before
	public void setUp() throws Exception {

		socialRepository = new SocialIdentityRepositoryJpaImpl();
		userRepository = new UserRepositoryJpaImpl();
		
		// create user and identity as we normally would
		user = new User.Builder()
				.lastUpdated(System.currentTimeMillis())
				.firstName(UUID.randomUUID().toString())
				.lastName(UUID.randomUUID().toString())
				.email(UUID.randomUUID().toString())
				.clientPlatform(ClientPlatform.Android)
				.displayName(UUID.randomUUID().toString())
				.build();
		
		identity = new ExternalIdentity.Builder()
			.isActive(Boolean.TRUE)
			.lastUpdated(System.currentTimeMillis())
			.user(user)
			.identifier(UUID.randomUUID().toString())
			.accessToken(UUID.randomUUID().toString())
			.identityProvider(SocialNetwork.Facebook.getValue())
			.build();
		
		user.getIdentities().add(identity);
		
		EntityManagerSupport.beginTransaction();
		userRepository.create(user);
		EntityManagerSupport.commit();

		
		log.info("id {}", user.getUserId());
	}

	@Test
	public void testFindSocialIdentityByUserIdAndProvider() throws Exception {
		ExternalIdentity persisted = socialRepository.findOne(user.getUserId(), SocialNetwork.Facebook);
		Assert.assertNotNull(persisted);
		Assert.assertEquals(persisted.getIdentityId(), identity.getIdentityId());
	
	}
	
	@Test
	public void testUpdateSocialIdentity() throws Exception {
		ExternalIdentity persisted = socialRepository.findOne(user.getUserId(), SocialNetwork.Facebook);
		String newToken = UUID.randomUUID().toString();
		persisted.setAccessToken(newToken);
		
		EntityManagerSupport.beginTransaction();
		userRepository.update(user);
		EntityManagerSupport.commit();
		
		persisted = socialRepository.findOne(user.getUserId(), SocialNetwork.Facebook);
		Assert.assertEquals(persisted.getAccessToken(), newToken);

		
		
	}

	



}
