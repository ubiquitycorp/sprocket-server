package com.ubiquity.social.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ubiquity.media.domain.Image;
import com.ubiquity.media.domain.Video;

@Entity
@Table(name = "video_content")
public class VideoContent {

	@Id
	@GeneratedValue
	@Column(name = "video_content_id")
	private Long videoContentId;

	@ManyToOne
	@JoinColumn(name = "identity_id")
	private ExternalIdentity identity;

	@Embedded
	private Video video;

	@Embedded
	private Image thumb;

	@Column(name = "last_updated", nullable = false)
	private Long lastUpdated;

	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "description", nullable = false)
	private String description;

	@Column(name = "category", nullable = false)
	private String category;

	public VideoContent(ExternalIdentity identity, Video video, Long lastUpdated) {
		super();
		this.identity = identity;
		this.video = video;
		this.lastUpdated = lastUpdated;
	}

	public Long getVideoContentId() {
		return videoContentId;
	}

	public ExternalIdentity getIdentity() {
		return identity;
	}

	public Video getVideo() {
		return video;
	}

	public Long getLastUpdated() {
		return lastUpdated;
	}

	public Image getThumb() {
		return thumb;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getCategory() {
		return category;
	}

	public static class Builder {
		private Long videoContentId;
		private ExternalIdentity identity;
		private Video video;
		private Image thumb;
		private Long lastUpdated;
		private String title;
		private String description;
		private String category;

		public Builder videoContentId(Long videoContentId) {
			this.videoContentId = videoContentId;
			return this;
		}

		public Builder identity(ExternalIdentity identity) {
			this.identity = identity;
			return this;
		}

		public Builder video(Video video) {
			this.video = video;
			return this;
		}

		public Builder thumb(Image thumb) {
			this.thumb = thumb;
			return this;
		}

		public Builder lastUpdated(Long lastUpdated) {
			this.lastUpdated = lastUpdated;
			return this;
		}

		public Builder title(String title) {
			this.title = title;
			return this;
		}

		public Builder description(String description) {
			this.description = description;
			return this;
		}

		public Builder category(String category) {
			this.category = category;
			return this;
		}

		public VideoContent build() {
			return new VideoContent(this);
		}
	}

	private VideoContent(Builder builder) {
		this.videoContentId = builder.videoContentId;
		this.identity = builder.identity;
		this.video = builder.video;
		this.thumb = builder.thumb;
		this.lastUpdated = builder.lastUpdated;
		this.title = builder.title;
		this.description = builder.description;
		this.category = builder.category;
	}
}
