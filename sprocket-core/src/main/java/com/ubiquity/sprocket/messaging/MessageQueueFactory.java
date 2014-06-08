package com.ubiquity.sprocket.messaging;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;

import com.niobium.amqp.MessageQueueChannel;
import com.niobium.amqp.MessageQueueConnection;
import com.niobium.amqp.MessageQueueProducer;

public class MessageQueueFactory {


	private static MessageQueueConnection cacheInvalidateQueueConsumerConnection;
	private static MessageQueueConnection trackQueueConsumerConnection;
	
	private static MessageQueueProducer cacheInvalidateQueueProducer;
	private static MessageQueueProducer trackQueueProducer;
	
	private static Configuration configuration;

	public static void initialize(Configuration config) throws IOException {
		configuration = config;
	}

	public static MessageQueueChannel createTrackQueueConsumerChannel() throws IOException {
		return getTrackQueueConsumerConnection().createMessageQueueChannel();
	}
	
	public static MessageQueueChannel createCacheInvalidateConsumerChannel() throws IOException {
		return getCacheInvalidateQueueConsumerConnection().createMessageQueueChannel();
	}
	
	private static MessageQueueConnection getTrackQueueConsumerConnection() {
		if(trackQueueConsumerConnection == null) {
			trackQueueConsumerConnection = new MessageQueueConnection.Builder()
			.queueName(configuration.getString("mq.queue.track.name"))
			.host(configuration.getString("mq.queue.track.host"))
			.username(configuration.getString("mq.queue.track.username"))
			.password(configuration.getString("mq.queue.track.password"))
			.virtualHost(configuration.getString("mq.queue.track.vhost"))
			.port(configuration.getInt("mq.queue.track.port"))
			.exchange(configuration.getString("mq.queue.track.exchange"))
			.exchangeType(configuration.getString("mq.queue.track.exchangeType"))
			.routeKey(configuration.getString("mq.queue.track.routeKey"))
			.heartBeat(configuration.getInt("mq.queue.track.heartbeat"))
			.autoAck(configuration.getBoolean("mq.queue.track.autoAck")).build();
		}
		return trackQueueConsumerConnection;
	}
	
	private static MessageQueueConnection getCacheInvalidateQueueConsumerConnection() {
		
		if(cacheInvalidateQueueConsumerConnection == null) {
			cacheInvalidateQueueConsumerConnection = new MessageQueueConnection.Builder()
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
		}
		return cacheInvalidateQueueConsumerConnection;
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
	
	public static MessageQueueProducer getTrackQueueProducer() throws IOException {
		if(trackQueueProducer == null) {
			MessageQueueConnection connection = new MessageQueueConnection.Builder()
			.queueName(configuration.getString("mq.queue.track.name"))
			.host(configuration.getString("mq.queue.track.host"))
			.username(configuration.getString("mq.queue.track.username"))
			.password(configuration.getString("mq.queue.track.password"))
			.virtualHost(configuration.getString("mq.queue.track.vhost"))
			.port(configuration.getInt("mq.queue.track.port"))
			.exchange(configuration.getString("mq.queue.track.exchange"))
			.exchangeType(configuration.getString("mq.queue.track.exchangeType"))
			.routeKey(configuration.getString("mq.queue.track.routeKey"))
			.heartBeat(configuration.getInt("mq.queue.track.heartbeat"))
			.autoAck(configuration.getBoolean("mq.queue.track.autoAck")).build();
			trackQueueProducer = new MessageQueueProducer(connection.createMessageQueueChannel());
		}
		return trackQueueProducer;
	}
	
	
}
