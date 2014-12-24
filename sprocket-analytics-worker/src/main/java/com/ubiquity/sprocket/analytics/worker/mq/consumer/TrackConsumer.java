package com.ubiquity.sprocket.analytics.worker.mq.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.amqp.AbstractConsumerThread;
import com.niobium.amqp.ConsumerStrategy;
import com.niobium.amqp.MessageQueueChannel;
import com.ubiquity.messaging.MessageConverter;
import com.ubiquity.messaging.format.Message;
import com.ubiquity.sprocket.domain.Content;
import com.ubiquity.sprocket.domain.factory.ContentFactory;
import com.ubiquity.sprocket.messaging.MessageConverterFactory;
import com.ubiquity.sprocket.messaging.definition.UserEngagedActivity;
import com.ubiquity.sprocket.messaging.definition.UserEngagedDocument;
import com.ubiquity.sprocket.messaging.definition.UserEngagedVideo;
import com.ubiquity.sprocket.service.AnalyticsService;
import com.ubiquity.sprocket.service.ServiceFactory;

public class TrackConsumer extends AbstractConsumerThread {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	private MessageConverter messageConverter = MessageConverterFactory.getMessageConverter();
	
	private AnalyticsService analyticsService = ServiceFactory.getAnalyticsService();
	
	public TrackConsumer(MessageQueueChannel queueChannel) {
		super(queueChannel, ConsumerStrategy.Bulk);
	}

	@Override
	public void processMessage(byte[] msg) {
		log.info("processMessage {}", new String(msg));
		
		Message message = messageConverter.deserialize(msg, Message.class);
		if(message.getType().equals(UserEngagedVideo.class.getSimpleName())) {
			process((UserEngagedVideo)message.getContent());
		} else if(message.getType().equals(UserEngagedActivity.class.getSimpleName())) {
			process((UserEngagedActivity)message.getContent());	
	    } else if(message.getType().equals(UserEngagedDocument.class.getSimpleName())) {
	    	process((UserEngagedDocument)message.getContent());	
	    }
	}
	
	private void process(UserEngagedDocument engagedDocument) {}

	private void process(UserEngagedActivity engagedActivity) {
		Content content = ContentFactory.createContent(engagedActivity.getActivity());
		analyticsService.track(content, engagedActivity.getUserId(), System.currentTimeMillis(), null);
	}

	private void process(UserEngagedVideo engagedVideo) {
		Content content = ContentFactory.createContent(engagedVideo.getVideoContent());
		analyticsService.track(content, engagedVideo.getUserId(), System.currentTimeMillis(), null);
	}

}
