package com.ubiquity.social.api.facebook.dto.model;

import com.ubiquity.social.api.facebook.dto.container.FacebookDataDto;

public class FacebookMessageDto {
	
	private String id;
	private FacebookDataDto to;
	private FacebookDataDto comments;
	
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
