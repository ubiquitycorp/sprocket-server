package com.ubiquity.sprocket.network.api.reddit.dto.model;


public class RedditContactDto {
	
	private String id;
	
	private String name;

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	public static class Builder {
		private String id;
		private String name;
		
		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}
		
		public RedditContactDto build() {
			return new RedditContactDto(this);
		}
	}

	private RedditContactDto(Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
	}
	
	
}
