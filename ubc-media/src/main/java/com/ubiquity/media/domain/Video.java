package com.ubiquity.media.domain;

import java.io.InputStream;

import com.ubiquity.media.domain.Media.Builder;

public class Video extends Media {

	/**
	 * Constructor initializes video with a required properties to upload to a cdn
	 * 
	 * @param inputStream
	 * @param itemKey
	 * @param contentLength
	 * @param url
	 */
	public Video(InputStream inputStream, String itemKey, Long contentLength,
			String url) {
		super(inputStream, itemKey, contentLength, url);
	}

	public static class Builder {
		private String itemKey;
		private String url;
		

		public Builder url(String url) {
			this.url = url;
			return this;
		}

		public Builder itemKey(String itemKey) {
			this.itemKey = itemKey;
			return this;
		}

		
		public Video build() {
			return new Video(this);
		}
	}

	private Video(Builder builder) {
		super.itemKey = builder.itemKey;
		super.url = builder.url;
	}
}
