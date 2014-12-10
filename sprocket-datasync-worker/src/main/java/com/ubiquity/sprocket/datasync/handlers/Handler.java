package com.ubiquity.sprocket.datasync.handlers;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.amqp.MessageQueueProducer;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.sprocket.datasync.worker.manager.DataSyncProcessor;
import com.ubiquity.sprocket.messaging.MessageQueueFactory;

/***
 * 
 * @author peter.tadros
 * 
 */
public abstract class Handler {
	protected Set<ExternalNetwork> networks;

	protected Handler next;

	protected Logger log = LoggerFactory.getLogger(getClass());

	protected DataSyncProcessor processor;
	protected MessageQueueProducer backchannel = null;

	public Handler(DataSyncProcessor processor, Set<ExternalNetwork> network) {
		this.networks = network;
		this.processor = processor;

		// get the back channel mq; we don't want to skip sync because we can't
		// send an update notificaiton
		try {
			backchannel = MessageQueueFactory.getBackChannelQueueProducer();
		} catch (Exception e) {
			log.warn("Unable to connect to MQ", backchannel);
		}
	}

	public void canAccept(ExternalIdentity identity,
			ExternalNetwork network) {
		if (networks.contains(network))
			syncData(identity, network);

		if (next != null)
			next.canAccept(identity, network);
	}

	public void setNext(Handler next) {
		this.next = next;
	}

	abstract protected void syncData(ExternalIdentity identity,
			ExternalNetwork network);
}
