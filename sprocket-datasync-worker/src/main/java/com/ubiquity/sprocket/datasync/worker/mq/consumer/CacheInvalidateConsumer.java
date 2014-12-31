package com.ubiquity.sprocket.datasync.worker.mq.consumer;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.amqp.AbstractConsumerThread;
import com.niobium.amqp.MessageQueueChannel;
import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.identity.domain.User;
import com.ubiquity.integration.domain.Activity;
import com.ubiquity.integration.domain.VideoContent;
import com.ubiquity.location.domain.Place;
import com.ubiquity.messaging.MessageConverter;
import com.ubiquity.messaging.format.Message;
import com.ubiquity.sprocket.datasync.worker.manager.DataSyncProcessor;
import com.ubiquity.sprocket.domain.FavoritePlace;
import com.ubiquity.sprocket.messaging.MessageConverterFactory;
import com.ubiquity.sprocket.messaging.definition.ActiveUsersFound;
import com.ubiquity.sprocket.messaging.definition.ExternalIdentityActivated;
import com.ubiquity.sprocket.messaging.definition.UserEngagedActivity;
import com.ubiquity.sprocket.messaging.definition.UserEngagedDocument;
import com.ubiquity.sprocket.messaging.definition.UserEngagedVideo;
import com.ubiquity.sprocket.messaging.definition.UserFavoritePlace;
import com.ubiquity.sprocket.repository.FavoritePlaceRepositoryJpaImpl;
import com.ubiquity.sprocket.service.ServiceFactory;

public class CacheInvalidateConsumer extends AbstractConsumerThread {

	private Logger log = LoggerFactory.getLogger(getClass());

	private MessageConverter messageConverter;

	public CacheInvalidateConsumer(MessageQueueChannel queueChannel) {
		super(queueChannel);
		this.messageConverter = MessageConverterFactory.getMessageConverter();
	}

	@Override
	public void processMessage(byte[] msg) {
		// Currently just automatically fan these out to all parties
		try {
			Message message = messageConverter.deserialize(msg, Message.class);
			log.info("Message received: {}", message);
			if(message.getType().equals(ExternalIdentityActivated.class.getSimpleName())) {
				DataSyncProcessor dataSyncManager = new DataSyncProcessor();
				dataSyncManager.processSync((ExternalIdentityActivated)message.getContent());	
			} if(message.getType().equals(ActiveUsersFound.class.getSimpleName())) {
				process((ActiveUsersFound)message.getContent());
			} else if(message.getType().equals(UserEngagedDocument.class.getSimpleName()))
				process((UserEngagedDocument)message.getContent());
			else if(message.getType().equals(UserEngagedVideo.class.getSimpleName()))
				process((UserEngagedVideo)message.getContent());
			else if(message.getType().equals(UserEngagedActivity.class.getSimpleName()))
				process((UserEngagedActivity)message.getContent());
			else if(message.getType().equals(UserFavoritePlace.class.getSimpleName()))
				process((UserFavoritePlace)message.getContent());
		} catch (Exception e) {
			log.error(Thread.currentThread().getName() + " Could not process, message: {}, root cause message: {}",ExceptionUtils.getMessage(e), ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
	}
	private void process(UserFavoritePlace favoritePlace) {
		// persist it or update the activity if it exists already
		log.debug(Thread.currentThread().getName() + " indexing this favorite place to db...");
		Place place = favoritePlace.getPlace();
		place = ServiceFactory.getLocationService().findOrCreate(place);
		User user = ServiceFactory.getUserService().getUserById(favoritePlace.getUserId());
		FavoritePlace favPlace = new FavoritePlace(user,place,System.currentTimeMillis());
		FavoritePlace favoritePlace2 = new FavoritePlaceRepositoryJpaImpl().getFavoritePlaceByUserIdAndBusinessId(favoritePlace.getUserId(),place.getExternalNetwork(), place.getPlaceId());
		if(favoritePlace2 == null)
		{
			EntityManagerSupport.beginTransaction();
			new FavoritePlaceRepositoryJpaImpl().create(favPlace);
			EntityManagerSupport.commit();
			ServiceFactory.getFavoriteService().setFavoritePlaceCache(user.getUserId(), favoritePlace.getPlace().getExternalNetwork(), favoritePlace.getPlace().getParent().getPlaceId());
		}
		
	}
	private void process(UserEngagedDocument engagedDocument) {
		log.debug("found: {}", engagedDocument);
			
		String dataType = engagedDocument.getDataType();
		if(dataType.equalsIgnoreCase(Activity.class.getSimpleName())) {
			// persist it or update the activity if it exists already
			log.debug(Thread.currentThread().getName() + " saving the activity to db...");
			Activity activity = engagedDocument.getActivity();
			activity = ServiceFactory.getSocialService().findOrCreate(activity);

			// index for search (this will update the index if the record exists already)
			ServiceFactory.getSearchService().indexActivities(null,
					Arrays.asList(new Activity[] { activity }), true);
			
		} else if(dataType.equalsIgnoreCase(VideoContent.class.getSimpleName())) {
			// persist it or update the activity if it exists already
			log.debug(Thread.currentThread().getName() + " saving the video to db...");
			VideoContent videoContent = engagedDocument.getVideoContent();
			videoContent = ServiceFactory.getContentService().findOrCreate(videoContent);

			// index for search (this will update the index if the record exists already)
			ServiceFactory.getSearchService().indexVideos(null,
					Arrays.asList(new VideoContent[] { videoContent }), true);
		}

	}

	private void process(UserEngagedVideo engagedVideo) {
		// persist it or update the activity if it exists already
		log.debug(Thread.currentThread().getName() + " indexing this video to db...");
		VideoContent videoContent = engagedVideo.getVideoContent();
		videoContent = ServiceFactory.getContentService().findOrCreate(videoContent);

		// index for search (this will update the index if the record exists already)
		ServiceFactory.getSearchService().indexVideos(null,
				Arrays.asList(new VideoContent[] { videoContent }), true);

	}

	private void process(UserEngagedActivity engagedActivity) {
		// build it
		Activity activity = engagedActivity.getActivity();

		// persist it or update it if it exists already
		ServiceFactory.getSocialService().findOrCreate(activity);

		// index for search (this will update the index if the record exists already)
		ServiceFactory.getSearchService().indexActivities(
				engagedActivity.getUserId(), 
				Arrays.asList(new Activity[] { activity }), true);		
	}
	
	private void process(ActiveUsersFound activeUsersFound) {
		List<Long> userIds = activeUsersFound.getUserIds();
		List<User> users = ServiceFactory.getUserService().findUsersInRange(userIds);
		DataSyncProcessor dataSyncManager = new DataSyncProcessor(users);
		dataSyncManager.syncData();
	}
}
