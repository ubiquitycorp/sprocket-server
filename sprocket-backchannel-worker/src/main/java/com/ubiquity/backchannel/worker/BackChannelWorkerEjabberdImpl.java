package com.ubiquity.backchannel.worker;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import com.niobium.common.thread.ThreadPool;
import com.niobium.xmpp.XMPPConnector;
import com.ubiquity.backchannel.worker.mq.consumer.BackChannelConsumer;
import com.ubiquity.sprocket.messaging.MessageQueueFactory;


public class BackChannelWorkerEjabberdImpl extends BackChannelWorker {

	private XMPPConnector xmppConnector;

	@Override
	public void initialize(Configuration configuration) throws IOException {
		xmppConnector = new XMPPConnector(configuration);
		
		// initialize MQ connection
		startServices(configuration);
		
		List<BackChannelConsumer> consumers = new LinkedList<BackChannelConsumer>();
		try {
			consumers.add(new BackChannelConsumer(MessageQueueFactory.createBackChannelConsumerChannel(), xmppConnector));
		} catch (IOException e) {
			log.error("Unable to start service", e);
			System.exit(0);
		}
		
		ThreadPool<BackChannelConsumer> threadPool = new ThreadPool<BackChannelConsumer>();
		threadPool.start(consumers);

	}
	
	
	private void startServices(Configuration configuration) throws IOException {
		MessageQueueFactory.initialize(configuration);
	}


	@Override
	public void destroy() {}



}
