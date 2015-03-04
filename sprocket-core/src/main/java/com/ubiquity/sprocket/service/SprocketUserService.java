package com.ubiquity.sprocket.service;

import org.apache.commons.configuration.Configuration;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.service.UserService;
import com.ubiquity.sprocket.repository.SprocketUserRepositoryJpaImpl;

public class SprocketUserService extends UserService {

	public SprocketUserService(Configuration configuration) {
		super(configuration);
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
}
