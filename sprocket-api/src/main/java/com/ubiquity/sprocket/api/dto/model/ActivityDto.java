package com.ubiquity.sprocket.api.dto.model;

public class ActivityDto {

	private String title;
	private String body;
	private Long date;
	private ContactDto postedBy;
	private String imageUrl;
	private Integer socialProviderId;

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

	public String getImageUrl() {
		return imageUrl;
	}

	public Integer getSocialProviderId() {
		return socialProviderId;
	}

	public static class Builder {
		private String title;
		private String body;
		private Long date;
		private ContactDto postedBy;
		private String imageUrl;
		private Integer socialProviderId;

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

		public Builder imageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
			return this;
		}

		public Builder socialProviderId(Integer socialProviderId) {
			this.socialProviderId = socialProviderId;
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
		this.imageUrl = builder.imageUrl;
		this.socialProviderId = builder.socialProviderId;
	}
}
