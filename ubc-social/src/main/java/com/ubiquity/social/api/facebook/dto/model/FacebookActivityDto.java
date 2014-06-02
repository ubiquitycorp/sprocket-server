package com.ubiquity.social.api.facebook.dto.model;

import com.google.gson.annotations.SerializedName;

public class FacebookActivityDto {
	
	private String id;
	private FacebookContactDto from;
	private String message;
	private String name;
	private String description;
	private String story;
	
	@SerializedName("created_time")
	private String createdTime;

	public String getId() {
		return id;
	}
	
	

	public String getStory() {
		return story;
	}



	public String getCreatedTime() {
		return createdTime;
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
	
	
	

}
