package com.ubiquity.sprocket.service;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import com.niobium.repository.Cache;
import com.niobium.repository.CacheRedisImpl;
import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.repository.UserRepository;
import com.ubiquity.identity.service.UserService;
import com.ubiquity.sprocket.domain.SprocketUser;
import com.ubiquity.sprocket.repository.SprocketUserRepositoryJpaImpl;

public class SprocketUserService extends UserService {

	private Cache cache;

	public SprocketUserService(Configuration configuration) {
		super(configuration);
		cache = new CacheRedisImpl(
				configuration.getInt("redis.user.application.database"));
	}

	/***
	 * Returns a user or an exception if the user does not exist
	 * 
	 * @param userId
	 * @return User
	 * 
	 * @throws IllegalArgumentException
	 *             If the user does not exist
	 */
	public User getUserById(Long userId) {
		try {
			User user = new SprocketUserRepositoryJpaImpl()
					.getSprocketUserById(userId);
			if (user == null)
				throw new IllegalArgumentException("User does not exist");
			return user;
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}

	/***
	 * Creates a user in the underlying database
	 * 
	 * @param user
	 */
	public void create(User user) {
		try {

			UserRepository userRepository = new SprocketUserRepositoryJpaImpl();
			EntityManagerSupport.beginTransaction();
			userRepository.create(user);
			EntityManagerSupport.commit();
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}

	public void saveApplicationId(Long userId, Long applicationId) {
		cache.put(userId.toString(), applicationId.toString());
	}

	public Long retrieveApplicationId(Long userId) {
		String applicationID = cache.get(userId.toString());
		if (applicationID == null)
			return null;
		return Long.parseLong(applicationID);

	}
	
	public List<BigDecimal[]> findAllActiveSprocketUserIds() {
		try {
			return new SprocketUserRepositoryJpaImpl().findAllActiveSprocketUserIds();
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}
	
	/***
	 * find users in the given ids from the DB
	 * 
	 * @return all users in DB
	 */
	public List<SprocketUser> findSprocketUsersInRange(List<Long> userIds) {
		try {
			return new SprocketUserRepositoryJpaImpl().findSprocketUsersInRange(userIds);
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}
}
