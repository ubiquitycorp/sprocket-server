package com.ubiquity.social.api.facebook.dto.model;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class FacebookMessageDto {
	
	private String message;
	
	private Map<String, Object> from;
	
	@SerializedName("created_time")
	private String createdTime;

	public String getMessage() {
		return message;
	}


	public String getCreatedTime() {
		return createdTime;
	}

	public Map<String, Object> getFrom() {
		return from;
	}
	
	

}
