package com.ubiquity.sprocket.network.api.facebook.dto.model;

import com.google.gson.annotations.SerializedName;

public class FacebookPageDto {
	
	private String id;
	private String name;
	private String category;
	private Long likes;
	
	@SerializedName("can_post")
	private Boolean canPost;

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
		return canPost;
	}

	@Override
	public String toString() {
		return "FacebookPageDto [id=" + id + ", name=" + name + ", category="
				+ category + ", likes=" + likes + ", canPost=" + canPost + "]";
	}
	
	
	

}
