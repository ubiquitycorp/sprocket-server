package com.ubiquity.sprocket.network.api.facebook.dto.model;

import com.google.gson.annotations.SerializedName;

public class FacebookActivityDto {

	private String id;
	private String type;
	private FacebookContactDto from;
	private String message;
	private String name;
	private String description;
	private String story;
	private String link;
	private String picture;
	private String source;

	private Long created_time;

	@SerializedName("status_type")
	private String status_type;

	public String getSource() {
		return source;
	}

	public String getType() {
		return type;
	}

	public String getPicture() {
		return picture;
	}

	public String getLink() {
		return link;
	}

	public String getId() {
		return id;
	}

	public String getStory() {
		return story;
	}

	public Long getCreatedTime() {
		return created_time;
	}

	public FacebookContactDto getFrom() {
		return from;
	}

	public String getMessage() {
		return message;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getStatusType() {
		return status_type;
	}

	public static class Builder {
		private String id;
		private String type;
		private FacebookContactDto from;
		private String message;
		private String name;
		private String description;
		private String story;
		private String link;
		private String picture;
		private String source;
		private Long createdTime;

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}
		public Builder createdTime(Long createdTime){
			this.createdTime = createdTime;
			return this;
		}
		public Builder from(FacebookContactDto from) {
			this.from = from;
			return this;
		}

		public Builder type(String type) {
			this.type = type;
			return this;
		}

		public Builder message(String message) {
			this.message = message;
			return this;
		}

		public Builder description(String description) {
			this.description = description;
			return this;
		}

		public Builder story(String story) {
			this.story = story;
			return this;
		}

		public Builder link(String link) {
			this.link = link;
			return this;
		}
		
		public Builder picture(String picture) {
			this.picture = picture;
			return this;
		}
		
		public Builder source(String source) {
			this.source = source;
			return this;
		}

		public FacebookActivityDto build() {
			return new FacebookActivityDto(this);
		}
	}

	private FacebookActivityDto(Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.type = builder.type;
		this.from = builder.from;
		this.message = builder.message;
		this.description = builder.description;
		this.link = builder.link;
		this.story = builder.story;
		this.picture = builder.picture;
		this.source = builder.source;
		this.created_time = builder.createdTime;
	}

}
