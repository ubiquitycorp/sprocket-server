package com.ubiquity.sprocket.location.worker.mq.consumer;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.amqp.AbstractConsumerThread;
import com.niobium.amqp.MessageQueueChannel;
import com.ubiquity.identity.domain.User;
import com.ubiquity.messaging.MessageConverter;
import com.ubiquity.messaging.format.Message;
import com.ubiquity.sprocket.domain.Location;
import com.ubiquity.sprocket.domain.UserLocation;
import com.ubiquity.sprocket.messaging.MessageConverterFactory;
import com.ubiquity.sprocket.messaging.definition.LocationUpdated;
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
		UserLocation location = new UserLocation.Builder()
			.user(user)
			.location(new Location.Builder()
				.latitude(locationUpdated.getLatitude())
				.longitude(locationUpdated.getLongitude())
				.altitude(locationUpdated.getAltitude()).build())
			.lastUpdated(locationUpdated.getLastUpdated())
			.accuracy(locationUpdated.getAccuracy())
			.build();
		
		// this will update the user's location in the SQL data store
		ServiceFactory.getLocationService().updateLocation(location);
	
	}

}
