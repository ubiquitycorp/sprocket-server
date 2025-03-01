package com.ubiquity.sprocket.api.dto.model.social;

import com.ubiquity.integration.domain.ActivityType;

public class PostActivityDto {
	private Integer activityTypeId;
	private String title; // (text, link)
	private String body; // (text, description in link, caption in photo, audio,
							// video)
	private String link; // (url in link, external_url in audio)
	private String[] data; // (photo, video)
	private String embed; // (video)
	private String pageId; // (page to be posted into)
	private String captcha;
	private String captchaIden;

	public String getTitle() {
		return title;
	}

	public String getBody() {
		return body;
	}

	public String getLink() {
		return link;
	}

	public String[] getData() {
		return data;
	}

	public String getEmbed() {
		return embed;
	}

	public String getPageId() {
		return pageId;
	}
	
	public String getCaptcha() {
		return captcha;
	}
	public String getCaptchaIden() {
		return captchaIden;
	}

	public Integer getActivityTypeId() {
		return activityTypeId;
	}

	public ActivityType getActivityType() {
		return ActivityType.getActivityTypeFromId(activityTypeId);
	}

	public static class Builder {
		private Integer activityTypeId;
		private String title;
		private String body;
		private String link;
		private String[] data;
		private String embed;
		private String pageId;
		private String captcha;
		private String captchaIden;

		public Builder title(String title) {
			this.title = title;
			return this;
		}

		public Builder body(String body) {
			this.body = body;
			return this;
		}

		public Builder link(String link) {
			this.link = link;
			return this;
		}

		public Builder data(String[] data) {
			this.data = data;
			return this;
		}

		public Builder embed(String embed) {
			this.embed = embed;
			return this;
		}

		public Builder activityTypeId(Integer activityTypeId) {
			this.activityTypeId = activityTypeId;
			return this;
		}
		
		public Builder pageId(String pageId) {
			this.pageId = pageId;
			return this;
		}
		
		public Builder captcha(String captcha) {
			this.captcha = captcha;
			return this;
		}
		public Builder captchaIden(String captchaIden) {
			this.captchaIden = captchaIden;
			return this;
		}
		
		public PostActivityDto build() {
			return new PostActivityDto(this);
		}
	}

	private PostActivityDto(Builder builder) {
		this.title = builder.title;
		this.body = builder.body;
		this.link = builder.link;
		this.data = builder.data;
		this.embed = builder.embed;
		this.activityTypeId = builder.activityTypeId;
		this.pageId = builder.pageId;
		this.captcha = builder.captcha;
		this.captchaIden = builder.captchaIden;
	}

	public void validate() {
		ActivityType type = this.getActivityType();
		if (type.equals(ActivityType.STATUS)) {
			if (body == null)
				throw new IllegalArgumentException("body cannot be null");
		} else if (type.equals(ActivityType.VIDEO)) {
			if (embed == null)
				throw new IllegalArgumentException("embed cannot be null");
		} else {
			if (link == null)
				throw new IllegalArgumentException("link cannot be null");
		}
	}

}
