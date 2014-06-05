package com.ubiquity.media.domain;

import java.io.InputStream;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.niobium.repository.cloud.RemoteAsset;

@Embeddable
public class Media implements RemoteAsset {

	@Column(name = "url", nullable = true)
	protected String url;

	@Transient
	protected InputStream inputStream;

	@Column(name = "item_key", nullable = true)
	protected String itemKey;

	@Column(name = "content_length")
	protected Long contentLength;


	/***
	 * Default constructor required by JPA
	 */
	protected Media() {
	}

	public Media(InputStream inputStream, String itemKey, Long contentLength,
			String url) {
		this.inputStream = inputStream;
		this.itemKey = itemKey;
		this.contentLength = contentLength;
		this.url = url;
	}

	public Media(String url) {
		this.url = url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public Long getContentLength() {
		return contentLength;
	}

	@Override
	public String getItemKey() {
		return itemKey;
	}

	@Override
	public InputStream getInputStream() {
		return inputStream;
	}

	public String getUrl() {
		return url;
	}

	public static class Builder {
		private InputStream inputStream;
		private String itemKey;
		private Long contentLength;
		private String url;

		public Builder inputStream(InputStream inputStream) {
			this.inputStream = inputStream;
			return this;
		}

		public Builder itemKey(String itemKey) {
			this.itemKey = itemKey;
			return this;
		}

		public Builder contentLength(Long contentLength) {
			this.contentLength = contentLength;
			return this;
		}

		public Builder url(String url) {
			this.url = url;
			return this;
		}

		public Media build() {
			return new Media(this);
		}
	}

	private Media(Builder builder) {
		this.inputStream = builder.inputStream;
		this.itemKey = builder.itemKey;
		this.contentLength = builder.contentLength;
		this.url = builder.url;
	}
}
