package com.ubiquity.sprocket.messaging;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;

import com.niobium.amqp.MessageQueueConnection;
import com.niobium.amqp.MessageQueueProducer;

public class MessageQueueProducerFactory {

	private static MessageQueueProducer cacheInvalidateQueueProducer;

	private static Configuration configuration;

	public static void initialize(Configuration config) throws IOException {
		configuration = config;
		getCacheInvalidationQueueProducer();
	}

	public static MessageQueueProducer getCacheInvalidationQueueProducer() throws IOException {
		if(cacheInvalidateQueueProducer == null) {
			MessageQueueConnection connection = new MessageQueueConnection.Builder()
			.queueName(configuration.getString("mq.queue.cacheinvalidate.name"))
			.host(configuration.getString("mq.queue.cacheinvalidate.host"))
			.username(configuration.getString("mq.queue.cacheinvalidate.username"))
			.password(configuration.getString("mq.queue.cacheinvalidate.password"))
			.virtualHost(configuration.getString("mq.queue.cacheinvalidate.vhost"))
			.port(configuration.getInt("mq.queue.cacheinvalidate.port"))
			.exchange(configuration.getString("mq.queue.cacheinvalidate.exchange"))
			.exchangeType(configuration.getString("mq.queue.cacheinvalidate.exchangeType"))
			.routeKey(configuration.getString("mq.queue.cacheinvalidate.routeKey"))
			.heartBeat(configuration.getInt("mq.queue.cacheinvalidate.heartbeat"))
			.autoAck(configuration.getBoolean("mq.queue.cacheinvalidate.autoAck")).build();
			cacheInvalidateQueueProducer = new MessageQueueProducer(connection.createMessageQueueChannel());
		}
		return cacheInvalidateQueueProducer;
	}
	
	
}
