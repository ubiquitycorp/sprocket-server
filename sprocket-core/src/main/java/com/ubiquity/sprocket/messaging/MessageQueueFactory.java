package com.ubiquity.sprocket.messaging;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;

import com.niobium.amqp.MessageQueueChannel;
import com.niobium.amqp.MessageQueueConnection;
import com.niobium.amqp.MessageQueueProducer;

public class MessageQueueFactory {


	private static MessageQueueConnection cacheInvalidateQueueConsumerConnection;
	private static MessageQueueConnection locationQueueConsumerConnection;
	private static MessageQueueConnection backChannelQueueConnection;

	private static MessageQueueProducer cacheInvalidateQueueProducer;
	private static MessageQueueProducer locationQueueProducer;
	private static MessageQueueProducer backChannelQueueProducer;

	private static Configuration configuration;

	public static void initialize(Configuration config) throws IOException {
		configuration = config;
	}

	/***
	 * Override default routing key
	 * @param routeKey
	 * @return
	 * @throws IOException
	 */
	public static MessageQueueProducer getBackChannelQueueProducer() throws IOException {
		if(backChannelQueueProducer == null) {
			MessageQueueConnection connection = new MessageQueueConnection.Builder()
			.host(configuration.getString("mq.host"))
			.username(configuration.getString("mq.username"))
			.password(configuration.getString("mq.password"))
			.port(configuration.getInt("mq.port"))
			.queueName(configuration.getString("mq.queue.backchannel.name"))
			.virtualHost(configuration.getString("mq.queue.backchannel.vhost"))
			.exchange(configuration.getString("mq.queue.backchannel.exchange"))
			.exchangeType(configuration.getString("mq.queue.backchannel.exchangeType"))
			.routeKey(configuration.getString("mq.queue.backchannel.routeKey"))
			.heartBeat(configuration.getInt("mq.queue.backchannel.heartbeat"))
			.autoAck(configuration.getBoolean("mq.queue.backchannel.autoAck")).build();
			backChannelQueueProducer = new MessageQueueProducer(connection.createProducerChannel());
		}
		return backChannelQueueProducer;
	}

	public static MessageQueueChannel createLocationQueueConsumerChannel() throws IOException {
		return getLocationQueueConsumerConnection().createConsumerChannel();
	}

	public static MessageQueueChannel createCacheInvalidateConsumerChannel() throws IOException {
		return getCacheInvalidateQueueConsumerConnection().createConsumerChannel();
	}

	public static MessageQueueChannel createBackChannelConsumerChannel() throws IOException {
		return getBackChannelQueueConnection().createConsumerChannel();
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
			cacheInvalidateQueueProducer = new MessageQueueProducer(connection.createProducerChannel());
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
			locationQueueProducer = new MessageQueueProducer(connection.createProducerChannel());
		}
		return locationQueueProducer;
	}
	
	
	private static MessageQueueConnection getBackChannelQueueConnection() {
		if(backChannelQueueConnection == null) {
			backChannelQueueConnection = new MessageQueueConnection.Builder()
			.host(configuration.getString("mq.host"))
			.port(configuration.getInt("mq.port"))
			.username(configuration.getString("mq.username"))
			.password(configuration.getString("mq.password"))
			.queueName(configuration.getString("mq.queue.backchannel.name"))
			.virtualHost(configuration.getString("mq.queue.backchannel.vhost"))
			.exchange(configuration.getString("mq.queue.backchannel.exchange"))
			.exchangeType(configuration.getString("mq.queue.backchannel.exchangeType"))
			.routeKey(configuration.getString("mq.queue.backchannel.routeKey"))
			.heartBeat(configuration.getInt("mq.queue.backchannel.heartbeat"))
			.autoAck(configuration.getBoolean("mq.queue.backchannel.autoAck")).build();
		}
		return backChannelQueueConnection;
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


}
