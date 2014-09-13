package com.ubiquity.sprocket.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ubiquity.content.domain.VideoContent;

@Entity
@Table(name = "recommended_video")
public class RecommendedVideo {
	
	@Id
	@GeneratedValue
	@Column(name = "recommended_video_id")
	private Long recommendedVideoId;
	
	@ManyToOne
	@JoinColumn(name = "video_content_id")
	private VideoContent videoContent;
	
	@Column(name = "group_identifier")
	private String groupIdentifier;

	/***
	 * Default constructor required by JPA
	 */
	protected RecommendedVideo() {}
	/***
	 * Creates a recommended video for the group identifier
	 * @param videoContent
	 * @param groupIdentifier
	 */
	public RecommendedVideo(VideoContent videoContent, String groupIdentifier) {
		this.videoContent = videoContent;
		this.groupIdentifier = groupIdentifier;
	}

	protected Long getRecommendedVideoId() {
		return recommendedVideoId;
	}

	protected VideoContent getVideoContent() {
		return videoContent;
	}

	protected String getGroupIdentifier() {
		return groupIdentifier;
	}

}
