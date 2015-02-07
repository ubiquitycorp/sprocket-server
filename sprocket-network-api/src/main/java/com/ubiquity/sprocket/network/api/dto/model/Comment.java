package com.ubiquity.sprocket.network.api.dto.model;

import java.util.LinkedList;
import java.util.List;

/**
 * @author mina.shafik
 *
 */
public class Comment {
	
	private String body;
	
	private Integer ownerVote;
	
	private String link;

	private Long creationDate;
	
	private Contact postedBy;
	
	private String externalIdentifier;
	
	private Rating rating;
	
	private Long lastUpdated;
	
	private List<Comment> replies = new LinkedList<Comment>();
	
	private Comment parent;
	
	public Integer getOwnerVote() {
		return ownerVote;
	}

	public String getBody() {
		return body;
	}

	public String getLink() {
		return link;
	}

	public Long getCreationDate() {
		return creationDate;
	}
	
	public Contact getPostedBy() {
		return postedBy;
	}

	public String getExternalIdentifier() {
		return externalIdentifier;
	}

	public Rating getRating() {
		return rating;
	}

	public List<Comment> getReplies() {
		return replies;
	}

	public Comment getParent() {
		return parent;
	}
	
	public Long getLastUpdated() {
		return lastUpdated;
	}
	
	
	public void setParent(Comment parent) {
		this.parent = parent;
	}
	
	public void setPostedBy(Contact postedBy) {
		this.postedBy = postedBy;
	}

	public void addReply(Comment comment){
		getReplies().add(comment);
	}
	public static class Builder {
		
		private String body;
		
		private String link;

		private Long creationDate;
		
		private Contact postedBy;
		
		private String externalIdentifier;
		
		private Rating rating;
		
		private Comment parent;
		
		private Integer ownerVote;
		
		public Builder body(String body) {
			this.body = body;
			return this;
		}
		
		public Builder link(String link) {
			this.link = link;
			return this;
		}
		
		public Builder creationDate(Long creationDate) {
			this.creationDate = creationDate;
			return this;
		}
		
		public Builder postedBy(Contact postedBy){
			this.postedBy = postedBy;
			return this;
		}
		public Builder externalIdentifier(String externalIdentifier) {
			this.externalIdentifier = externalIdentifier;
			return this;
		}
		public Builder rating(Rating rating) {
			this.rating = rating;
			return this;
		}
		
		public Builder parent(Comment parent) {
			this.parent = parent;
			return this;
		}
		public Builder ownerVote(Integer ownerVote){
			this.ownerVote = ownerVote;
			return this;
		}

		public Comment build() {
			return new Comment(this);
		}
	}

	private Comment(Builder builder) {
		
		this.body = builder.body;
		this.link = builder.link;
		this.creationDate = builder.creationDate;
		this.postedBy = builder.postedBy;
		this.externalIdentifier = builder.externalIdentifier;
		this.rating = builder.rating;
		this.parent = builder.parent;
		this.ownerVote = builder.ownerVote;
	}
	
}
