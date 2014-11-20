package com.ubiquity.sprocket.api.dto.model;

import javax.validation.constraints.NotNull;

public class PostCommentDto {

	@NotNull
	private String body;
	@NotNull
	private String parentId;

	public String getBody() {
		return body;
	}

	public String getParentId() {
		return parentId;
	}

	public static class Builder {
		private String body;
		private String parentId;

		public Builder body(String body) {
			this.body = body;
			return this;
		}

		public Builder parentId(String parentId) {
			this.parentId = parentId;
			return this;
		}

		public PostCommentDto build() {
			return new PostCommentDto(this);
		}
	}

	private PostCommentDto(Builder builder) {
		this.body = builder.body;
		this.parentId = builder.parentId;
	}

}