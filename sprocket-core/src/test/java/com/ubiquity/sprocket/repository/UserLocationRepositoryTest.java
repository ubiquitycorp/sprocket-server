package com.ubiquity.sprocket.repository;

import java.math.BigDecimal;

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
import com.ubiquity.location.domain.Location;
import com.ubiquity.location.domain.UserLocation;
import com.ubiquity.location.repository.UserLocationRepository;
import com.ubiquity.location.repository.UserLocationRepositoryJpaImpl;

/***
 * Tests testing basic CRUD operations for a location repository
 * 
 * @author chris
 * 
 */
public class UserLocationRepositoryTest {

	private Logger log = LoggerFactory.getLogger(getClass());

	private UserRepository userRepository;
	private UserLocationRepository locationRepository;

	private UserLocation location;
	private User user;

	@After
	public void tearDown() throws Exception {
		EntityManagerSupport.closeEntityManager();
	}

	@Before
	public void setUp() throws Exception {

		userRepository = new UserRepositoryJpaImpl();
		locationRepository = new UserLocationRepositoryJpaImpl();

		user = TestUserFactory.createTestUserWithMinimumRequiredProperties(null);

		EntityManagerSupport.beginTransaction();
		userRepository.create(user);
		EntityManagerSupport.commit();

		location = new UserLocation.Builder()
				.location(
						new Location.Builder()
								.latitude(new BigDecimal(59.93939393))
								.longitude(new BigDecimal(-34.3030303)).build())
				.lastUpdated(System.currentTimeMillis()).user(user)
				.timestamp(System.currentTimeMillis()).build();

		EntityManagerSupport.beginTransaction();
		locationRepository.create(location);
		EntityManagerSupport.commit();

		log.info("id {}", user.getUserId());
	}

	@Test
	public void testFindByUser() throws Exception {
		UserLocation persisted = locationRepository.findByUserId(user
				.getUserId());
		Assert.assertNotNull(persisted);
	}

}
