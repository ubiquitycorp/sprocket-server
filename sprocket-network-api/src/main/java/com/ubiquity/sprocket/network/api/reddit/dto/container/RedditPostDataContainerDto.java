package com.ubiquity.sprocket.network.api.reddit.dto.container;

import java.util.LinkedList;
import java.util.List;

import com.ubiquity.sprocket.network.api.reddit.dto.model.RedditPostDataDto;

public class RedditPostDataContainerDto {

	private RedditDataDto data = new RedditDataDto();

	public RedditDataDto getData() {
		return data;
	}

	public class RedditDataDto {
		private String modhash;
		private List<RedditPostDataDto> children = new LinkedList<RedditPostDataDto>();
		private String after;
		private String before;

		public String getModhash() {
			return modhash;
		}

		public List<RedditPostDataDto> getChildren() {
			return children;
		}

		public String getAfter() {
			return after;
		}

		public String getBefore() {
			return before;
		}
		
	}
}
