package com.ubiquity.sprocket.messaging.definition;

import com.ubiquity.integration.domain.ExternalNetwork;

public class SynchronizationStepCompleted {

	private ExternalNetwork network;
	private String message;
	private Long timestamp;
	private String resourcePath;
	private Integer records;

	public Long getTimestamp() {
		return timestamp;
	}

	public String getResourcePath() {
		return resourcePath;
	}

	public ExternalNetwork getNetwork() {
		return network;
	}

	public String getMessage() {
		return message;
	}

	public Integer getRecords() {
		return records;
	}

	@Override
	public String toString() {
		return "SynchronizationStepCompleted [network=" + network
				+ ", message=" + message + ", timestamp=" + timestamp
				+ ", resourcePath=" + resourcePath + "]";
	}

	public static class Builder {
		private ExternalNetwork network;
		private String message;
		private Long timestamp;
		private String resourcePath;
		private Integer records;

		public Builder network(ExternalNetwork network) {
			this.network = network;
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

		public Builder records(Integer records) {
			this.records = records;
			return this;
		}

		public SynchronizationStepCompleted build() {
			return new SynchronizationStepCompleted(this);
		}
	}

	private SynchronizationStepCompleted(Builder builder) {
		this.network = builder.network;
		this.message = builder.message;
		this.timestamp = builder.timestamp;
		this.resourcePath = builder.resourcePath;
		this.records = builder.records;
	}
}
