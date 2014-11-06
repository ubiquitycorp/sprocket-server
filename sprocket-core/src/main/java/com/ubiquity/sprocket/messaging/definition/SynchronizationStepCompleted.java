package com.ubiquity.sprocket.messaging.definition;

public class SynchronizationStepCompleted {

	private Integer externalNetworkId;
	private String message;
	private Long timestamp;
	private String resourcePath;
	private String resourceType;
	private Integer records;

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

	public Integer getRecords() {
		return records;
	}

	public String getResourceType() {
		return resourceType;
	}

	@Override
	public String toString() {
		return "SynchronizationStepCompleted [network=" + externalNetworkId
				+ ", message=" + message + ", timestamp=" + timestamp
				+ ", resourcePath=" + resourcePath + "]";
	}

	public static class Builder {
		private Integer externalNetworkId;
		private String message;
		private Long timestamp;
		private String resourcePath;
		private String resourceType;
		private Integer records;

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

		public Builder records(Integer records) {
			this.records = records;
			return this;
		}

		public SynchronizationStepCompleted build() {
			return new SynchronizationStepCompleted(this);
		}
	}

	private SynchronizationStepCompleted(Builder builder) {
		this.externalNetworkId = builder.externalNetworkId;
		this.message = builder.message;
		this.timestamp = builder.timestamp;
		this.resourcePath = builder.resourcePath;
		this.resourceType = builder.resourceType;
		this.records = builder.records;
	}
}
