package com.ubiquity.sprocket.messaging;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;

import com.niobium.amqp.MessageQueueChannel;
import com.niobium.amqp.MessageQueueConnection;
import com.niobium.amqp.MessageQueueProducer;

public class MessageQueueFactory {


	private static MessageQueueConnection cacheInvalidateQueueConsumerConnection;
	private static MessageQueueConnection locationQueueConsumerConnection;
	private static MessageQueueConnection backchannelQueueConsumerConnection;

	private static MessageQueueProducer cacheInvalidateQueueProducer;
	private static MessageQueueProducer locationQueueProducer;
	private static MessageQueueProducer backchannelQueueProducer;

	
	private static Configuration configuration;

	public static void initialize(Configuration config) throws IOException {
		configuration = config;
	}

	public static MessageQueueChannel createLocationQueueConsumerChannel() throws IOException {
		return getLocationQueueConsumerConnection().createMessageQueueChannel();
	}
	
	public static MessageQueueChannel createCacheInvalidateConsumerChannel() throws IOException {
		return getCacheInvalidateQueueConsumerConnection().createMessageQueueChannel();
	}
	
	public static MessageQueueChannel createBackChannelConsumerChannel() throws IOException {
		return getBackChannelQueueConnection().createMessageQueueChannel();
	}
	
	private static MessageQueueConnection getBackChannelQueueConnection() {
		if(backchannelQueueConsumerConnection == null) {
			backchannelQueueConsumerConnection = new MessageQueueConnection.Builder()
			.queueName(configuration.getString("mq.queue.backchannel.name"))
			.host(configuration.getString("mq.queue.backchannel.host"))
			.username(configuration.getString("mq.queue.backchannel.username"))
			.password(configuration.getString("mq.queue.backchannel.password"))
			.virtualHost(configuration.getString("mq.queue.backchannel.vhost"))
			.port(configuration.getInt("mq.queue.backchannel.port"))
			.exchange(configuration.getString("mq.queue.backchannel.exchange"))
			.exchangeType(configuration.getString("mq.queue.backchannel.exchangeType"))
			.routeKey(configuration.getString("mq.queue.backchannel.routeKey"))
			.heartBeat(configuration.getInt("mq.queue.backchannel.heartbeat"))
			.autoAck(configuration.getBoolean("mq.queue.backchannel.autoAck")).build();
		}
		return backchannelQueueConsumerConnection;
	}
	
	private static MessageQueueConnection getLocationQueueConsumerConnection() {
		if(locationQueueConsumerConnection == null) {
			locationQueueConsumerConnection = new MessageQueueConnection.Builder()
			.queueName(configuration.getString("mq.queue.location.name"))
			.host(configuration.getString("mq.queue.location.host"))
			.username(configuration.getString("mq.queue.location.username"))
			.password(configuration.getString("mq.queue.location.password"))
			.virtualHost(configuration.getString("mq.queue.location.vhost"))
			.port(configuration.getInt("mq.queue.location.port"))
			.exchange(configuration.getString("mq.queue.location.exchange"))
			.exchangeType(configuration.getString("mq.queue.location.exchangeType"))
			.routeKey(configuration.getString("mq.queue.location.routeKey"))
			.heartBeat(configuration.getInt("mq.queue.location.heartbeat"))
			.autoAck(configuration.getBoolean("mq.queue.location.autoAck")).build();
		}
		return locationQueueConsumerConnection;
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
	
	public static MessageQueueProducer getBackChannelQueueProducer() throws IOException {
		if(backchannelQueueProducer == null) {
			MessageQueueConnection connection = new MessageQueueConnection.Builder()
			.queueName(configuration.getString("mq.queue.backchannel.name"))
			.host(configuration.getString("mq.queue.backchannel.host"))
			.username(configuration.getString("mq.queue.backchannel.username"))
			.password(configuration.getString("mq.queue.backchannel.password"))
			.virtualHost(configuration.getString("mq.queue.backchannel.vhost"))
			.port(configuration.getInt("mq.queue.backchannel.port"))
			.exchange(configuration.getString("mq.queue.backchannel.exchange"))
			.exchangeType(configuration.getString("mq.queue.backchannel.exchangeType"))
			.routeKey(configuration.getString("mq.queue.backchannel.routeKey"))
			.heartBeat(configuration.getInt("mq.queue.backchannel.heartbeat"))
			.autoAck(configuration.getBoolean("mq.queue.backchannel.autoAck")).build();
			backchannelQueueProducer = new MessageQueueProducer(connection.createMessageQueueChannel());
		}
		return backchannelQueueProducer;
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
	
	public static MessageQueueProducer getLocationQueueProducer() throws IOException {
		if(locationQueueProducer == null) {
			MessageQueueConnection connection = new MessageQueueConnection.Builder()
			.queueName(configuration.getString("mq.queue.location.name"))
			.host(configuration.getString("mq.queue.location.host"))
			.username(configuration.getString("mq.queue.location.username"))
			.password(configuration.getString("mq.queue.location.password"))
			.virtualHost(configuration.getString("mq.queue.location.vhost"))
			.port(configuration.getInt("mq.queue.location.port"))
			.exchange(configuration.getString("mq.queue.location.exchange"))
			.exchangeType(configuration.getString("mq.queue.location.exchangeType"))
			.routeKey(configuration.getString("mq.queue.location.routeKey"))
			.heartBeat(configuration.getInt("mq.queue.location.heartbeat"))
			.autoAck(configuration.getBoolean("mq.queue.location.autoAck")).build();
			locationQueueProducer = new MessageQueueProducer(connection.createMessageQueueChannel());
		}
		return locationQueueProducer;
	}
	
	
}
