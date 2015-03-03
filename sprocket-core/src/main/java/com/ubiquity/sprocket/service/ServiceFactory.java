package com.ubiquity.sprocket.service;

import org.apache.commons.configuration.Configuration;

import com.ubiquity.identity.domain.Admin;
import com.ubiquity.identity.domain.Developer;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.service.AdminAuthService;
import com.ubiquity.identity.service.AuthenticationService;
import com.ubiquity.identity.service.DeveloperAuthService;
import com.ubiquity.identity.service.DeveloperService;
import com.ubiquity.identity.service.UserService;
import com.ubiquity.integration.service.ContactService;
import com.ubiquity.integration.service.ContentService;
import com.ubiquity.integration.service.ExternalIdentityService;
import com.ubiquity.integration.service.SocialService;
import com.ubiquity.media.service.MediaService;

/***
 * Factory class returns a managers as singletons.
 * 
 * @author chris
 *
 */
public class ServiceFactory {
		
	private static Configuration configuration;
	private static Configuration errorsConfiguration;
	private static AuthenticationService<User> userAuthService;
	private static AuthenticationService<Admin> adminAuthService;
	private static AuthenticationService<Developer> developerAuthService;
	private static UserService userService;
	private static SocialService socialService;
	private static ClientConfigurationService clientConfigurationService;
	private static SearchService searchService;
	private static ContentService contentService;
	private static ContactService contactService;
	private static AnalyticsService analyticsService;
	private static ExternalIdentityService externalIdentityService;
	private static ErrorsConfigurationService errorsConfigurationService;
	private static EmailService emailService;
	private static LocationService locationService;
	private static FavoriteService favoriteService;
	private static MediaService mediaService;
	private static DeveloperService developerService;
	
	/***
	 * Initializes all services with the specified configuration
	 * 
	 * @param configuration
	 */
	public static void initialize(Configuration config, Configuration errorsConfig) {
		configuration = config;
		errorsConfiguration = errorsConfig;
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
	 * Returns client configuration service
	 * 
	 * @return
	 */
	public static ClientConfigurationService getClientConfigurationService() {
		if(clientConfigurationService == null)
			clientConfigurationService = new ClientConfigurationService(configuration);
		return clientConfigurationService;
	}
	
	
	/***
	 * Returns location service
	 * 
	 * @return
	 */
	public static LocationService getLocationService() {
		if(locationService == null)
			locationService = new LocationService(configuration);
		return locationService;
	}
	
	/***
	 * Returns location service 
	 * @return
	 */
	public static FavoriteService getFavoriteService() {
		if(favoriteService == null)
			favoriteService = new FavoriteService(configuration);
		return favoriteService;
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
	 * Creates or returns a user authentication manager
	 * @return
	 */
	public static AuthenticationService<User> getUserAuthService() {
		if(userAuthService == null)
			userAuthService = new SprocketUserAuthService(configuration);
		return userAuthService;
	}
	
	/***
	 * Creates or returns a user authentication manager
	 * @return
	 */
	public static AuthenticationService<Admin> getAdminAuthService() {
		if(adminAuthService == null)
			adminAuthService = new AdminAuthService(configuration);
		return adminAuthService;
	}
	
	/***
	 * Creates or returns a user authentication manager
	 * @return
	 */
	public static AuthenticationService<Developer> getDevloperAuthService() {
		if(developerAuthService == null)
			developerAuthService = new DeveloperAuthService(configuration);
		return developerAuthService;
	}
	
	/***
	 * Creates or returns an ExternalNetwork manager
	 * @return
	 */
	public static ExternalIdentityService getExternalIdentityService() {
		if(externalIdentityService == null)
			externalIdentityService = new ExternalIdentityService(configuration);
		return externalIdentityService;
	}
	
	/**
	 * Creates or returns a new errors configuration service
	 * @return
	 */
	public static ErrorsConfigurationService getErrorsConfigurationService() {
		if(errorsConfigurationService == null)
			errorsConfigurationService = new ErrorsConfigurationService(errorsConfiguration);
		return errorsConfigurationService;
	}
	
	/**
	 * Creates or returns a new email service
	 * @return
	 */
	public static EmailService getEmailService() {
		if(emailService == null)
			emailService = new EmailService(configuration);
		return emailService;
	}
	
	/**
	 * Creates or returns a new media service
	 * @return
	 */
	public static MediaService getMediaService() {
		if(mediaService == null)
			mediaService = new MediaService(configuration);
		return mediaService;
	}
	
	public static DeveloperService getDeveloperService() {
		if (developerService == null)
			developerService = new DeveloperService(configuration);
		return developerService;
	}
}

