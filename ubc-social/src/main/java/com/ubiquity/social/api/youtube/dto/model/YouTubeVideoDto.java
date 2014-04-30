package com.ubiquity.social.api.youtube.dto.model;

import java.util.Map;

public class YouTubeVideoDto {

	private String id;
	private String title;
	private String description;
	private String categoryId;
	private String channelTitle;
	private String channelId;
	private String publishedAt;
	private Map<String, Map<String, String>> thumbnails;
	
	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	
	public Map<String, Map<String, String>> getThumbnails() {
		return thumbnails;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public String getChannelTitle() {
		return channelTitle;
	}

	public String getChannelId() {
		return channelId;
	}

	public String getPublishedAt() {
		return publishedAt;
	}

	public static class Builder {
		private String id;
		private String title;
		private String description;
		private String categoryId;
		private String channelTitle;
		private String channelId;
		private String publishedAt;

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder title(String title) {
			this.title = title;
			return this;
		}

		public Builder description(String description) {
			this.description = description;
			return this;
		}

		public Builder categoryId(String categoryId) {
			this.categoryId = categoryId;
			return this;
		}

		public Builder channelTitle(String channelTitle) {
			this.channelTitle = channelTitle;
			return this;
		}

		public Builder channelId(String channelId) {
			this.channelId = channelId;
			return this;
		}

		public Builder publishedAt(String publishedAt) {
			this.publishedAt = publishedAt;
			return this;
		}

		public YouTubeVideoDto build() {
			return new YouTubeVideoDto(this);
		}
	}

	private YouTubeVideoDto(Builder builder) {
		this.id = builder.id;
		this.title = builder.title;
		this.description = builder.description;
		this.categoryId = builder.categoryId;
		this.channelTitle = builder.channelTitle;
		this.channelId = builder.channelId;
		this.publishedAt = builder.publishedAt;
	}
}
