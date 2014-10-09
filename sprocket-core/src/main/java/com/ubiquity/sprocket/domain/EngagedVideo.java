package com.ubiquity.sprocket.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.ubiquity.identity.domain.User;
import com.ubiquity.integration.domain.VideoContent;

@Entity
@DiscriminatorValue("video")
public class EngagedVideo extends EngagedItem {

	@ManyToOne
	@JoinColumn(name = "video_content_id")	
	private VideoContent videoContent;
	
	protected EngagedVideo() {}
	
	public EngagedVideo(User user, VideoContent videoContent) {
		super(user);
		this.videoContent = videoContent;
	}

	public VideoContent getVideoContent() {
		return videoContent;
	}
	
	

}
