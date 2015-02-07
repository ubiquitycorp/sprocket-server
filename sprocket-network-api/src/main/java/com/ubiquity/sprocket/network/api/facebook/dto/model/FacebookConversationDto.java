package com.ubiquity.sprocket.network.api.facebook.dto.model;

import com.google.gson.annotations.SerializedName;
import com.ubiquity.sprocket.network.api.facebook.dto.container.FacebookDataDto;


public class FacebookConversationDto {
	
	private String id;
	private FacebookDataDto to;
	private FacebookDataDto comments;
	
	@SerializedName("updated_time")
	private String updatedTime;
	
	public String getId() {
		return id;
	}
	public FacebookDataDto getTo() {
		return to;
	}
	public FacebookDataDto getComments() {
		return comments;
	}
	
	
	public static class Builder {
		private String id;
		private FacebookDataDto to;
		private FacebookDataDto comments;
		
		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder to(FacebookDataDto to) {
			this.to = to;
			return this;
		}
		
		public Builder comments(FacebookDataDto comments) {
			this.comments = comments;
			return this;
		}
		
		public FacebookConversationDto build() {
			return new FacebookConversationDto(this);
		}
	}

	private FacebookConversationDto(Builder builder) {
		this.id = builder.id;
		this.to = builder.to;
		this.comments = builder.comments;
	}
	

}
