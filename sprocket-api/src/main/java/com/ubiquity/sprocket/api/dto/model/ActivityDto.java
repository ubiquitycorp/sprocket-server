package com.ubiquity.sprocket.api.dto.model;

public class ActivityDto {

	private String title;
	private String body;
	private Long date;
	private ContactDto postedBy;
	private ImageDto photo;
	private VideoDto video;
	private Integer externalNetworkId;
	private String type;
	private String link;

	public String getLink() {
		return link;
	}

	public ImageDto getPhoto() {
		return photo;
	}

	public VideoDto getVideo() {
		return video;
	}

	public String getType() {
		return type;
	}

	public String getTitle() {
		return title;
	}

	public String getBody() {
		return body;
	}

	public Long getDate() {
		return date;
	}

	public ContactDto getPostedBy() {
		return postedBy;
	}

	public Integer externalNetworkId() {
		return externalNetworkId;
	}

	public static class Builder {
		private String title;
		private String body;
		private Long date;
		private ContactDto postedBy;
		private ImageDto photo;
		private VideoDto video;
		private Integer externalNetworkId;
		private String type;
		private String link;

		public Builder title(String title) {
			this.title = title;
			return this;
		}

		public Builder body(String body) {
			this.body = body;
			return this;
		}

		public Builder date(Long date) {
			this.date = date;
			return this;
		}

		public Builder postedBy(ContactDto postedBy) {
			this.postedBy = postedBy;
			return this;
		}

		public Builder photo(ImageDto photo) {
			this.photo = photo;
			return this;
		}

		public Builder video(VideoDto video) {
			this.video = video;
			return this;
		}

		public Builder externalNetworkId(Integer externalNetworkId) {
			this.externalNetworkId = externalNetworkId;
			return this;
		}

		public Builder type(String type) {
			this.type = type;
			return this;
		}

		public Builder link(String link) {
			this.link = link;
			return this;
		}

		public ActivityDto build() {
			return new ActivityDto(this);
		}
	}

	private ActivityDto(Builder builder) {
		this.title = builder.title;
		this.body = builder.body;
		this.date = builder.date;
		this.postedBy = builder.postedBy;
		this.photo = builder.photo;
		this.video = builder.video;
		this.externalNetworkId = builder.externalNetworkId;
		this.type = builder.type;
		this.link = builder.link;
	}
}
