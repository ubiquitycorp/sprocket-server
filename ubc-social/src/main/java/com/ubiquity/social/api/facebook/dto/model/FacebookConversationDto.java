package com.ubiquity.social.api.facebook.dto.model;

import com.google.gson.annotations.SerializedName;
import com.ubiquity.social.api.facebook.dto.container.FacebookDataDto;

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
	
	
	
	

}
