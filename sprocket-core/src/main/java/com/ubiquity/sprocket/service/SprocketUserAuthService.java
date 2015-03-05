package com.ubiquity.sprocket.service;

import org.apache.commons.configuration.Configuration;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.identity.domain.Application;
import com.ubiquity.identity.domain.ClientPlatform;
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
