package com.ubiquity.sprocket.messaging;

import com.ubiquity.messaging.MessageConverter;
import com.ubiquity.sprocket.messaging.definition.SocialIdentityActivated;

public class MessageConverterFactory {
	
	private static MessageConverter converter;
	
	public static MessageConverter getMessageConverter() {
		if(converter == null) {
			converter = new MessageConverter.Builder()
				.registerMessageType(SocialIdentityActivated.class)
				.build();
		}
		return converter;
	}

}
