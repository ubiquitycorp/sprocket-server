package com.ubiquity.sprocket.location.worker.mq.consumer;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.amqp.AbstractConsumerThread;
import com.niobium.amqp.MessageQueueChannel;
import com.ubiquity.identity.domain.User;
import com.ubiquity.location.domain.Location;
import com.ubiquity.location.domain.Place;
import com.ubiquity.location.domain.UserLocation;
import com.ubiquity.messaging.MessageConverter;
import com.ubiquity.messaging.format.Message;
import com.ubiquity.sprocket.messaging.MessageConverterFactory;
import com.ubiquity.sprocket.messaging.definition.LocationUpdated;
import com.ubiquity.sprocket.messaging.definition.PlaceLocationUpdated;
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
			else if (message.getType().equals(PlaceLocationUpdated.class.getSimpleName()))
				process((PlaceLocationUpdated) message.getContent());
		} catch (Exception e) {
			log.error("Could not process, message: {}, root cause message: {}",ExceptionUtils.getMessage(e), ExceptionUtils.getRootCauseMessage(e));
		}
	}

	private void process(LocationUpdated locationUpdated) {
		log.debug("found: {}", locationUpdated);

		// get user entity and then store this in the db (for now)
		User user = ServiceFactory.getUserService().getUserById(locationUpdated.getUserId());
		UserLocation userLocation = new UserLocation.Builder()
			.user(user)
			.location(new Location.Builder()
				.latitude(locationUpdated.getLatitude())
				.longitude(locationUpdated.getLongitude())
				.altitude(locationUpdated.getAltitude()).build())
			.timestamp(locationUpdated.getTimestamp())
			.lastUpdated(System.currentTimeMillis())
			.horizontalAccuracy(locationUpdated.getHorizontalAccuracy())
			.verticalAccuracy(locationUpdated.getVerticalAccuracy())
			.build();
		// Get nearest place to the new user's location
		Place nearestPlace = ServiceFactory.getLocationService().getClosestNeighborhoodIsWithin(userLocation.getLocation());
		userLocation.setNearestPlace(nearestPlace);
		
		// this will update the user's location in the SQL data store
		ServiceFactory.getLocationService().updateLocation(userLocation);
	
	}
	private void process(PlaceLocationUpdated placeLocationUpdated) {
		log.debug("found: {}", placeLocationUpdated);

		// get user entity and then store eshtathis in the db (for now)
		Place place = ServiceFactory.getLocationService().getPlaceByID(placeLocationUpdated.getPlaceId());
		
		if(place.getExternalNetwork() != null)
		{
			place.setBoundingBox(placeLocationUpdated.getGeobox());
			
			// this will update the place's location in the SQL data store
			ServiceFactory.getLocationService().updatePlace(place);
		}
	}
}
