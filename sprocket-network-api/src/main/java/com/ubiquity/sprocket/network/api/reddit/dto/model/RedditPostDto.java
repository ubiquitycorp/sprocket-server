package com.ubiquity.sprocket.network.api.reddit.dto.model;

import com.google.gson.annotations.SerializedName;

public class RedditPostDto {

	@SerializedName("selftext_html")
	private String selfTextHtml;

	private String name;
	@SerializedName("created_utc")
	private Long createdUtc;
	private String subreddit;

	@SerializedName("subreddit_id")
	private String subredditId;

	private String thumbnail;

	private String url;
	
	private Boolean likes;

	private String title;

	@SerializedName("num_comments")
	private Integer commentsNum;

	private String author;
	
	private String id;
	
	private Integer score;
	
	@SerializedName("media_embed")
	private RedditMediaEmbedDto mediaEmbed;
	
	@SerializedName("secure_media_embed")
	private RedditMediaEmbedDto secureMediaEmbed;

	public String getSelfTextHtml() {
		return selfTextHtml;
	}

	public String getName() {
		return name;
	}

	public Long getCreatedUtc() {
		return createdUtc;
	}

	public String getSubreddit() {
		return subreddit;
	}

	public String getSubredditId() {
		return subredditId;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public String getUrl() {
		return url;
	}

	public String getTitle() {
		return title;
	}

	public Integer getCommentsNum() {
		return commentsNum;
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

	public RedditMediaEmbedDto getMediaEmbed() {
		return mediaEmbed;
	}

	public RedditMediaEmbedDto getSecureMediaEmbed() {
		return secureMediaEmbed;
	}
	public static class Builder {
		private String selfTextHtml;
		private String name;
		private Long createdUtc;
		private String subreddit;
		private String subredditId;
		private String thumbnail;
		private String url;
		private Boolean likes;
		private String title;
		private Integer commentsNum;
		private String author;
		private String id;
		private Integer score;
		private RedditMediaEmbedDto mediaEmbed;
		private RedditMediaEmbedDto secureMediaEmbed;
		
		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder selfTextHtml(String selfTextHtml) {
			this.selfTextHtml = selfTextHtml;
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
		
		public Builder subreddit(String subreddit) {
			this.subreddit = subreddit;
			return this;
		}
		
		public Builder subredditId(String subredditId) {
			this.subredditId = subredditId;
			return this;
		}
		
		public Builder thumbnail(String thumbnail) {
			this.thumbnail = thumbnail;
			return this;
		}
		
		public Builder url(String url) {
			this.url = url;
			return this;
		}
		
		public Builder likes(Boolean likes) {
			this.likes = likes;
			return this;
		}
		
		public Builder title(String title) {
			this.title = title;
			return this;
		}
		
		public Builder commentsNum(Integer commentsNum) {
			this.commentsNum = commentsNum;
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
		
		public Builder mediaEmbed(RedditMediaEmbedDto mediaEmbed) {
			this.mediaEmbed = mediaEmbed;
			return this;
		}
		
		public Builder secureMediaEmbed(RedditMediaEmbedDto secureMediaEmbed) {
			this.secureMediaEmbed = secureMediaEmbed;
			return this;
		}
		public RedditPostDto build() {
			return new RedditPostDto(this);
		}
		
		
	}

	private RedditPostDto(Builder builder) {
		this.id = builder.id;
		this.selfTextHtml = builder.selfTextHtml;
		this.name = builder.name;
		this.subreddit = builder.subreddit;
		this.createdUtc = builder.createdUtc;
		this.subredditId = builder.subredditId;
		this.thumbnail = builder.thumbnail;
		this.url = builder.url;
		this.likes = builder.likes;
		this.title = builder.title;
		this.commentsNum = builder.commentsNum;
		this.author = builder.author;
		this.score = builder.score;
		this.mediaEmbed = builder.mediaEmbed;
		this.secureMediaEmbed = builder.secureMediaEmbed;
	}
	

}
