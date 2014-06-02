package com.ubiquity.sprocket.messaging.definition;

/**
 * A message indicating a registration event
 * @author chris
 *
 */
public class UserRegistered {
	
	private Long userId;

	public Long getUserId() {
		return userId;
	}

	public UserRegistered(Long userId) {
		super();
		this.userId = userId;
	}
	
	

}
