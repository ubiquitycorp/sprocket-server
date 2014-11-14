package com.ubiquity.sprocket.domain;

import com.ubiquity.integration.domain.Activity;
import com.ubiquity.integration.domain.VideoContent;
import com.ubiquity.location.domain.Place;

public class Content {

	private String contentId;
	private Activity activity;
	private Place place;
	private Document document;
	private VideoContent video;

	
	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public String getContentId() {
		return contentId;
	}

	public Activity getActivity() {
		return activity;
	}

	public Place getPlace() {
		return place;
	}

	public Document getDocument() {
		return document;
	}

	public VideoContent getVideo() {
		return video;
	}

	public static class Builder {
		private String contentId;
		private Activity activity;
		private Place place;
		private Document document;
		private VideoContent video;

		public Builder contentId(String contentId) {
			this.contentId = contentId;
			return this;
		}

		public Builder activity(Activity activity) {
			this.activity = activity;
			return this;
		}

		public Builder place(Place place) {
			this.place = place;
			return this;
		}

		public Builder document(Document document) {
			this.document = document;
			return this;
		}

		public Builder video(VideoContent video) {
			this.video = video;
			return this;
		}

		public Content build() {
			return new Content(this);
		}
	}

	private Content(Builder builder) {
		this.contentId = builder.contentId;
		this.activity = builder.activity;
		this.place = builder.place;
		this.document = builder.document;
		this.video = builder.video;
	}
}
