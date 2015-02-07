package com.ubiquity.sprocket.network.api.youtube.dto.model;


public class YouTubeCompositeKeyDto {

	private String videoId;
	private String type;

	public String getVideoId() {
		return videoId;
	}

	public String getType() {
		return type;
	}

	public static class Builder {
		private String videoId;
		private String type;

		public Builder videoId(String videoId) {
			this.videoId = videoId;
			return this;
		}

		public Builder type(String type) {
			this.type = type;
			return this;
		}

		public YouTubeCompositeKeyDto build() {
			return new YouTubeCompositeKeyDto(this);
		}
	}

	private YouTubeCompositeKeyDto(Builder builder) {
		this.videoId = builder.videoId;
		this.type = builder.type;
	}

}
