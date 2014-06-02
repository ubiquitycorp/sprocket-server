package com.ubiquity.social.api.linkedin.dto.model;

import java.util.LinkedList;
import java.util.List;

public class LinkedInMessageDto {
	
	private String subject;
	private String body;
	private List<LinkedInRecipientDto> recipients = new LinkedList<LinkedInRecipientDto>();

	public String getSubject() {
		return subject;
	}

	public String getBody() {
		return body;
	}

	public static class Builder {
		private String subject;
		private String body;
		private String[] recipients;
		
		public Builder subject(String subject) {
			this.subject = subject;
			return this;
		}

		public Builder body(String body) {
			this.body = body;
			return this;
		}

		public Builder recipients(String[] recipients) {
			this.recipients = recipients;
			return this;
		}

		public LinkedInMessageDto build() {
			return new LinkedInMessageDto(this);
		}
	}

	private LinkedInMessageDto(Builder builder) {
		this.subject = builder.subject;
		this.body = builder.body;
		for(String id : builder.recipients) {
			this.recipients.add(new LinkedInRecipientDto(new LinkedInPersonDto(id)));
		}
	}
}
