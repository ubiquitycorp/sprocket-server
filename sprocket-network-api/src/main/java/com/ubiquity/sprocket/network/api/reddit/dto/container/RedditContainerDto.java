package com.ubiquity.sprocket.network.api.reddit.dto.container;

import java.util.List;

public class RedditContainerDto {

		private RedditDataDto data;

		public RedditDataDto getData() {
			return data;
		}

		public class RedditDataDto {
			private String modhash;
			private List<Object> children;
			private String after;
			private String before;

			public String getModhash() {
				return modhash;
			}

			public List<Object> getChildren() {
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
