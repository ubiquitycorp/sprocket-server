package com.ubiquity.sprocket.messaging;

import com.ubiquity.messaging.MessageConverter;
import com.ubiquity.sprocket.messaging.definition.ExternalIdentityActivated;
import com.ubiquity.sprocket.messaging.definition.LocationUpdated;
import com.ubiquity.sprocket.messaging.definition.UserAuthenticated;
import com.ubiquity.sprocket.messaging.definition.UserEngagedActivity;
import com.ubiquity.sprocket.messaging.definition.UserEngagedDocument;
import com.ubiquity.sprocket.messaging.definition.UserEngagedVideo;
import com.ubiquity.sprocket.messaging.definition.UserRegistered;

public class MessageConverterFactory {
	
	private static MessageConverter converter;
	
	public static MessageConverter getMessageConverter() {
		if(converter == null) {
			converter = new MessageConverter.Builder()
				.registerMessageType(ExternalIdentityActivated.class)
				.registerMessageType(UserAuthenticated.class)
				.registerMessageType(UserRegistered.class)
				.registerMessageType(UserEngagedActivity.class)
				.registerMessageType(UserEngagedDocument.class)
				.registerMessageType(UserEngagedVideo.class)
				.registerMessageType(LocationUpdated.class)
				.build();
		}
		return converter;
	}

}
