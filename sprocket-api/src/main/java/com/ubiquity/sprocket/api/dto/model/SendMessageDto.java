package com.ubiquity.sprocket.api.dto.model;

import javax.validation.constraints.NotNull;

import com.ubiquity.identity.domain.ClientPlatform;

public class SendMessageDto {
	private String text;
	private String receiverId;
	private String receiverName;
	private ClientPlatform clientPlatform;
	
	@NotNull
	public String getText() {
		return text;
	}

	public String getReceiverId() {
		return receiverId;
	}
	public String getReceiverName() {
		return receiverName;
	}
	
	public ClientPlatform getClientPlatform() {
		return clientPlatform;
	}

}
