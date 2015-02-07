package com.ubiquity.sprocket.network.api.dto.model;


public class AudioTrack {

	private String url;

	private String itemKey;

	public String getUrl() {
		return url;
	}

	public String getItemKey() {
		return itemKey;
	}

	public static class Builder {
		private String url;
		private String itemKey;

		public Builder url(String url) {
			this.url = url;
			return this;
		}

		public Builder itemKey(String itemKey) {
			this.itemKey = itemKey;
			return this;
		}


		public AudioTrack build() {
			return new AudioTrack(this);
		}
	}

	private AudioTrack(Builder builder) {
		this.url = builder.url;
		this.itemKey = builder.itemKey;
	}
}
