package com.ubiquity.sprocket.datasync.worker.manager;

public enum ResourceType {
	messages ("messages"),
	activities ("activities"),
	videos ("videos"),
	localfeed ("localfeed"),
	contacts ("contacts/synced");
	
	public String endpointName;
	
	ResourceType(String endpointName){
		this.endpointName = endpointName;
	}

	public String getEndpointName() {
		return endpointName;
	}
}
