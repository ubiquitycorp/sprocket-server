package com.ubiquity.sprocket.messaging.definition;

import com.ubiquity.integration.domain.ExternalNetwork;

public class SynchronizationCompleted {
	
	private Integer externalNetworkId;
	private Long timestamp;

	
	public SynchronizationCompleted(Integer externalNetworkId, Long timestamp) {
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
		return "SynchronizationCompleted [network=" + externalNetworkId + ", timestamp="
				+ timestamp + "]";
	}


	
	

}
