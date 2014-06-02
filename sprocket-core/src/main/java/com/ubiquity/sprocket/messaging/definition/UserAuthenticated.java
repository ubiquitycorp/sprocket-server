package com.ubiquity.sprocket.messaging.definition;

/**
 * A message indicating an authentication event
 * @author chris
 *
 */
public class UserAuthenticated {
	
	private Long userId;

	public Long getUserId() {
		return userId;
	}

	public UserAuthenticated(Long userId) {
		super();
		this.userId = userId;
	}
	
	

}
