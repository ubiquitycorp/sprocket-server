package com.ubiquity.sprocket.datasync.worker.mq.consumer;

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
					ExternalIdentityActivated.class.getSimpleName()))
				process((ExternalIdentityActivated) message.getContent());
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

		// get user entity and then store this in the db (for now)
		User user = ServiceFactory.getUserService().getUserById(engagedDocument.getUserId());	
					
		String dataType = engagedDocument.getDataType();
		if(dataType.equalsIgnoreCase(Activity.class.getSimpleName())) {
			// persist it or update the activity if it exists already
			log.debug("saving the activity to db...");
			Activity activity = engagedDocument.getActivity();
			activity = ServiceFactory.getSocialService().findOrCreate(activity);

			// index for search (this will update the index if the record exists already)
			ServiceFactory.getSearchService().indexActivities(null,
					Arrays.asList(new Activity[] { activity }));

			// track this in db
			EngagedItem engagedItem = new EngagedDocument(user, engagedDocument.getSearchTerm(), activity);
			ServiceFactory.getAnalyticsService().track(engagedItem);

		} else if(dataType.equalsIgnoreCase(VideoContent.class.getSimpleName())) {
			// persist it or update the activity if it exists already
			log.debug("saving the video to db...");
			VideoContent videoContent = engagedDocument.getVideoContent();
			videoContent = ServiceFactory.getContentService().findOrCreate(videoContent);

			// index for search (this will update the index if the record exists already)
			ServiceFactory.getSearchService().indexVideos(null,
					Arrays.asList(new VideoContent[] { videoContent }));

			// track this in db
			EngagedItem engagedItem = new EngagedDocument(user, engagedDocument.getSearchTerm(), videoContent);
			ServiceFactory.getAnalyticsService().track(engagedItem);


		}

	}

	private void process(UserEngagedVideo engagedVideo) {
		// persist it or update the activity if it exists already
		log.debug("saving the video to db...");
		VideoContent videoContent = engagedVideo.getVideoContent();
		videoContent = ServiceFactory.getContentService().findOrCreate(videoContent);

		// index for search (this will update the index if the record exists already)
		ServiceFactory.getSearchService().indexVideos(null,
				Arrays.asList(new VideoContent[] { videoContent }));

		// get user entity and then store this in the db (for now)
		User user = ServiceFactory.getUserService().getUserById(engagedVideo.getUserId());		
		EngagedItem engagedItem = new EngagedVideo(user, videoContent);
		ServiceFactory.getAnalyticsService().track(engagedItem);
	}

	private void process(UserEngagedActivity engagedActivity) {
		log.info("found: {}", engagedActivity);

		// build it
		Activity activity = engagedActivity.getActivity();

		// persist it or update it if it exists already
		ServiceFactory.getSocialService().findOrCreate(activity);

		// index for search (this will update the index if the record exists already)
		ServiceFactory.getSearchService().indexActivities(
				engagedActivity.getUserId(), 
				Arrays.asList(new Activity[] { activity }));

		// get user entity and then store this in the db (for now)
		User user = ServiceFactory.getUserService().getUserById(engagedActivity.getUserId());		
		EngagedItem engagedItem = new EngagedActivity(user, activity);
		ServiceFactory.getAnalyticsService().track(engagedItem);



	}
	/**
	 * If an identity has been activated, process all available content;
	 * 
	 * @param content
	 */
	private void process(ExternalIdentityActivated activated) {
		log.debug("found: {}", activated);

		// get identity from message
		ExternalIdentity identity = ServiceFactory.getExternalIdentityService()
				.getExternalIdentityById(activated.getIdentityId());
		ExternalNetwork externalNetwork = ExternalNetwork
				.getNetworkById(identity.getExternalNetwork());

		ServiceFactory.getSocialService().checkValidityOfExternalIdentity(identity);
		
		if (externalNetwork.network.equals(Network.Content))
			processVideos(identity, externalNetwork);
		else if (externalNetwork.equals(ExternalNetwork.Google))
			processVideos(identity, ExternalNetwork.YouTube);

		if(externalNetwork.equals(ExternalNetwork.Google) ) {
			processMessages(identity, externalNetwork);
		}  else if ( externalNetwork.equals(ExternalNetwork.Facebook)||externalNetwork.equals(ExternalNetwork.Twitter)) {
			processMessages(identity, externalNetwork);
			processActivities(identity, externalNetwork);
			processLocalActivities(identity, externalNetwork);
		}
		else if(externalNetwork.equals(ExternalNetwork.LinkedIn))
		{
			processActivities(identity, ExternalNetwork.LinkedIn);
		}
		
		
		
	} 

	private void processActivities(ExternalIdentity identity, ExternalNetwork socialNetwork) {
		SocialService socialService = ServiceFactory.getSocialService();
		List<Activity> synced = socialService.syncActivities(identity,
				socialNetwork);
		// index for searching
		ServiceFactory.getSearchService().indexActivities(identity.getUser().getUserId(), synced);
		
		}
	
	private void processLocalActivities(ExternalIdentity identity, ExternalNetwork socialNetwork){
		List<Activity> localActivities = ServiceFactory.getSocialService().syncLocalNewsFeed(identity, socialNetwork);
		// index for searching
		//ServiceFactory.getSearchService().indexActivities(identity.getUser().getUserId(), localActivities);
	}

	/***
	 * Process videos for this content provider
	 * 
	 * @param identity
	 * @param externalNetwork
	 */
	private void processVideos(ExternalIdentity identity, ExternalNetwork externalNetwork) {

		ContentService contentService = ServiceFactory.getContentService();
		
		List<VideoContent> synced = contentService.sync(identity, externalNetwork);

		// add videos to search results for this specific user
		ServiceFactory.getSearchService().indexVideos(identity.getUser().getUserId(), synced);
	}

	/***
	 * Process messages for this external network
	 * 
	 * @param identity
	 * @param network
	 */
	private void processMessages(ExternalIdentity identity,
			ExternalNetwork network) {
		SocialService socialService = ServiceFactory.getSocialService();

		List<com.ubiquity.social.domain.Message> messages = socialService
				.syncMessages(identity, network);

		// add messages to search results
		ServiceFactory.getSearchService().indexMessages(messages);
	}

}
