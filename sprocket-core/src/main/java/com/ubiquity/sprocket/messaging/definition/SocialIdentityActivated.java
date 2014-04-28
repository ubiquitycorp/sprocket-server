package com.ubiquity.sprocket.messaging.definition;

/**
 * A message indicating an authentication event
 * @author chris
 *
 */
public class SocialIdentityActivated {

	private Long identityId;
	private Long userId;
	private Integer clientPlatformId;

	public Long getIdentityId() {
		return identityId;
	}

	public Long getUserId() {
		return userId;
	}

	public Integer getClientPlatformId() {
		return clientPlatformId;
	}

	
	@Override
	public String toString() {
		return "SocialIdentityActivated [identityId=" + identityId
				+ ", userId=" + userId + ", clientPlatformId="
				+ clientPlatformId + "]";
	}


	public static class Builder {
		private Long identityId;
		private Long userId;
		private Integer clientPlatformId;

		public Builder identityId(Long identityId) {
			this.identityId = identityId;
			return this;
		}

		public Builder userId(Long userId) {
			this.userId = userId;
			return this;
		}

		public Builder clientPlatformId(Integer clientPlatformId) {
			this.clientPlatformId = clientPlatformId;
			return this;
		}

		public SocialIdentityActivated build() {
			return new SocialIdentityActivated(this);
		}
	}

	private SocialIdentityActivated(Builder builder) {
		this.identityId = builder.identityId;
		this.userId = builder.userId;
		this.clientPlatformId = builder.clientPlatformId;
	}
}
