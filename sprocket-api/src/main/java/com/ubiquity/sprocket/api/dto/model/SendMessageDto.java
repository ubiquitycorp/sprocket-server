package com.ubiquity.sprocket.api.dto.model;

import javax.validation.constraints.NotNull;

import com.ubiquity.identity.domain.ClientPlatform;

public class SendMessageDto {
	private String subject;
	private String text;
	private Long contactId;
	private ClientPlatform clientPlatform;
	
	
	public String getSubject() {
		return subject;
	}

	@NotNull
	public String getText() {
		return text;
	}

	public Long getContactId() {
		return contactId;
	}
	
	public ClientPlatform getClientPlatform() {
		return clientPlatform;
	}

}
