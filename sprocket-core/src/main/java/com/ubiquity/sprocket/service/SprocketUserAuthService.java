package com.ubiquity.sprocket.service;

import org.apache.commons.configuration.Configuration;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.identity.domain.Application;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.NativeIdentity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.service.UserAuthService;
import com.ubiquity.sprocket.domain.factory.SprocketUserFactory;
import com.ubiquity.sprocket.repository.SprocketUserRepository;
import com.ubiquity.sprocket.repository.SprocketUserRepositoryJpaImpl;

/****
 * authenticates and registers SprocketUser via SDK. This class extends
 * "UserAuthService" to use basic functionalities of it
 * 
 * @author peter.tadros
 * 
 */
public class SprocketUserAuthService extends UserAuthService {
	
	public SprocketUserAuthService(Configuration configuration) {
		super(configuration);
	}

	
	public User register(String username, String password, String firstName,
			String lastName, String displayName, String email,
			ClientPlatform platform, Boolean hasVerified) {
		// first check if the user name is take
		try {
			SprocketUserRepository userRepository = new SprocketUserRepositoryJpaImpl();
			User user = userRepository.searchUserByUsername(username);
			
			if (user != null)
				throw new IllegalArgumentException("Username already taken");

			user = userRepository.findByEmail(email);
			if (user != null)
				throw new IllegalArgumentException(
						"This email is associated with another account");

			user = SprocketUserFactory.createUserWithMinimumRequiredFields(firstName,
					lastName, displayName, email, platform, hasVerified);

			NativeIdentity identity = new NativeIdentity.Builder()
					.isActive(Boolean.TRUE)
					.lastUpdated(System.currentTimeMillis()).user(user)
					.username(username).password(password).build();
			user.getIdentities().add(identity);

			EntityManagerSupport.beginTransaction();
			userRepository.create(user);
			EntityManagerSupport.commit();

			return user;
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}
	/***
	 * Registers a new user using external identifier via SDK
	 * 
	 * @param externalIdentifier
	 * @param platform
	 * @param hasVerified
	 * @param application
	 * @return
	 */
	public User register(String externalIdentifier, ClientPlatform platform,
			Boolean hasVerified, Application application) {
		try {
			SprocketUserRepository userRepository = new SprocketUserRepositoryJpaImpl();
			User user = userRepository.searchByIdentifierAndApplicationId(
					externalIdentifier, application.getAppId());
			if (user != null)
				throw new IllegalArgumentException("User already exists");

			user = SprocketUserFactory
					.createUserWithRequiredFieldsUsingApplication(
							externalIdentifier, platform, hasVerified,
							application);

			EntityManagerSupport.beginTransaction();
			userRepository.create(user);
			EntityManagerSupport.commit();

			return user;
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}

	/***
	 * authenticates a user by an external identifier via SDK
	 * 
	 * @param externalIdentifier
	 * @param application
	 * @return
	 */
	public User authenticate(String externalIdentifier, Application application) {
		try {
			SprocketUserRepository userRepository = new SprocketUserRepositoryJpaImpl();
			User user = userRepository.searchByIdentifierAndApplicationId(
					externalIdentifier, application.getAppId());
			return user;
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}
}
