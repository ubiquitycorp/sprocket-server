package com.ubiquity.sprocket.repository;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.factory.TestUserFactory;
import com.ubiquity.identity.repository.UserRepository;
import com.ubiquity.identity.repository.UserRepositoryJpaImpl;
import com.ubiquity.sprocket.domain.Location;

/***
 * Tests testing basic CRUD operations for a location repository
 * 
 * @author chris
 *
 */
public class LocationRepositoryTest {

	private Logger log = LoggerFactory.getLogger(getClass());

	private UserRepository userRepository;
	private LocationRepository locationRepository;
	
	private Location location;
	private User user;
	
	@After
	public void tearDown() throws Exception {
		EntityManagerSupport.closeEntityManager();
	}

	@Before
	public void setUp() throws Exception {

		userRepository = new UserRepositoryJpaImpl();
		locationRepository = new LocationRepositoryJpaImpl();
		
		user = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		
		EntityManagerSupport.beginTransaction();
		userRepository.create(user);		
		EntityManagerSupport.commit();

		location = new Location.Builder().latitude(59.93939393).longitude(-34.3030303).lastUpdated(System.currentTimeMillis()).user(user).build();

		EntityManagerSupport.beginTransaction();
		locationRepository.create(location);		
		EntityManagerSupport.commit();
		
		log.info("id {}", user.getUserId());
	}

	@Test
	public void testFindByUser() throws Exception {
		Location persisted = locationRepository.findByUserId(user.getUserId());
		Assert.assertNotNull(persisted);
	}

	

}
