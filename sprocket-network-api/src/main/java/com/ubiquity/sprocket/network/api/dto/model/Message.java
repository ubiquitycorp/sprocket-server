package com.ubiquity.sprocket.network.api.dto.model;


public class Message {

	private String title;
	
	private String body;

	private Long sentDate;
	
	private Conversation conversation;

	private String externalIdentifier;

	private Contact sender;
	
	private Long lastUpdated;
	

	/**
	 * Default constructor required by JPA
	 */
	protected Message() {
	}

	public String getTitle() {
		return title;
	}

	public String getBody() {
		return body;
	}

	public String getExternalIdentifier() {
		return externalIdentifier;
	}

	public Long getSentDate() {
		return sentDate;
	}

	public Contact getSender() {
		return sender;
	}

	public void setSender(Contact sender) {
		this.sender = sender;
	}
	
	public Conversation getConversation() {
		return conversation;
	}
	
	public Long getLastUpdated() {
		return lastUpdated;
	}

	public static class Builder {
		private String title;
		private String body;
		private Long sentDate;
		private String externalIdentifier;
		private Conversation conversation;
		private Contact sender;
		private Long lastUpdated;

		public Builder title(String title) {
			this.title = title;
			return this;
		}

		public Builder body(String body) {
			this.body = body;
			return this;
		}

		public Builder sentDate(Long sentDate) {
			this.sentDate = sentDate;
			return this;
		}
		
		public Builder lastUpdated(Long lastUpdated) {
			this.lastUpdated = lastUpdated;
			return this;
		}
		
		public Builder externalIdentifier(String externalIdentifier) {
			this.externalIdentifier = externalIdentifier;
			return this;
		}
		
		public Builder conversation(Conversation conversation)
		{
			this.conversation = conversation;
			return this;
		}

		public Builder sender(Contact sender) {
			this.sender = sender;
			return this;
		}

		public Message build() {
			return new Message(this);
		}
	}

	private Message(Builder builder) {
		this.title = builder.title;
		this.body = builder.body;
		this.sentDate = builder.sentDate;
		this.externalIdentifier = builder.externalIdentifier;
		this.conversation = builder.conversation;
		this.lastUpdated = builder.lastUpdated;
		this.sender = builder.sender;
	}

}