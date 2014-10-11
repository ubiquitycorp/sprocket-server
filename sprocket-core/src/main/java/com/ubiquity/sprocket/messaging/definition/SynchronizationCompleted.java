package com.ubiquity.sprocket.messaging.definition;

import com.ubiquity.integration.domain.ExternalNetwork;

public class SynchronizationCompleted {
	
	private ExternalNetwork network;
	private Long timestamp;

	
	public SynchronizationCompleted(ExternalNetwork network, Long timestamp) {
		super();
		this.network = network;
		this.timestamp = timestamp;
	}


	public ExternalNetwork getNetwork() {
		return network;
	}
	
	public Long getTimestamp() {
		return timestamp;
	}


	@Override
	public String toString() {
		return "SynchronizationCompleted [network=" + network + ", timestamp="
				+ timestamp + "]";
	}


	
	

}
