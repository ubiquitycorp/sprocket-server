package com.ubiquity.sprocket.api.dto.model.social;

import javax.validation.constraints.NotNull;

import com.ubiquity.identity.domain.ClientPlatform;

public class SendMessageDto {
	private String subject;
	private String text;
	private Long receiverId;
	private String receiverName;
	private ClientPlatform clientPlatform;
	
	
	public String getSubject() {
		return subject;
	}

	@NotNull
	public String getText() {
		return text;
	}

	public Long getReceiverId() {
		return receiverId;
	}
	public String getReceiverName() {
		return receiverName;
	}
	
	public ClientPlatform getClientPlatform() {
		return clientPlatform;
	}

}
