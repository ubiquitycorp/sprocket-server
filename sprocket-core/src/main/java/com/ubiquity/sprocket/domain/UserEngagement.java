package com.ubiquity.sprocket.domain;


/***
 * An entity tracking user engagement
 * 
 * @author chris
 *
 */
public class UserEngagement {

	private static final String DEFAULT_GROUP_MEMBERSHIP = "unassigned";
	
	private Long userId;
	private Long timestamp;
	private ContentPK contentId;
	
	private String groupMembership;

	public Long getUserId() {
		return userId;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public ContentPK getContentId() {
		return contentId;
	}


	public String getGroupMembership() {
		return groupMembership;
	}

	public static class Builder {
		private Long userId;
		private Long timestamp;
		private ContentPK contentId;
		private String groupMembership;

		public Builder userId(Long userId) {
			this.userId = userId;
			return this;
		}

		public Builder timestamp(Long timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		public Builder contentId(ContentPK contentId) {
			this.contentId = contentId;
			return this;
		}

		public Builder groupMembership(String groupMembership) {
			this.groupMembership = groupMembership;
			return this;
		}

		public UserEngagement build() {
			return new UserEngagement(this);
		}
	}

	private UserEngagement(Builder builder) {
		this.userId = builder.userId;
		this.timestamp = builder.timestamp;
		this.contentId = builder.contentId;
		this.groupMembership = builder.groupMembership == null ? DEFAULT_GROUP_MEMBERSHIP : builder.groupMembership;
	}
}
