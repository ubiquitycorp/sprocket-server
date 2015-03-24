package com.ubiquity.sprocket.messaging.definition;

import java.util.List;


public class ActiveUsersFound {

	private List<Long> userIds;
	
	private Long applicationID;
	
	public ActiveUsersFound(List<Long> userIds,Long applicationID) {
		this.userIds = userIds;
		this.applicationID = applicationID;
	}
	
	@Override
	public String toString() {
		return "ActiveUsersFound [userIds=" + userIds + "]";
	}

	public List<Long> getUserIds() {
		return userIds;
	}

	public Long getApplicationID() {
		return applicationID;
	}
	
	
}
