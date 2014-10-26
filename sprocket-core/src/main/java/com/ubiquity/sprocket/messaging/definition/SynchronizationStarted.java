package com.ubiquity.sprocket.messaging.definition;


public class SynchronizationStarted {
	
	private Integer externalNetworkId;
	private Long timestamp;

	
	public SynchronizationStarted(Integer externalNetworkId, Long timestamp) {
		super();
		this.externalNetworkId = externalNetworkId;
		this.timestamp = timestamp;
	}
	
	public Integer getExternalNetworkId() {
		return externalNetworkId;
	}

	
	public Long getTimestamp() {
		return timestamp;
	}


	@Override
	public String toString() {
		return "SynchronizationStarted [network=" + externalNetworkId + ", timestamp="
				+ timestamp + "]";
	}
	

}
