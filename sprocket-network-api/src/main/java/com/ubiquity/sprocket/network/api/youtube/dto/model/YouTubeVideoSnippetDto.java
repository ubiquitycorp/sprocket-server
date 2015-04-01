package com.ubiquity.sprocket.network.api.youtube.dto.model;

import java.util.Map;

public class YouTubeVideoSnippetDto {

	private String title;
	private String description;
	private String categoryId;
	private String channelTitle;
	private String channelId;
	private String publishedAt;
	private String type;
	private Map<String, Map<String, String>> thumbnails;
	private Map<String, String> resourceId;
	
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

	public Map<String, String> getResourceId() {
		return resourceId;
	}

	public String getType() {
		return type;
	}
	
	public static class Builder {
		private String title;
		private String description;
		private String categoryId;
		private String channelTitle;
		private String channelId;
		private String publishedAt;
		private String type;
		private Map<String, Map<String, String>> thumbnails;
		private Map<String, String> resourceId;
		
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
		
		public Builder snippet(String categoryId) {
			this.categoryId = categoryId;
			return this;
		}

		public Builder thumbnails(Map<String, Map<String, String>> thumbnails) {
			this.thumbnails = thumbnails;
			return this;
		}
		
		public Builder resourceId(Map<String, String> resourceId) {
			this.resourceId = resourceId;
			return this;
		}
		public Builder type(String type) {
			this.type = type;
			return this;
		}
		public YouTubeVideoSnippetDto build() {
			return new YouTubeVideoSnippetDto(this);
		}
	}

	private YouTubeVideoSnippetDto(Builder builder) {
		this.title = builder.title;
		this.description = builder.description;
		this.categoryId = builder.categoryId;
		this.channelTitle = builder.channelTitle;
		this.channelId = builder.channelId;
		this.publishedAt = builder.publishedAt;
		this.type = builder.type;
		this.thumbnails = builder.thumbnails;
		this.resourceId = builder.resourceId;
	}
	
}
