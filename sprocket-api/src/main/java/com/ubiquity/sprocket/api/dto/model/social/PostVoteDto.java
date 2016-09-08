package com.ubiquity.sprocket.api.dto.model.social;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


public class PostVoteDto {
	@Min(-1)
	@Max(1)
	private int direction;
	
	@Min(0)
	@Max(5)
	private int stars;
	
	@NotNull
	private String parentId;

	public String getParentId() {
		return parentId;
	}

	public int getDirection() {
		return direction;
	}

	public int getStars() {
		return stars;
	}

	public static class Builder {
		private int direction;
		private int stars;
		private String parentId;

		public Builder direction(int direction) {
			this.direction = direction;
			return this;
		}

		public Builder stars(int stars) {
			this.stars = stars;
			return this;
		}

		public Builder parentId(String parentId) {
			this.parentId = parentId;
			return this;
		}

		public PostVoteDto build() {
			return new PostVoteDto(this);
		}
	}

	private PostVoteDto(Builder builder) {
		this.stars = builder.stars;
		this.direction = builder.direction;
		this.parentId = builder.parentId;
	}

}
