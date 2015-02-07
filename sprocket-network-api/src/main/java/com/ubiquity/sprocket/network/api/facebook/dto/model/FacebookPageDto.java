package com.ubiquity.sprocket.network.api.facebook.dto.model;


public class FacebookPageDto {
	
	private String id;
	private String name;
	private String category;
	private Long likes;
	private Boolean can_post;

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getCategory() {
		return category;
	}

	public Long getLikes() {
		return likes;
	}

	public Boolean getCanPost() {
		return can_post;
	}
	public static class Builder {
		private String id;
		private String name;
		private String category;
		private Long likes;
		private Boolean can_post;
		
		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder category(String category) {
			this.category = category;
			return this;
		}
		
		public Builder name(String name) {
			this.name = name;
			return this;
		}
		
		public Builder likes(Long likes) {
			this.likes = likes;
			return this;
		}
		
		public Builder can_post(Boolean can_post) {
			this.can_post = can_post;
			return this;
		}


		public FacebookPageDto build() {
			return new FacebookPageDto(this);
		}
	}

	private FacebookPageDto(Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.category= builder.category;
		this.likes= builder.likes;
		this.can_post = builder.can_post;
	}
	

}
