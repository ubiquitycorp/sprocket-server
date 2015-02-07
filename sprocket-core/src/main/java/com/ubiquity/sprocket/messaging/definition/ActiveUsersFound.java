package com.ubiquity.sprocket.messaging.definition;

import java.util.List;


public class ActiveUsersFound {

	private List<Long> userIds;
	
	public ActiveUsersFound(List<Long> userIds) {
		this.userIds = userIds;
	}
	
	@Override
	public String toString() {
		return "ActiveUsersFound [userIds=" + userIds + "]";
	}

	public List<Long> getUserIds() {
		return userIds;
	}
}
