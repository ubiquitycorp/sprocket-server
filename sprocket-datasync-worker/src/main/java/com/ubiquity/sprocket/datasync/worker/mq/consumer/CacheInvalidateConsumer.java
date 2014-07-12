package com.ubiquity.sprocket.datasync.worker.mq.consumer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.amqp.AbstractConsumerThread;
import com.niobium.amqp.MessageQueueChannel;
import com.ubiquity.content.domain.VideoContent;
import com.ubiquity.content.service.ContentService;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.messaging.MessageConverter;
import com.ubiquity.messaging.format.Message;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.external.domain.Network;
import com.ubiquity.social.service.SocialService;
import com.ubiquity.sprocket.messaging.MessageConverterFactory;
import com.ubiquity.sprocket.messaging.definition.ExternalIdentityActivated;
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
			log.debug("message received: {}", message);
			if (message.getType().equals(
					ExternalIdentityActivated.class.getSimpleName()))
				process((ExternalIdentityActivated) message.getContent());

		} catch (Exception e) {
			// For now, log an error and exit until we know all the
			// circumstances under which this can happen
			log.error("Could not process message: {}", e);
			// System.exit(0);
		}
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

		if (externalNetwork.network.equals(Network.Content))
			processVideos(identity, externalNetwork);
		else if (externalNetwork.equals(ExternalNetwork.Google))
			processVideos(identity, ExternalNetwork.YouTube);
		
		if(externalNetwork.equals(ExternalNetwork.Google)||externalNetwork.equals(ExternalNetwork.Twitter)) {
			processMessages(identity, externalNetwork);
		}  else if (externalNetwork.equals(ExternalNetwork.Facebook)) {
			processMessages(identity, ExternalNetwork.Facebook);
			processActivities(identity, ExternalNetwork.Facebook);
		}
	} 

	private void processActivities(ExternalIdentity identity,
			ExternalNetwork socialNetwork) {
		SocialService socialService = ServiceFactory.getSocialService();
		List<Activity> synced = socialService.syncActivities(identity,
				socialNetwork);

		// index for searching
		ServiceFactory.getSearchService().indexActivities(synced);
	}

	/***
	 * Process videos for this content provider
	 * 
	 * @param identity
	 * @param externalNetwork
	 */
	private void processVideos(ExternalIdentity identity,
			ExternalNetwork externalNetwork) {

		ContentService contentService = ServiceFactory.getContentService();
		List<VideoContent> synced = contentService.sync(identity,
				externalNetwork);

		// add videos to search results
		ServiceFactory.getSearchService().indexVideos(synced,
				identity.getUser().getUserId());
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
