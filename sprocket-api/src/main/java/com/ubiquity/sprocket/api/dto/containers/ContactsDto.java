package com.ubiquity.sprocket.api.dto.containers;

import java.util.LinkedList;
import java.util.List;

import com.ubiquity.sprocket.api.dto.model.social.ContactDto;

public class ContactsDto {

	private List<ContactDto> contacts = new LinkedList<ContactDto>();

	public List<ContactDto> getContacts() {
		return contacts;
	}
}
