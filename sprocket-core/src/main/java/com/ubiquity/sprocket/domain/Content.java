package com.ubiquity.sprocket.domain;

import java.util.LinkedList;
import java.util.List;

import com.ubiquity.identity.domain.User;
import com.ubiquity.integration.domain.Activity;
import com.ubiquity.integration.domain.VideoContent;
import com.ubiquity.location.domain.Place;

public class Content {

	private ContentPK contentId;
	private Activity activity;
	private Place place;
	private Document document;
	private VideoContent videoContent;
	private List<UserEngagement> userEngagement = new LinkedList<UserEngagement>();
	
	/***
	 * Convenience method return owner of the embedded content (i.e. Activity)
	 * 
	 * @return owner or null if it does not exist
	 */
	public User getOwner() {
		if(activity != null)
			return activity.getOwner();
		if(videoContent != null)
			return videoContent.getOwner();
		return null;
	}
	
	
	public ContentPK getContentId() {
		return contentId;
	}
	
	public void setContentId(ContentPK contentId) {
		this.contentId = contentId;
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

	public VideoContent getVideoContent() {
		return videoContent;
	}

	
	public List<UserEngagement> getUserEngagement() {
		return userEngagement;
	}


	public static class Builder {
		private ContentPK contentId;
		private Activity activity;
		private Place place;
		private Document document;
		private VideoContent videoContent;

		public Builder contentId(ContentPK contentId) {
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

		public Builder videoContent(VideoContent videoContent) {
			this.videoContent = videoContent;
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
		this.videoContent = builder.videoContent;
	}


	@Override
	public String toString() {
		return "Content [contentId=" + contentId + ", activity=" + activity
				+ ", place=" + place + ", document=" + document
				+ ", videoContent=" + videoContent + ", userEngagement="
				+ userEngagement + "]";
	}
	
	
}
