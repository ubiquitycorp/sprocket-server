package com.ubiquity.identity.service;

import org.apache.commons.configuration.Configuration;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.identity.domain.Identity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.repository.IdentityRepository;
import com.ubiquity.identity.repository.IdentityRepositoryJpaImpl;
import com.ubiquity.identity.repository.UserRepository;
import com.ubiquity.identity.repository.UserRepositoryJpaImpl;

/***
 * Service for managing operations for authenticated users. 
 * 
 * @author chris
 *
 */
public class UserService {

	private UserRepository userRepository;
	private IdentityRepository identityRepository;

	/***
	 * Parameterized constructor builds a manager with required configuration property
	 * 
	 * @param configuration
	 */
	public UserService(Configuration configuration) {
		this.userRepository = new UserRepositoryJpaImpl();
		this.identityRepository = new IdentityRepositoryJpaImpl();
	}

	
	/***
	 * Creates a user in the underlying database
	 * 
	 * @param user
	 */
	public void create(User user) {
		try {				
			EntityManagerSupport.beginTransaction();
			userRepository.create(user);
			EntityManagerSupport.commit();
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}

	public void update(Identity identity) {
		try {	
			EntityManagerSupport.beginTransaction();
			identityRepository.update(identity);
			EntityManagerSupport.commit();
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}



	/***
	 * Updates a user in the underlying database
	 * 
	 * @param user
	 */
	public void update(User user) {
		try {	
			EntityManagerSupport.beginTransaction();
			userRepository.update(user);
			EntityManagerSupport.commit();
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}

	/***
	 * Returns a user or an exception if the user does not exist
	 * 
	 * @param userId
	 * @return User 
	 * 
	 * @throws IllegalArgumentException If the user does not exist
	 */
	public User getUserById(Long userId) {
		try {				
			User user = userRepository.read(userId);
			if(user == null)
				throw new IllegalArgumentException("User does not exist");
			return user;
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}
	
	/**
	 * Searches for a user with this identity
	 * 
	 * @param identityId
	 * 
	 * @return user or null if it is not found
	 */
	public User searchUserByIdentityId(Long identityId) {
		try {
			return userRepository.getByIdentityId(identityId);
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
		
	}


}
