package com.ubiquity.sprocket.service;

import org.apache.commons.configuration.Configuration;

import com.ubiquity.identity.service.AuthenticationService;
import com.ubiquity.identity.service.UserService;
import com.ubiquity.social.service.ContactService;
import com.ubiquity.social.service.EventService;
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
	private static ContactService contactService;
	private static UserService userService;
	private static EventService eventService;
	private static SocialService socialService;
	
	/***
	 * Initializes all services with the specified configuration
	 * 
	 * @param configuration
	 */
	public static void initialize(Configuration config) {
		configuration = config;
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
	
	/***
	 * Creates or returns user manager
	 * 
	 * @return
	 */
	public static EventService getEventService() {
		if(eventService == null)
			eventService = new EventService(configuration);
		return eventService;
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
	
	
	
	/**
	 * Creates or returns a new client contact manager
	 * 
	 * @return
	 */
	public static ContactService getContactService() {
		if(contactService == null)
			contactService = new ContactService(configuration);
		return contactService;
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
