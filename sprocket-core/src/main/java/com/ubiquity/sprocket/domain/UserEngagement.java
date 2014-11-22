package com.ubiquity.sprocket.domain;

import com.ubiquity.identity.domain.User;

/***
 * An entity tracking user engagement
 * 
 * @author chris
 *
 */
public class UserEngagement {
	
	private User user;
	private Long timestamp;
	
	/***
	 * Default constructor initializes with required properties
	 * 
	 * @param user
	 * @param timestamp
	 */
	public UserEngagement(User user, Long timestamp) {
		this.user = user;
		this.timestamp = timestamp;
	}
	public User getUser() {
		return user;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	
	
	

}
