package com.ubiquity.sprocket.messaging.definition;

import com.ubiquity.content.domain.VideoContent;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.domain.Message;

public class UserEngagedDocument {

	private Long userId;
	private Activity activity;
	private VideoContent videoContent;
	private Message message;
	private String dataType;
	private String searchTerm;
	
	public UserEngagedDocument(Long userId, Activity activity,
			String searchTerm) {
		super();
		this.userId = userId;
		this.activity = activity;
		this.dataType = activity.getClass().getSimpleName();
		this.searchTerm = searchTerm;
	}
	
	public UserEngagedDocument(Long userId, VideoContent videoContent,
			String searchTerm) {
		super();
		this.userId = userId;
		this.videoContent = videoContent;
		this.dataType = videoContent.getClass().getSimpleName();
		this.searchTerm = searchTerm;
	}
	
	public UserEngagedDocument(Long userId, Message message, String searchTerm) {
		super();
		this.userId = userId;
		this.message = message;
		this.dataType = message.getClass().getSimpleName();
		this.searchTerm = searchTerm;
	}


	public Long getUserId() {
		return userId;
	}
	public Activity getActivity() {
		return activity;
	}
	public VideoContent getVideoContent() {
		return videoContent;
	}
	public Message getMessage() {
		return message;
	}
	public String getDataType() {
		return dataType;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	
	
}
