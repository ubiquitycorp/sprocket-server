package com.ubiquity.sprocket.network.api.youtube.dto.model;


public class YouTubeVideoDto {
	
	private String id;
	private String etag;
	private YouTubeVideoSnippetDto snippet;
	private YoutubeContentDetails contentDetails;
	
	public String getId() {
		return id;
	}
	public String getEtag() {
		return etag;
	}
	public YouTubeVideoSnippetDto getSnippet() {
		return snippet;
	}
	public YoutubeContentDetails getContentDetails() {
		return contentDetails;
	}
	
	public static class Builder {
		private String id;
		private String etag;
		private YouTubeVideoSnippetDto snippet ;
		private YoutubeContentDetails contentDetails  = new YoutubeContentDetails();;
		
		public Builder id(String id) {
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

		public Builder contentDetails(YoutubeContentDetails contentDetails) {
			this.contentDetails = contentDetails;
			return this;
		}

		public YouTubeVideoDto build() {
			return new YouTubeVideoDto(this);
		}
	}

	private YouTubeVideoDto(Builder builder) {
		this.id = builder.id;
		this.etag = builder.etag;
		this.snippet = builder.snippet;
		this.contentDetails = builder.contentDetails;
	}
	

}
