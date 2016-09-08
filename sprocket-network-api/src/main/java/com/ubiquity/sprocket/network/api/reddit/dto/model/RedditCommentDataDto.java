package com.ubiquity.sprocket.network.api.reddit.dto.model;



public class RedditCommentDataDto {
	private String kind;
	private RedditCommentDto data;
	public String getKind() {
		return kind;
	}
	public RedditCommentDto getData() {
		return data;
	}
	
	public static class Builder {
		private String kind;
		private RedditCommentDto data ;
		
		public Builder kind(String kind) {
			this.kind = kind;
			return this;
		}

		public Builder data(RedditCommentDto data) {
			this.data = data;
			return this;
		}
		
		public RedditCommentDataDto build() {
			return new RedditCommentDataDto(this);
		}
		
	}
	private RedditCommentDataDto(Builder builder) {
		this.kind = builder.kind;
		this.data = builder.data;

	}

}
