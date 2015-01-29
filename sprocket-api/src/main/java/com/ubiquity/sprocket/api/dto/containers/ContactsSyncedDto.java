package com.ubiquity.sprocket.api.dto.containers;

import java.util.LinkedList;
import java.util.List;

import com.ubiquity.sprocket.api.dto.model.social.ContactDto;

public class ContactsSyncedDto {
	private List<ContactDto> added = new LinkedList<ContactDto>();
	
	private List<ContactDto> updated = new LinkedList<ContactDto>();
	
	private List<Long> deleted = new LinkedList<Long>();
	
	public List<ContactDto> getAdded() {
		return added;
	}
	public List<ContactDto> getUpdated() {
		return updated;
	}
	public List<Long> getDeleted() {
		return deleted;
	}
	
	
}
