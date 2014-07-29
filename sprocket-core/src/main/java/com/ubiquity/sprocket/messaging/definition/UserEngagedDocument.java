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
	
	public UserEngagedDocument(Long userId, Activity activity,
			String dataType) {
		super();
		this.userId = userId;
		this.activity = activity;
		this.dataType = dataType;
	}
	
	public UserEngagedDocument(Long userId, VideoContent videoContent,
			String dataType) {
		super();
		this.userId = userId;
		this.videoContent = videoContent;
		this.dataType = dataType;
	}
	
	public UserEngagedDocument(Long userId, Message message, String dataType) {
		super();
		this.userId = userId;
		this.message = message;
		this.dataType = dataType;
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

	
	
}
