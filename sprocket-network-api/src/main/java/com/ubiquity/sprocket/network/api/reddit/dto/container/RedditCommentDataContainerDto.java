package com.ubiquity.sprocket.network.api.reddit.dto.container;

import java.util.LinkedList;
import java.util.List;

import com.ubiquity.sprocket.network.api.reddit.dto.model.RedditCommentDataDto;

public class RedditCommentDataContainerDto {
	
	private RedditDataDto data = new RedditDataDto();
	
	private String kind;

	public RedditDataDto getData() {
		return data;
	}
	
	public String getKind() {
		return kind;
	}

	public class RedditDataDto {
		private String modhash;
		private List<RedditCommentDataDto> children = new LinkedList<RedditCommentDataDto>();
		private String after;
		private String before;

		public String getModhash() {
			return modhash;
		}

		public List<RedditCommentDataDto> getChildren() {
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
