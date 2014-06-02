package com.ubiquity.sprocket.messaging.definition;

/**
 * A message indicating an authentication event
 * @author chris
 *
 */
public class ExternalIdentityActivated {

	private Long identityId;
	private Long userId;
	private Integer clientPlatformId;
	private Integer contentNetworkId;

	public Integer getContentNetworkId() {
		return contentNetworkId;
	}

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
		return "ExternalIdentityActivated [identityId=" + identityId
				+ ", userId=" + userId + ", clientPlatformId="
				+ clientPlatformId + "]";
	}

	public static class Builder {
		private Long identityId;
		private Long userId;
		private Integer clientPlatformId;
		private Integer contentNetworkId;

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

		public Builder contentNetworkId(Integer contentNetworkId) {
			this.contentNetworkId = contentNetworkId;
			return this;
		}

		public ExternalIdentityActivated build() {
			return new ExternalIdentityActivated(this);
		}
	}

	private ExternalIdentityActivated(Builder builder) {
		this.identityId = builder.identityId;
		this.userId = builder.userId;
		this.clientPlatformId = builder.clientPlatformId;
		this.contentNetworkId = builder.contentNetworkId;
	}
}
