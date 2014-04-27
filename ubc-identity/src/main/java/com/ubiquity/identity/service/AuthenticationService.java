package com.ubiquity.identity.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.Cache;
import com.niobium.repository.CacheRedisImpl;

public class AuthenticationService {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	private Cache cache;

	public AuthenticationService(Configuration configuration) {
		cache = new CacheRedisImpl(
				configuration.getInt("redis.user.session.database"));
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

	public void saveAuthkey(Long userId, String apiKey) {
		cache.put(userId.toString(), apiKey);
	}

	public String retrieveAuthkey(Long userId) {
		return cache.get(userId.toString());
	}

}
