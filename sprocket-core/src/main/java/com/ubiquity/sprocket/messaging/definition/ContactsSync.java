package com.ubiquity.sprocket.messaging.definition;

import java.util.List;


public class ContactsSync {

	private List<Long> userIds;
	private Long apllicationID;
	
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

	public Long getApllicationID() {
		return apllicationID;
	}
	
	
}
