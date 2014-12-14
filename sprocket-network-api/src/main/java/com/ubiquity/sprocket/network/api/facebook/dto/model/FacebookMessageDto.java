package com.ubiquity.sprocket.network.api.facebook.dto.model;

import java.util.Map;

public class FacebookMessageDto {
	
	private String id;
	private String message;
	private Map<String, Object> from;
	private Long createdTime;

	public String getMessage() {
		return message;
	}


	public String getId() {
		return id;
	}


	public Long getCreatedTime() {
		return createdTime;
	}

	public Map<String, Object> getFrom() {
		return from;
	}
	public static class Builder {
		private String id;
		private String message;
		private Map<String, Object> from;
		private Long createdTime;
		
		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder message(String message) {
			this.message = message;
			return this;
		}
		
		public Builder from(Map<String, Object> from) {
			this.from = from;
			return this;
		}

		public Builder createdTime(Long createdTime) {
			this.createdTime = createdTime;
			return this;
		}

		public FacebookMessageDto build() {
			return new FacebookMessageDto(this);
		}
	}

	private FacebookMessageDto(Builder builder) {
		this.id = builder.id;
		this.message = builder.message;
		this.from= builder.from;
		this.createdTime= builder.createdTime;
	}
	
	

}
