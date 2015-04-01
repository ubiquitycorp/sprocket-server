package com.ubiquity.sprocket.network.api.dto.model;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author mina.shafik
 *
 */

public class Conversation {

	private String conversationIdentifier;
	
	private String conversationName;

	private Set<Contact> receivers = new HashSet<Contact>();
	
	private Set<Message> messages = new HashSet<Message>();
	
	public String getConversationIdentifier() {
		return conversationIdentifier;
	}
	
	public String getConversationName() {
		return conversationName;
	}

	public Set<Contact> getReceivers() {
		return receivers;
	}
	
	public Set<Message> getMessages() {
		return messages;
	}
	
	public static class Builder {
		private String conversationIdentifier;
		private String conversationName;

		public Builder conversationIdentifier(String conversationIdentifier) {
			this.conversationIdentifier = conversationIdentifier;
			return this;
		}
		public Builder conversationName(String conversationName) {
			this.conversationName = conversationName;
			return this;
		}

		public Conversation build() {
			return new Conversation(this);
		}
	}
	private Conversation(Builder builder) {
		this.conversationIdentifier = builder.conversationIdentifier;
		this.conversationName = builder.conversationName;
	}
}
