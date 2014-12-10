package com.ubiquity.sprocket.network.api.dto.model;


public class VideoContent {

	private Video video;

	private Image thumb;
	
	private Long publishedAt;

	private String title;

	private String description;
	
	private String categoryExternalIdentifier;
	
	private Long lastUpdated;

	
	public String getCategoryExternalIdentifier() {
		return categoryExternalIdentifier;
	}

	public Long getPublishedAt() {
		return publishedAt;
	}

	public Video getVideo() {
		return video;
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
	
	public Long getLastUpdated() {
		return lastUpdated;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static class Builder {
		private Video video;
		private Image thumb;
		private Long publishedAt;
		private String title;
		private String description;
		private String categoryExternalIdentifier;
		private Long lastUpdated;

		public Builder video(Video video) {
			this.video = video;
			return this;
		}
		
		public Builder thumb(Image thumb) {
			this.thumb = thumb;
			return this;
		}
		
		public Builder publishedAt(Long publishedAt) {
			this.publishedAt = publishedAt;
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
		
		public Builder categoryExternalIdentifier(String categoryExternalIdentifier) {
			this.categoryExternalIdentifier = categoryExternalIdentifier;
			return this;
		}
		
		public VideoContent build() {
			return new VideoContent(this);
		}
	}

	private VideoContent(Builder builder) {
		this.video = builder.video;
		this.thumb = builder.thumb;
		this.publishedAt = builder.publishedAt;
		this.title = builder.title;
		this.description = builder.description;
		this.lastUpdated = builder.lastUpdated;
		this.categoryExternalIdentifier = builder.categoryExternalIdentifier;
	}
}
