package com.ubiquity.sprocket.service;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.identity.domain.User;
import com.ubiquity.social.domain.Activity;

public class AnalyticsService {
		
	private Logger log = LoggerFactory.getLogger(getClass());
	/***
	 * Sets up redis cache interfaces for each event type
	 * 
	 * @param configuration
	 */
	public AnalyticsService(Configuration configuration) {
		
	}

	public void track(User user, Activity activity) {
		log.debug("tracking activity {}", activity);
	}
	
	
}
