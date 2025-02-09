package com.ubiquity.sprocket.messaging;

import com.ubiquity.messaging.MessageConverter;
import com.ubiquity.sprocket.messaging.definition.ActiveUsersFound;
import com.ubiquity.sprocket.messaging.definition.ContactsSync;
import com.ubiquity.sprocket.messaging.definition.ExternalIdentityActivated;
import com.ubiquity.sprocket.messaging.definition.LocationUpdated;
import com.ubiquity.sprocket.messaging.definition.PlaceLocationUpdated;
import com.ubiquity.sprocket.messaging.definition.SynchronizationCompleted;
import com.ubiquity.sprocket.messaging.definition.SynchronizationStarted;
import com.ubiquity.sprocket.messaging.definition.SynchronizationStepCompleted;
import com.ubiquity.sprocket.messaging.definition.SynchronizationStepNotification;
import com.ubiquity.sprocket.messaging.definition.UserAuthenticated;
import com.ubiquity.sprocket.messaging.definition.UserEngagedActivity;
import com.ubiquity.sprocket.messaging.definition.UserEngagedDocument;
import com.ubiquity.sprocket.messaging.definition.UserEngagedVideo;
import com.ubiquity.sprocket.messaging.definition.UserFavoritePlace;
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
				.registerMessageType(PlaceLocationUpdated.class)
				.registerMessageType(UserFavoritePlace.class)
				.registerMessageType(SynchronizationStepCompleted.class)
				.registerMessageType(SynchronizationCompleted.class)
				.registerMessageType(SynchronizationStarted.class)
				.registerMessageType(SynchronizationStepNotification.class)
				.registerMessageType(ActiveUsersFound.class)
				.registerMessageType(ContactsSync.class)
				.build();
		}
		return converter;
	}

}
