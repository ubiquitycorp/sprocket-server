package com.ubiquity.sprocket.repository.cache;

import com.ubiquity.social.domain.SocialNetwork;
import com.ubiquity.sprocket.domain.ContentNetwork;

/***
 * Centeral class for storing references to cache database names and keys for content
 * @author chris
 *
 */
public class SprocketCacheKeys {

	private static final String DELIMITER = ":";
	/***
	 * Keys for user properties are shortened to save space, as they are associated with every user record
	 * 
	 * @author chris
	 *
	 */
	public static final class UserProperties {
		public static final String VIDEOS = "videos";
		public static final String MESSAGES = "messages";
	}
	
	
	
	
	/**
	 * Will generate a cache key for each content network
	 * 
	 * @param key
	 * @param network
	 * @return
	 */
	public String generateCacheKeyForContentNetwork(String key, ContentNetwork network) {
		return new StringBuilder(key).append(DELIMITER).append(network.ordinal()).toString();
	}
	
	/**
	 * Will generate a cache key for each social network
	 * 
	 * @param key
	 * @param network
	 * @return
	 */
	public String generateCacheKeyForSocialNetwork(String key, SocialNetwork network) {
		return new StringBuilder(key).append(DELIMITER).append(network.ordinal()).toString();
	}

}
