package com.ubiquity.sprocket.api.dto.model.social;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class MessageDto implements Comparable<MessageDto>{

	private String subject;
	private String body;
	private Long date;
	private Long lastMessageDate;

	private ContactDto sender;
	private Integer externalNetworkId;
	
	private Deque<MessageDto> conversation = new LinkedList<MessageDto>();

	private Set<ContactDto> receivers = new HashSet<ContactDto>();
	
	private Long ownerId;
	
	public Integer getExternalNetworkId() {
		return externalNetworkId;
	}

	public String getSubject() {
		return subject;
	}

	public String getBody() {
		return body;
	}

	public Long getDate() {
		return date;
	}

	public ContactDto getSender() {
		return sender;
	}

	
	public Deque<MessageDto> getConversation() {
		return conversation;
	}

	public Long getLastMessageDate() {
		return lastMessageDate;
	}
	
	public void setLastMessageDate(Long lastMessageDate) {
		this.lastMessageDate = lastMessageDate;
	}
	
	public Set<ContactDto> getReceivers() {
		return receivers;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public static class Builder {
		private String subject;
		private String body;
		private Long date;
		private ContactDto sender;
		private Integer externalNetworkId;
		private Long lastMessageDate;
		private Long ownerId;
		
		public Builder subject(String subject) {
			this.subject = subject;
			return this;
		}

		public Builder body(String body) {
			this.body = body;
			return this;
		}

		public Builder date(Long date) {
			this.date = date;
			return this;
		}
		
		public Builder lastMessageDate(Long lastMessageDate) {
			this.lastMessageDate = lastMessageDate;
			return this;
		}
		public Builder sender(ContactDto sender) {
			this.sender = sender;
			return this;
		}

		public Builder externalNetworkId(Integer externalNetworkId) {
			this.externalNetworkId = externalNetworkId;
			return this;
		}
		
		public Builder ownerId(Long ownerId) {
			this.ownerId = ownerId;
			return this;
		}
		
		public MessageDto build() {
			return new MessageDto(this);
		}

		
	}

	private MessageDto(Builder builder) {
		this.subject = builder.subject;
		this.body = builder.body;
		this.date = builder.date;
		this.lastMessageDate = builder.lastMessageDate;
		this.sender = builder.sender;
		this.externalNetworkId = builder.externalNetworkId;
		this.ownerId = builder.ownerId;
	}

	@Override
	public int compareTo(MessageDto message) {
		
		return this.lastMessageDate.compareTo(message.getLastMessageDate());
	}
}
