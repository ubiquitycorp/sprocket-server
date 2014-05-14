package com.ubiquity.sprocket.api.dto.model;

public class MessageDto {

	private String subject;
	private String body;
	private Long date;
	private ContactDto sender;

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

	public static class Builder {
		private String subject;
		private String body;
		private Long date;
		private ContactDto sender;

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

		public Builder sender(ContactDto sender) {
			this.sender = sender;
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
		this.sender = builder.sender;
	}
}
