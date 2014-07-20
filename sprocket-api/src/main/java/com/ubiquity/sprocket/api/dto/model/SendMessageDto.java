package com.ubiquity.sprocket.api.dto.model;

import javax.validation.constraints.NotNull;

public class SendMessageDto {
	private String subject;
	private String text;
	private Long contactId;
	private Integer clientPlatformId;
	
	
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
	
	public int getClientPlatformId() {
		return clientPlatformId;
	}

}
