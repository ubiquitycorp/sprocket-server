package com.ubiquity.social.api.facebook.dto.model;

import com.google.gson.annotations.SerializedName;

public class FacebookEventDto {

	private String id;
	private String name;

	@SerializedName("start_time")
	private String startTime;

	@SerializedName("end_time")
	private String endTime;

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getStartTime() {
		return startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public static class Builder {
		private String id;
		private String name;
		private String startTime;
		private String endTime;

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder startTime(String startTime) {
			this.startTime = startTime;
			return this;
		}

		public Builder endTime(String endTime) {
			this.endTime = endTime;
			return this;
		}

		public FacebookEventDto build() {
			return new FacebookEventDto(this);
		}
	}

	private FacebookEventDto(Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.startTime = builder.startTime;
		this.endTime = builder.endTime;
	}
}
