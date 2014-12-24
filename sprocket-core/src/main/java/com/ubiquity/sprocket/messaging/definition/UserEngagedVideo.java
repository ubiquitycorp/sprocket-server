package com.ubiquity.sprocket.messaging.definition;

import com.ubiquity.integration.domain.VideoContent;

public class UserEngagedVideo {

	private Long userId;
	private VideoContent videoContent;
	private Long timestamp;

	public UserEngagedVideo(Long userId, VideoContent videoContent) {
		this.userId = userId;
		this.videoContent = videoContent;
	}
	

	public UserEngagedVideo(Long userId, VideoContent videoContent,
			Long timestamp) {
		this.userId = userId;
		this.videoContent = videoContent;
		this.timestamp = timestamp;
	}

	public Long getTimestamp() {
		return timestamp;
	}


	public Long getUserId() {
		return userId;
	}

	public VideoContent getVideoContent() {
		return videoContent;
	}

	@Override
	public String toString() {
		return "UserEngagedVideo [userId=" + userId + ", videoContent="
				+ videoContent + "]";
	}



}
