package com.ubiquity.sprocket.network.api.reddit.dto.model;

public class RedditPostDataDto {
	private String kind;
	private RedditPostDto data ;

	public String getKind() {
		return kind;
	}

	public RedditPostDto getData() {
		return data;
	}
	
	public static class Builder {
		private String kind;
		private RedditPostDto data ;
		
		public Builder kind(String kind) {
			this.kind = kind;
			return this;
		}

		public Builder data(RedditPostDto data) {
			this.data = data;
			return this;
		}
		
		public RedditPostDataDto build() {
			return new RedditPostDataDto(this);
		}
		
	}

	private RedditPostDataDto(Builder builder) {
		this.kind = builder.kind;
		this.data = builder.data;

	}
	
}
