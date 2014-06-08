package com.ubiquity.sprocket.service;

import org.apache.commons.configuration.Configuration;

import com.ubiquity.identity.service.AuthenticationService;
import com.ubiquity.identity.service.UserService;
import com.ubiquity.social.service.ContactService;
import com.ubiquity.social.service.SocialService;

/***
 * Factory class returns a managers as singletons.
 * 
 * @author chris
 *
 */
public class ServiceFactory {
		
	private static Configuration configuration;
	private static AuthenticationService authenticationService;
	private static UserService userService;
	private static SocialService socialService;
	private static SearchService searchService;
	private static ContentService contentService;
	private static ContactService contactService;
	private static AnalyticsService analyticsService;
	
	/***
	 * Initializes all services with the specified configuration
	 * 
	 * @param configuration
	 */
	public static void initialize(Configuration config) {
		configuration = config;
	}
	
	
	/***
	 * Returns analytics service
	 * 
	 * @return
	 */
	public static AnalyticsService getAnalyticsService() {
		if(analyticsService == null)
			analyticsService = new AnalyticsService(configuration);
		return analyticsService;
	}
	
	/***
	 * Returns content service
	 * 
	 * @return
	 */
	public static ContentService getContentService() {
		if(contentService == null)
			contentService = new ContentService(configuration);
		return contentService;
	}
	
	/***
	 * Returns contact service
	 * 
	 * @return
	 */
	public static ContactService getContactService() {
		if(contactService == null)
			contactService = new ContactService(configuration);
		return contactService;
	}
	
	
	/***
	 * Returns search service
	 * 
	 * @return
	 */
	public static SearchService getSearchService() {
		if(searchService == null)
			searchService = new SearchService(configuration);
		return searchService;
	}
	/***
	 * Creates or returns user manager
	 * 
	 * @return
	 */
	public static SocialService getSocialService() {
		if(socialService == null)
			socialService = new SocialService(configuration);
		return socialService;
	}
	
	
	/**
	 * Creates or returns a new user manager
	 * @return
	 */
	public static UserService getUserService() {
		if(userService == null)
			userService = new UserService(configuration);
		return userService;
	}

	
	/***
	 * Creates or returns an authentication manager
	 * @return
	 */
	public static AuthenticationService getAuthenticationService() {
		if(authenticationService == null)
			authenticationService = new AuthenticationService(configuration);
		return authenticationService;
	}
}

