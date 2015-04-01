package com.ubiquity.sprocket.network.api.reddit.dto.model;

import com.google.gson.annotations.SerializedName;

public class RedditCommentDto {

	@SerializedName("body_html")
	private String bodyHtml;

	private Object replies;

	private String name;
	@SerializedName("created_utc")
	private Long createdUtc;

	private String author;

	private String id;

	private Boolean likes;

	private Integer score;

	public String getBodyHtml() {
		return bodyHtml;
	}

	public Object getReplies() {
		return replies;
	}

	public String getName() {
		return name;
	}

	public Long getCreatedUtc() {
		return createdUtc;
	}

	public String getAuthor() {
		return author;
	}

	public String getId() {
		return id;
	}

	public Integer getScore() {
		return score;
	}

	public Boolean getLikes() {
		return likes;
	}

	public void setReplies(Object replies) {
		this.replies = replies;
	}

	public static class Builder {
		private String bodyHtml;
		private Object replies;
		private String name;
		private Long createdUtc;
		private String author;
		private String id;
		private Boolean likes;
		private Integer score;

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder bodyHtml(String bodyHtml) {
			this.bodyHtml = bodyHtml;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder createdUtc(Long createdUtc) {
			this.createdUtc = createdUtc;
			return this;
		}

		public Builder replies(Object replies) {
			this.replies = replies;
			return this;
		}

		public Builder likes(Boolean likes) {
			this.likes = likes;
			return this;
		}

		public Builder author(String author) {
			this.author = author;
			return this;
		}

		public Builder score(Integer score) {
			this.score = score;
			return this;
		}

		public RedditCommentDto build() {
			return new RedditCommentDto(this);
		}

	}

	private RedditCommentDto(Builder builder) {
		this.id = builder.id;
		this.bodyHtml = builder.bodyHtml;
		this.name = builder.name;
		this.replies = builder.replies;
		this.createdUtc = builder.createdUtc;
		this.likes = builder.likes;
		this.author = builder.author;
		this.score = builder.score;
	}

}