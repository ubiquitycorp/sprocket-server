package com.ubiquity.sprocket.network.api.youtube.dto.model;

public class YouTubeSearchResultDto {

	private YouTubeCompositeKeyDto id;
	private String etag;
	private YouTubeVideoSnippetDto snippet;

	public YouTubeCompositeKeyDto getId() {
		return id;
	}

	public String getEtag() {
		return etag;
	}

	public YouTubeVideoSnippetDto getSnippet() {
		return snippet;
	}

	public static class Builder {
		private YouTubeCompositeKeyDto id;
		private String etag;
		private YouTubeVideoSnippetDto snippet;

		public Builder id(YouTubeCompositeKeyDto id) {
			this.id = id;
			return this;
		}

		public Builder etag(String etag) {
			this.etag = etag;
			return this;
		}

		public Builder snippet(YouTubeVideoSnippetDto snippet) {
			this.snippet = snippet;
			return this;
		}

		public YouTubeSearchResultDto build() {
			return new YouTubeSearchResultDto(this);
		}
	}

	private YouTubeSearchResultDto(Builder builder) {
		this.id = builder.id;
		this.etag = builder.etag;
		this.snippet = builder.snippet;
	}

}
