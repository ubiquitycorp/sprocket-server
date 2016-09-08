package com.ubiquity.sprocket.network.api.reddit.dto.model;

import com.google.gson.annotations.SerializedName;

public class RedditMessageDto {
	
	@SerializedName("body_html")
	private String bodyHtml;
	private String name;
	@SerializedName("created_utc")
	private String createdUtc;
	private String author;
	private String id;
	private String subject;
	public String getBodyHtml() {
		return bodyHtml;
	}
	public String getName() {
		return name;
	}
	public String getCreatedUtc() {
		return createdUtc;
	}
	public String getAuthor() {
		return author;
	}
	public String getId() {
		return id;
	}
	public String getSubject() {
		return subject;
	}
	
	
}
