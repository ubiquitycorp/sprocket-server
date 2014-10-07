package com.ubiquity.sprocket.datasync.worker.mq.consumer;

import java.util.Arrays;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.amqp.AbstractConsumerThread;
import com.niobium.amqp.MessageQueueChannel;
import com.ubiquity.content.domain.VideoContent;
import com.ubiquity.messaging.MessageConverter;
import com.ubiquity.messaging.format.Message;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.sprocket.datasync.worker.manager.DataSyncProcessor;
import com.ubiquity.sprocket.messaging.MessageConverterFactory;
import com.ubiquity.sprocket.messaging.definition.ExternalIdentityActivated;
import com.ubiquity.sprocket.messaging.definition.UserEngagedActivity;
import com.ubiquity.sprocket.messaging.definition.UserEngagedDocument;
import com.ubiquity.sprocket.messaging.definition.UserEngagedVideo;
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
			log.info("message received: {}", message);
			if(message.getType().equals(
					ExternalIdentityActivated.class.getSimpleName())) {
				DataSyncProcessor dataSyncManager = new DataSyncProcessor();
				dataSyncManager.processSync((ExternalIdentityActivated)message.getContent());
			}
			else if(message.getType().equals(UserEngagedDocument.class.getSimpleName()))
				process((UserEngagedDocument)message.getContent());
			else if(message.getType().equals(UserEngagedVideo.class.getSimpleName()))
				process((UserEngagedVideo)message.getContent());
			else if(message.getType().equals(UserEngagedActivity.class.getSimpleName()))
				process((UserEngagedActivity)message.getContent());
		} catch (Exception e) {
			log.error("Could not process, message: {}, root cause message: {}",ExceptionUtils.getMessage(e), ExceptionUtils.getRootCauseMessage(e));
			e.printStackTrace();
		}
	}

	private void process(UserEngagedDocument engagedDocument) {
		log.debug("found: {}", engagedDocument);
			
		String dataType = engagedDocument.getDataType();
		if(dataType.equalsIgnoreCase(Activity.class.getSimpleName())) {
			// persist it or update the activity if it exists already
			log.debug("saving the activity to db...");
			Activity activity = engagedDocument.getActivity();
			activity = ServiceFactory.getSocialService().findOrCreate(activity);

			// index for search (this will update the index if the record exists already)
			ServiceFactory.getSearchService().indexActivities(null,
					Arrays.asList(new Activity[] { activity }), true);
			
		} else if(dataType.equalsIgnoreCase(VideoContent.class.getSimpleName())) {
			// persist it or update the activity if it exists already
			log.debug("saving the video to db...");
			VideoContent videoContent = engagedDocument.getVideoContent();
			videoContent = ServiceFactory.getContentService().findOrCreate(videoContent);

			// index for search (this will update the index if the record exists already)
			ServiceFactory.getSearchService().indexVideos(null,
					Arrays.asList(new VideoContent[] { videoContent }), true);
		}

	}

	private void process(UserEngagedVideo engagedVideo) {
		// persist it or update the activity if it exists already
		log.debug("indexing this video to db...");
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
}
