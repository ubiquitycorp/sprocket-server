package com.ubiquity.giftsender.api.dto.containers;

import java.util.LinkedList;
import java.util.List;

import com.ubiquity.giftsender.api.dto.model.ContactDto;

public class ContactsDto {

	private List<ContactDto> contacts = new LinkedList<ContactDto>();

	public List<ContactDto> getContacts() {
		return contacts;
	}
}
