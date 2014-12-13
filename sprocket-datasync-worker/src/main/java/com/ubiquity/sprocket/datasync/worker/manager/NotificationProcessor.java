package com.ubiquity.sprocket.datasync.worker.manager;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.amqp.MessageQueueProducer;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.UpdateMessage;
import com.ubiquity.messaging.MessageConverter;
import com.ubiquity.messaging.format.DestinationType;
import com.ubiquity.messaging.format.Envelope;
import com.ubiquity.sprocket.messaging.MessageConverterFactory;
import com.ubiquity.sprocket.messaging.MessageQueueFactory;
import com.ubiquity.sprocket.messaging.definition.SynchronizationStepNotification;

/***
 * Notify users by each change in data synchronization process
 * 
 * @author peter.tadros
 * 
 */
public class NotificationProcessor extends Thread {

	private MessageConverter messageConverter = MessageConverterFactory
			.getMessageConverter();

	private Logger log = LoggerFactory.getLogger(getClass());

	private boolean runnable = true;
	Map<String, UpdateMessage> processedMessages;

	/***
	 * Creates a data sync processor that operate
	 */
	public NotificationProcessor(Map<String, UpdateMessage> processedMessages) {
		this.processedMessages = processedMessages;
	}

	public void run() {
		MessageQueueProducer backchannel = null;
		// get the back channel mq; we don't want to skip sync because we can't
		// send an update notification
		try {
			while (runnable) {
				log.info(Thread.currentThread().getName()
						+ " Notification thread started");
				backchannel = MessageQueueFactory.getBackChannelQueueProducer();

				for (Map.Entry<String, UpdateMessage> entry : processedMessages.entrySet()) {
					UpdateMessage message = entry.getValue();
					log.info(entry.getKey() + "/" + message);
					if(message.isUpdated())
					{
						sendStepNotificationMessageToIndividual(backchannel, message.getNetwork(),
								message.getRecords() + "/" + message.getMaxRecords(), message.getUserId(), ResourceType.messages);
						message.setUpdated(false);
					}
				}
				sleep(2000);
			}
		} catch (Exception e) {
			log.warn("Unable to connect to MQ", backchannel);
		}
	}

	/***
	 * Sends a step notification message to the backchannel. If the backchannel
	 * is not available, this is a no-op.
	 * 
	 * @param backchannel
	 * @param network
	 * @param message
	 * @param userId
	 * @param resourceType
	 */
	public void sendStepNotificationMessageToIndividual(
			MessageQueueProducer backchannel, ExternalNetwork network,
			String message, Long userId, ResourceType resourceType) {

		if (backchannel == null)
			return;

		Envelope envelope = new Envelope(DestinationType.Individual,
				String.valueOf(userId),
				new com.ubiquity.messaging.format.Message(
						new SynchronizationStepNotification.Builder()
								.resourceType(resourceType.name())
								.timestamp(System.currentTimeMillis())
								.message(message)
								.externalNetworkId(network.ordinal()).build()));
		try {
			backchannel.write(messageConverter.serialize(envelope).getBytes());
		} catch (IOException e) {
			log.warn("Could not send notification message to user {}", userId);
		}

	}

	public void setTerminate() {
		runnable = false;
	}
}
