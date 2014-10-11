package com.ubiquity.sprocket.messaging.definition;

import com.ubiquity.integration.domain.ExternalNetwork;

public class SynchronizationStarted {
	
	private ExternalNetwork network;

	
	public SynchronizationStarted(ExternalNetwork network) {
		super();
		this.network = network;
	}


	public ExternalNetwork getNetwork() {
		return network;
	}


	@Override
	public String toString() {
		return "SynchronizationStarted [network=" + network + "]";
	}
	
	

}
