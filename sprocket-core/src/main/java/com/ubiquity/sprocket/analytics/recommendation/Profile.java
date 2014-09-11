package com.ubiquity.sprocket.analytics.recommendation;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.identity.domain.User;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.location.domain.UserLocation;

public class Profile implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private User user;
	private List<Contact> contacts = new LinkedList<Contact>();
	private UserLocation location;
	
	public Profile(User user, UserLocation location) {
		this.user = user;
		this.location = location;
	}

	public User getUser() {
		return user;
	}

	public UserLocation getLocation() {
		return location;
	}
	
	public List<Contact> getContacts() {
		return contacts;
	}

	public Contact getContactForExternalNetwork(ExternalNetwork network) {
		for(Contact contact : contacts) {
			if(contact.getExternalIdentity().getExternalNetwork() == network.ordinal())
				return contact;
		}
		return null;
	}
	
	

}
