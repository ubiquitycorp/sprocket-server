package com.ubiquity.sprocket.backchannel.worker;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import com.niobium.common.thread.ThreadPool;
import com.niobium.xmpp.XMPPConnector;
import com.ubiquity.sprocket.backchannel.worker.mq.consumer.BackChannelConsumer;
import com.ubiquity.sprocket.messaging.MessageQueueFactory;


public class BackChannelWorkerEjabberdImpl extends BackChannelWorker {

	private static final int DEFAULT_NUM_CONSUMERS = 20;

	@Override
	public void initialize(Configuration configuration) throws IOException {
		
		// initialize MQ connection
		startServices(configuration);
		
		List<BackChannelConsumer> consumers = new LinkedList<BackChannelConsumer>();
		try {
			for(int i = 0; i < DEFAULT_NUM_CONSUMERS; i++)
				consumers.add(new BackChannelConsumer(MessageQueueFactory.createBackChannelConsumerChannel(), 
							new XMPPConnector(configuration, "_" + i)));
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
