package com.ubiquity.sprocket.messaging.definition;

import java.util.List;


public class ContactsSync {

	private List<Long> userIds;
	private Long applicationID;
	
	public ContactsSync(List<Long> userIds,Long applicationID) {
		this.userIds = userIds;
		this.applicationID = applicationID;
	}
	
	@Override
	public String toString() {
		return "ContactsSync [userIds=" + userIds + "]";
	}

	public List<Long> getUserIds() {
		return userIds;
	}

	public Long getApplicationID() {
		return applicationID;
	}
	
	
}
