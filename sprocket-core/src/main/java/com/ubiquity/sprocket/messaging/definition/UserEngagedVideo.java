package com.ubiquity.sprocket.messaging.definition;

import com.ubiquity.content.domain.VideoContent;

public class UserEngagedVideo {

	private Long userId;
	private VideoContent videoContent;

	public UserEngagedVideo(Long userId, VideoContent videoContent) {
		super();
		this.userId = userId;
		this.videoContent = videoContent;
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
