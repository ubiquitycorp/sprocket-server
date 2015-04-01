package com.ubiquity.sprocket.messaging.definition;


public class SynchronizationError {
	private Integer externalNetworkId;
	private Long timestamp;
	private String message;
	
	public Integer getExternalNetworkId() {
		return externalNetworkId;
	}

	public Long getTimestamp() {
		return timestamp;
	}
	
	public String getMessage() {
		return message;
	}
	
	public static class Builder {
		private Integer externalNetworkId;
		private String message;
		private Long timestamp;

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

		public SynchronizationError build() {
			return new SynchronizationError(this);
		}
	}

	private SynchronizationError(Builder builder) {
		this.externalNetworkId = builder.externalNetworkId;
		this.message = builder.message;
		this.timestamp = builder.timestamp;
	}


	@Override
	public String toString() {
		return "SynchronizationError [network=" + externalNetworkId + ", message=" + message + ", timestamp="
				+ timestamp + "]";
	}
}
