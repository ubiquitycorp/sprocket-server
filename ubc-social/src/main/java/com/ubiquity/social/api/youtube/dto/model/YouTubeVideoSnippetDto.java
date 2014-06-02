package com.ubiquity.social.api.youtube.dto.model;

import java.util.Map;

public class YouTubeVideoSnippetDto {

	private String title;
	private String description;
	private String categoryId;
	private String channelTitle;
	private String channelId;
	private String publishedAt;
	private Map<String, Map<String, String>> thumbnails;
	
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
}
