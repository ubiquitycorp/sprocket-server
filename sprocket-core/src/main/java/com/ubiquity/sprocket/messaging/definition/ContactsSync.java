package com.ubiquity.sprocket.messaging.definition;

import java.util.List;


public class ContactsSync {

	private List<Long> userIds;
	
	public ContactsSync(List<Long> userIds) {
		this.userIds = userIds;
	}
	
	@Override
	public String toString() {
		return "ContactsSync [userIds=" + userIds + "]";
	}

	public List<Long> getUserIds() {
		return userIds;
	}
}
