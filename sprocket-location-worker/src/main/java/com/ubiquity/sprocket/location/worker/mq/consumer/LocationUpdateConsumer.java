package com.ubiquity.sprocket.location.worker.mq.consumer;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.amqp.AbstractConsumerThread;
import com.niobium.amqp.MessageQueueChannel;
import com.ubiquity.content.domain.VideoContent;
import com.ubiquity.content.service.ContentService;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.external.domain.Network;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.messaging.MessageConverter;
import com.ubiquity.messaging.format.Message;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.service.SocialService;
import com.ubiquity.sprocket.domain.EngagedActivity;
import com.ubiquity.sprocket.domain.EngagedDocument;
import com.ubiquity.sprocket.domain.EngagedItem;
import com.ubiquity.sprocket.domain.EngagedVideo;
import com.ubiquity.sprocket.domain.Location;
import com.ubiquity.sprocket.messaging.MessageConverterFactory;
import com.ubiquity.sprocket.messaging.definition.ExternalIdentityActivated;
import com.ubiquity.sprocket.messaging.definition.LocationUpdated;
import com.ubiquity.sprocket.messaging.definition.UserEngagedActivity;
import com.ubiquity.sprocket.messaging.definition.UserEngagedVideo;
import com.ubiquity.sprocket.service.ServiceFactory;

public class LocationUpdateConsumer extends AbstractConsumerThread {

	private Logger log = LoggerFactory.getLogger(getClass());

	private MessageConverter messageConverter;

	public LocationUpdateConsumer(MessageQueueChannel queueChannel) {
		super(queueChannel);
		this.messageConverter = MessageConverterFactory.getMessageConverter();
	}

	@Override
	public void processMessage(byte[] msg) {
		// Currently just automatically fan these out to all parties
		try {
			Message message = messageConverter.deserialize(msg, Message.class);
			log.debug("message received: {}", message);
			if(message.getType().equals(
					LocationUpdated.class.getSimpleName()))
				process((LocationUpdated) message.getContent());
		} catch (Exception e) {
			log.error("Could not process, message: {}, root cause message: {}",ExceptionUtils.getMessage(e), ExceptionUtils.getRootCauseMessage(e));
		}
	}

	private void process(LocationUpdated locationUpdated) {
		log.debug("found: {}", locationUpdated);

		// get user entity and then store this in the db (for now)
		User user = ServiceFactory.getUserService().getUserById(locationUpdated.getUserId());
		Location location = new Location.Builder()
			.user(user)
			.latitude(locationUpdated.getLatitude())
			.longitude(locationUpdated.getLongitude())
			.accuracy(locationUpdated.getAccuracy())
			.lastUpdated(locationUpdated.getLastUpdated())
			.altitude(locationUpdated.getAltitude())
			.build();
		
		// this will update the user's location in the SQL data store
		ServiceFactory.getLocationService().updateLocation(location);
	
	}

}
