package com.ubiquity.sprocket.tracker.worker.mq.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.amqp.AbstractConsumerThread;
import com.niobium.amqp.MessageQueueChannel;
import com.ubiquity.messaging.MessageConverter;
import com.ubiquity.messaging.format.Message;
import com.ubiquity.sprocket.messaging.MessageConverterFactory;
import com.ubiquity.sprocket.messaging.definition.UserEngagedActivity;

/***
 * Consumer will consume messages from the event tracking queue and store them into the event data store
 * 
 * @author chris
 *
 */
public class TrackConsumer extends AbstractConsumerThread {

	private Logger log = LoggerFactory.getLogger(getClass());

	private MessageConverter messageConverter;

	public TrackConsumer(MessageQueueChannel queueChannel) {
		super(queueChannel);
		this.messageConverter = MessageConverterFactory.getMessageConverter();
	}

	@Override
	public void processMessage(byte[] msg) {
		// Currently just automatically fan these out to all parties

		try {
			Message message = messageConverter.deserialize(msg, Message.class);
			log.debug("message received: {}", message);
			if(message.getType().equals(UserEngagedActivity.class.getSimpleName()))
				process((UserEngagedActivity)message.getContent());
			
		} catch (Exception e) {
			// For now, log an error and exit until we know all the circumstances under which this can happen
			log.error("Could not process message: {}", e);
			System.exit(0);
		}
	}

	private void process(UserEngagedActivity messageContent) {		
		
		
	} 
	
}
