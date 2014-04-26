package com.ubiquity.sprocket.messaging;

import com.ubiquity.messaging.MessageConverter;

public class MessageConverterFactory {
	
	private static MessageConverter converter;
	
	public static MessageConverter getMessageConverter() {
		if(converter == null) {
			converter = new MessageConverter.Builder()
//				.registerMessageType(Authentication.class)
//				.registerMessageType(LocationUpdate.class)
//				.registerMessageType(ProximityNetworkRoleAssignment.class)
//				.registerMessageType(NodesInProximity.class)
//				.registerMessageType(ProximityNetworkNode.class)
//				.registerMessageType(NodesInArea.class)
				.build();
		}
		return converter;
	}

}
