package com.ubiquity.social.repository.cache;

/***
 * Centeral class for centralizing references to cache database names and keys
 * @author chris
 *
 */
public class SocialCacheKeys {

	
	/***
	 * Keys for user properties are shortened to save space, as they are associated with every user record
	 * 
	 * @author chris
	 *
	 */
	public static final class UserProperties {
		public static final String CONTACTS = "c";
		public static final String EVENTS = "e";
	}

}
