package com.ubiquity.sprocket.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ubiquity.identity.domain.User;
import com.ubiquity.media.domain.Image;
import com.ubiquity.media.domain.Video;

@Entity
@AttributeOverrides({
		@AttributeOverride(name = "video.contentLength", column = @Column(name = "video_content_length")),
		@AttributeOverride(name = "video.itemKey", column = @Column(name = "video_item_key")),
		@AttributeOverride(name = "video.url", column = @Column(name = "video_url")),
		@AttributeOverride(name = "thumb.contentLength", column = @Column(name = "thumb_content_length")),
		@AttributeOverride(name = "thumb.itemKey", column = @Column(name = "thumb_item_key")),
		@AttributeOverride(name = "thumb.url", column = @Column(name = "thumb_url")),

})
@Table(name = "video_content")
public class VideoContent {

	@Id
	@GeneratedValue
	@Column(name = "video_content_id")
	private Long videoContentId;

	@ManyToOne
	@JoinColumn(name = "owner_id", nullable = false)
	private User owner;

	@Embedded
	private Video video;

	@Embedded
	private Image thumb;

	@Column(name = "content_network")
	private ContentNetwork contentNetwork;

	@Column(name = "last_updated", nullable = false)
	private Long lastUpdated;

	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "description", nullable = false)
	private String description;

	@Column(name = "category", nullable = false)
	private String category;

	public VideoContent(User owner, Video video, Long lastUpdated) {
		super();
		this.owner = owner;
		this.video = video;
		this.lastUpdated = lastUpdated;
	}

	public ContentNetwork getContentNetwork() {
		return contentNetwork;
	}

	public User getOwner() {
		return owner;
	}

	public void setLastUpdated(Long lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public Long getVideoContentId() {
		return videoContentId;
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
		private User owner;
		private Video video;
		private Image thumb;
		private ContentNetwork contentNetwork;
		private Long lastUpdated;
		private String title;
		private String description;
		private String category;

		public Builder videoContentId(Long videoContentId) {
			this.videoContentId = videoContentId;
			return this;
		}

		public Builder owner(User owner) {
			this.owner = owner;
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

		public Builder contentNetwork(ContentNetwork contentNetwork) {
			this.contentNetwork = contentNetwork;
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
		this.owner = builder.owner;
		this.video = builder.video;
		this.thumb = builder.thumb;
		this.contentNetwork = builder.contentNetwork;
		this.lastUpdated = builder.lastUpdated;
		this.title = builder.title;
		this.description = builder.description;
		this.category = builder.category;
	}
}
