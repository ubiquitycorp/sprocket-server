package com.ubiquity.identity.service;

import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.Cache;
import com.niobium.repository.CacheRedisImpl;
import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.NativeIdentity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.repository.UserRepository;
import com.ubiquity.identity.repository.UserRepositoryJpaImpl;

public class AuthenticationService {

	private Logger log = LoggerFactory.getLogger(getClass());

	private Cache cache;
	private UserRepository userRepository;

	public AuthenticationService(Configuration configuration) {
		cache = new CacheRedisImpl(
				configuration.getInt("redis.user.session.database"));
		userRepository = new UserRepositoryJpaImpl();
	}

	/***
	 * 
	 * Checks session token cache if a user is authenticated
	 * 
	 * @param sessionToken
	 * @return
	 */
	public boolean isUserAuthenticated(String userId, String apiKey) {
		String cachedApiKey = cache.get(userId);
		if (cachedApiKey.equals(apiKey)) {
			return true;
		} else {
			return false;
		}
	}

	public User authenticate(String username, String password) {
		try {
			User user = userRepository.searchUserByUsernameAndPassword(username, password);
			return user;
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}
	
	public User register(String username, String password, String displayName, ClientPlatform platform) {
		// first check if the user name is take
		try {				
			User user = userRepository.searchUserByUsername(username);
			if(user != null)
				throw new IllegalArgumentException("Username already taken");

			user = new User.Builder()
			.lastUpdated(System.currentTimeMillis())
			.clientPlatform(ClientPlatform.Android)
			.displayName(displayName)
			.build();

			NativeIdentity identity = new NativeIdentity.Builder()
				.isActive(Boolean.TRUE)
				.lastUpdated(System.currentTimeMillis())
				.user(user)
				.username(username)
				.password(password)
				.build();
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
	 * This method checks for this given sessionToken in Redis. If found , then
	 * retrieve it, otherwise it generates the API key for user to use in this
	 * system. It generates random number of length 10 digits, then it encrypts
	 * it using SH1 using DigestUtils plugin It concatenates this encrypted junk
	 * with given session ID and returns this string as the API key
	 * 
	 * @param sessionToken
	 * @return APIKey
	 */
	public String generateAPIKey(String sessionToken) {
		String APIKey;
		// generate random number of length 10 digits
		long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
		APIKey = DigestUtils.shaHex(number + "");
		log.debug("APIKey: {} ", APIKey);
		APIKey += sessionToken;
		return APIKey;
	}
	
	public String generateApiKey() {
		return UUID.randomUUID().toString();
	}

	public void saveAuthkey(Long userId, String apiKey) {
		cache.put(userId.toString(), apiKey);
	}

	public String retrieveAuthkey(Long userId) {
		return cache.get(userId.toString());
	}

}
