package com.ubiquity.sprocket.network.api.dto.model;

import java.util.HashSet;
import java.util.Locale.Category;
import java.util.Set;

public class Activity  {

	private ActivityType activityType;
	
	private String title;

	private String body;

	private String link;

	private Long creationDate;

	private Contact postedBy;

	private String externalIdentifier;
	
	private Set<Comment> comments = new HashSet<Comment>();

	private Image image;
	
	private Video video;
	
	private AudioTrack audio;

	private Rating rating;
	
	private Integer commentsNum;
	
	private Category category;
	
	private Integer ownerVote;
	
	private Long lastUpdated;

	private Set<String> tags = new HashSet<String>();

	public Set<String> getTags() {
		return tags;
	}

	public String getTitle() {
		return title;
	}

	public String getBody() {
		return body;
	}

	public Long getCreationDate() {
		return creationDate;
	}

	public Image getImage() {
		return image;
	}

	public Contact getPostedBy() {
		return postedBy;
	}
	
	public ActivityType getActivityType() {
		return activityType;
	}

	public void setPostedBy(Contact postedBy) {
		this.postedBy = postedBy;
	}

	public String getExternalIdentifier() {
		return externalIdentifier;
	}

	public String getLink() {
		return link;
	}

	public Video getVideo() {
		return video;
	}

	public AudioTrack getAudio() {
		return audio;
	}
	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
	
	public Rating getRating() {
		return rating;
	}
	
	public Integer getOwnerVote() {
		return ownerVote;
	}

	public Set<Comment> getComments() {
		return comments;
	}

	public void setComments(Set<Comment> comments) {
		this.comments = comments;
	}

	public Integer getCommentsNum() {
		return commentsNum;
	}
	
	public Long getLastUpdated() {
		return lastUpdated;
	}

	public static class Builder {
		private String title;
		private ActivityType activityType;
		private String body;
		private String link;
		private Video video;
		private AudioTrack audio;
		private Long creationDate;
		private Integer ownerVote;
		private Contact postedBy;
		private String externalIdentifier;
		private Image image;
		private Rating rating;
		private Category category;
		private Integer commentsNum;
		private Long lastUpdated;

		public Builder title(String title) {
			this.title = title;
			return this;
		}
		
		public Builder activityType(ActivityType activityType) {
			this.activityType = activityType;
			return this;
		}
		
		public Builder body(String body) {
			this.body = body;
			return this;
		}
		
		public Builder lastUpdated(Long lastUpdated) {
			this.lastUpdated = lastUpdated;
			return this;
		}
		
		public Builder link(String link) {
			this.link = link;
			return this;
		}

		public Builder video(Video video) {
			this.video = video;
			return this;
		}
		
		public Builder audio(AudioTrack audio) {
			this.audio = audio;
			return this;
		}

		public Builder creationDate(Long creationDate) {
			this.creationDate = creationDate;
			return this;
		}


		public Builder postedBy(Contact postedBy) {
			this.postedBy = postedBy;
			return this;
		}
		
		public Builder ownerVote(Integer ownerVote){
			this.ownerVote = ownerVote;
			return this;
		}

		public Builder externalIdentifier(String externalIdentifier) {
			this.externalIdentifier = externalIdentifier;
			return this;
		}

		public Builder image(Image image) {
			this.image = image;
			return this;
		}
		public Builder rating(Rating rating){
			this.rating = rating;
			return this;
		}
		public Builder commentsNum(Integer commentsNum){
			this.commentsNum =commentsNum;
			return this;
		}
		public Builder category(Category category) {
			this.category = category;
			return this;
		}

		public Activity build() {
			return new Activity(this);
		}
	}

	private Activity(Builder builder) {
		this.title = builder.title;
		this.activityType = builder.activityType;
		this.body = builder.body;
		this.link = builder.link;
		this.video = builder.video;
		this.audio = builder.audio;
		this.creationDate = builder.creationDate;
		this.postedBy = builder.postedBy;
		this.externalIdentifier = builder.externalIdentifier;
		this.image = builder.image;
		this.category = builder.category;
		this.rating = builder.rating;
		this.commentsNum = builder.commentsNum;
		this.lastUpdated = builder.lastUpdated;
		this.ownerVote = builder.ownerVote;
	}
}