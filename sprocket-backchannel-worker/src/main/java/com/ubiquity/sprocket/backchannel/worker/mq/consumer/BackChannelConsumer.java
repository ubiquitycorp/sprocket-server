package com.ubiquity.sprocket.backchannel.worker.mq.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.amqp.AbstractConsumerThread;
import com.niobium.amqp.ConsumerStrategy;
import com.niobium.amqp.MessageQueueChannel;
import com.niobium.xmpp.XMPPConnector;
import com.ubiquity.messaging.MessageConverter;
import com.ubiquity.messaging.format.DestinationType;
import com.ubiquity.messaging.format.Envelope;
import com.ubiquity.sprocket.messaging.MessageConverterFactory;

public class BackChannelConsumer extends AbstractConsumerThread {

	private Logger log = LoggerFactory.getLogger(getClass());
		
	private XMPPConnector xmppConnector;

	protected MessageConverter messageConverter = MessageConverterFactory.getMessageConverter();
	
	/***
	 * Establishes connection to location update queue
	 */
	public BackChannelConsumer(MessageQueueChannel queueChannel, XMPPConnector xmppConnector) {
		super(queueChannel, ConsumerStrategy.Bulk);
		this.xmppConnector = xmppConnector;
	}

	@Override
	public void processMessage(byte[] msg) {
		log.info("Processing message {}", new String(msg));

		Envelope envelope = messageConverter.deserialize(msg, Envelope.class);
		if(envelope.getDestination() == DestinationType.Individual) {
			log.debug("Sending message for type {}", DestinationType.Individual);
			try {
				xmppConnector.sendMessage(envelope.getIdentifier(), messageConverter.serialize(envelope.getBody()));
			} catch (Exception e) {
				log.error("Could not send message via message service", e);
				System.exit(0);
			}
		}
	}

	@Override
	protected void handleException(Throwable e) {
		// TODO Auto-generated method stub
		log.info("Exeception : {}", e.getMessage());
		
	}

}
