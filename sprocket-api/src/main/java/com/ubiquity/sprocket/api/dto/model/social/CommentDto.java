package com.ubiquity.sprocket.api.dto.model.social;

import java.util.LinkedList;
import java.util.List;

public class CommentDto {
	private Long commentId;
	private String body;
	private String link;
	private Long creationDate;
	private Long lastUpdated;
	private ContactDto postedBy;
	private String externalIdentifier;
	private RatingDto rating;
	private Integer ownerVote;
	private List<CommentDto> replies = new LinkedList<CommentDto>();
	

	public Long getCommentId() {
		return commentId;
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

	public Long getLastUpdated() {
		return lastUpdated;
	}

	public ContactDto getPostedBy() {
		return postedBy;
	}

	public String getExternalIdentifier() {
		return externalIdentifier;
	}

	public RatingDto getRating() {
		return rating;
	}

	public List<CommentDto> getReplies() {
		return replies;
	}
	
	public Integer getOwnerVote() {
		return ownerVote;
	}

	public static class Builder {
		private Long commentId;
		
		private String body;
		
		private String link;

		private Long creationDate;

		private Long lastUpdated;
		
		private ContactDto postedBy;
		
		private String externalIdentifier;
		
		private RatingDto rating;
		
		private Integer ownerVote;
		
		private List<CommentDto> replies = new LinkedList<CommentDto>();
		
		
		public Builder commentId(Long commentId) {
			this.commentId = commentId;
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
		
		public Builder creationDate(Long creationDate) {
			this.creationDate = creationDate;
			return this;
		}
		
		public Builder lastUpdated(Long lastUpdated) {
			this.lastUpdated = lastUpdated;
			return this;
		}
		public Builder postedBy(ContactDto postedBy){
			this.postedBy = postedBy;
			return this;
		}
		public Builder externalIdentifier(String externalIdentifier) {
			this.externalIdentifier = externalIdentifier;
			return this;
		}
		public Builder rating(RatingDto rating) {
			this.rating = rating;
			return this;
		}

		public Builder addReply(CommentDto commentDto) {
			this.replies.add(commentDto);
			return this;
		}
		
		public Builder ownerVote(Integer ownerVote){
			this.ownerVote = ownerVote;
			return this;
		}

		public CommentDto build() {
			return new CommentDto(this);
		}
	}

	private CommentDto(Builder builder) {
		
		this.commentId = builder.commentId;
		this.body = builder.body;
		this.link = builder.link;
		this.creationDate = builder.creationDate;
		this.lastUpdated = builder.lastUpdated;
		this.postedBy = builder.postedBy;
		this.externalIdentifier = builder.externalIdentifier;
		this.rating = builder.rating;
		this.replies = builder.replies;
		this.ownerVote = builder.ownerVote;
	}
	
}
