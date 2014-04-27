package com.ubiquity.messaging.format;

public class Message {
	
	private String type;
	private Object content;
		
	public Message(Object content) {
		this.type = content.getClass().getSimpleName();
		this.content = content;
	}

	public String getType() {
		return type;
	}
	
	public Object getContent() {
		return content;
	}

}
