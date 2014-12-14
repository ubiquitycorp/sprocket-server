package com.ubiquity.sprocket.network.api.youtube.dto.model;

import java.util.Map;

public class YoutubeContentDetails {
	private YoutubeRelatedPlaylists relatedPlaylists = new YoutubeRelatedPlaylists();
	private Map<String, String> upload;

	public YoutubeRelatedPlaylists getRelatedPlaylists() {
		return relatedPlaylists;
	}

	public Map<String, String> getUpload() {
		return upload;
	}

	public class YoutubeRelatedPlaylists {
		private String likes;
		private String favorites;
		private String uploads;
		private String watchHistory;
		private String watchLater;

		public String getLikes() {
			return likes;
		}

		public String getFavorites() {
			return favorites;
		}

		public String getUploads() {
			return uploads;
		}

		public String getWatchHistory() {
			return watchHistory;
		}

		public String getWatchLater() {
			return watchLater;
		}

		public void setWatchHistory(String watchHistory) {
			this.watchHistory = watchHistory;

		}

	}
}