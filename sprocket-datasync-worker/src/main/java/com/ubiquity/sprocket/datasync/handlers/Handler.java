package com.ubiquity.sprocket.datasync.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.amqp.MessageQueueProducer;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.ExternalNetworkApplication;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.SyncStatusMessage;
import com.ubiquity.sprocket.datasync.worker.manager.SyncProcessor;
import com.ubiquity.sprocket.messaging.MessageQueueFactory;
import com.ubiquity.sprocket.service.ServiceFactory;

/***
 * 
 * @author peter.tadros
 * 
 */
public abstract class Handler {
	protected Set<ExternalNetwork> networks;

	protected Handler next;

	protected Logger log = LoggerFactory.getLogger(getClass());

	protected SyncProcessor processor;
	protected MessageQueueProducer backchannel = null;
	protected Map<String, SyncStatusMessage> processedMessages = null;

	public Handler(SyncProcessor processor) {
		this.processor = processor;
		processedMessages = new HashMap<String, SyncStatusMessage>();
		// get the back channel mq; we don't want to skip sync because we can't
		// send an update notification
		try {
			backchannel = MessageQueueFactory.getBackChannelQueueProducer();
		} catch (Exception e) {
			log.warn("Unable to connect to MQ", backchannel);
		}
	}

	public void canAccept(ExternalIdentity identity, ExternalNetwork network,
			ExternalNetworkApplication externalNetworkApplication) {
		if (networks.contains(network))
			syncData(identity, network, externalNetworkApplication);

		if (next != null)
			next.canAccept(identity, network, externalNetworkApplication);
	}

	public void setNext(Handler next) {
		this.next = next;
	}

	public Handler getNext() {
		return next;
	}

	public Map<String, SyncStatusMessage> getProcessedMessages() {
		return processedMessages;
	}

	protected Boolean makeDecision(String key, ExternalNetwork network) {
		return ServiceFactory.getClientConfigurationService().getValue(key,
				network);
	}
	
	abstract protected void syncData(ExternalIdentity identity,
			ExternalNetwork network,
			ExternalNetworkApplication externalNetworkApplication);

	
}
