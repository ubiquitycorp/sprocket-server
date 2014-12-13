package com.ubiquity.sprocket.messaging.definition;

public class SynchronizationStepNotification {

	private Integer externalNetworkId;
	private String message;
	private Long timestamp;
	private String resourcePath;
	private String resourceType;

	public Long getTimestamp() {
		return timestamp;
	}

	public String getResourcePath() {
		return resourcePath;
	}

	public Integer getExternalNetworkId() {
		return externalNetworkId;
	}

	public String getMessage() {
		return message;
	}

	public String getResourceType() {
		return resourceType;
	}

	@Override
	public String toString() {
		return "SynchronizationStepNotification [network=" + externalNetworkId
				+ ", message=" + message + ", timestamp=" + timestamp
				+ ", resourcePath=" + resourcePath + ", resourceType=" + resourceType + "]";
	}

	public static class Builder {
		private Integer externalNetworkId;
		private String message;
		private Long timestamp;
		private String resourcePath;
		private String resourceType;

		public Builder externalNetworkId(Integer externalNetworkId) {
			this.externalNetworkId = externalNetworkId;
			return this;
		}

		public Builder message(String message) {
			this.message = message;
			return this;
		}

		public Builder timestamp(Long timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		public Builder resourcePath(String resourcePath) {
			this.resourcePath = resourcePath;
			return this;
		}
		
		public Builder resourceType(String resourceType) {
			this.resourceType = resourceType;
			return this;
		}

		public SynchronizationStepNotification build() {
			return new SynchronizationStepNotification(this);
		}
	}

	private SynchronizationStepNotification(Builder builder) {
		this.externalNetworkId = builder.externalNetworkId;
		this.message = builder.message;
		this.timestamp = builder.timestamp;
		this.resourcePath = builder.resourcePath;
		this.resourceType = builder.resourceType;
	}
}
