package com.ubiquity.commerce.repository.cache;

/***
 * Centeral class for centralizing references to cache database names and keys
 * @author chris
 *
 */
public class CommerceCacheKeys {
	
	/***
	 * Keys
	 * @author chris
	 *
	 */
	public static final class Keys {
		public static final String ITEMS = "items";
	}
	
	/***
	 * Keys for user properties are shortened to save space, as they are associated with every user record
	 * 
	 * @author chris
	 *
	 */
	public static final class UserProperties {
		public static final String SENT_ITEMS = "si";
		public static final String RECEIVED_ITEMS = "ri";
	}

}
