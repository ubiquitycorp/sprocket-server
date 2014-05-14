package com.ubiquity.sprocket.api.dto.containers;

import java.util.LinkedList;
import java.util.List;

import com.ubiquity.sprocket.api.dto.model.MessageDto;

public class MessagesDto {
	
	private List<MessageDto> messages = new LinkedList<MessageDto>();

	public List<MessageDto> getMessages() {
		return messages;
	}
	
	

}
