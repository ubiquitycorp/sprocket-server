package com.ubiquity.sprocket.datasync.worker.manager;

import java.io.IOException;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.amqp.MessageQueueProducer;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.Identity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.service.SocialService;
import com.ubiquity.messaging.MessageConverter;
import com.ubiquity.messaging.format.DestinationType;
import com.ubiquity.messaging.format.Envelope;
import com.ubiquity.sprocket.datasync.handlers.Handler;
import com.ubiquity.sprocket.messaging.MessageConverterFactory;
import com.ubiquity.sprocket.messaging.MessageQueueFactory;
import com.ubiquity.sprocket.messaging.definition.ExternalIdentityActivated;
import com.ubiquity.sprocket.messaging.definition.SynchronizationCompleted;
import com.ubiquity.sprocket.messaging.definition.SynchronizationStarted;
import com.ubiquity.sprocket.messaging.definition.SynchronizationStepCompleted;
import com.ubiquity.sprocket.service.ServiceFactory;

/***
 * 
 * @author peter.tadros
 * 
 */
public abstract class SyncProcessor {

	Handler mainHandler;

	protected MessageConverter messageConverter = MessageConverterFactory
			.getMessageConverter();
	protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * If an identity has been activated, process all available content;
	 * 
	 * @param content
	 * @throws IOException
	 */
	public void processSync(ExternalIdentityActivated activated) {
		// get identity from message
		ExternalIdentity identity = ServiceFactory.getExternalIdentityService()
				.getExternalIdentityById(activated.getIdentityId());
		processSync(identity);
	}

	/**
	 * Synchronizes an identity by network
	 * 
	 * @param identity
	 * @throws IOException
	 */
	public void processSync(ExternalIdentity identity) {

		MessageQueueProducer backchannel = null;
		// get the back channel mq; we don't want to skip sync because we can't
		// send an update notificaiton
		try {
			backchannel = MessageQueueFactory.getBackChannelQueueProducer();
		} catch (Exception e) {
			log.warn("Unable to connect to MQ", backchannel);
		}
		ExternalNetwork externalNetwork = ExternalNetwork
				.getNetworkById(identity.getExternalNetwork());

		Long userId = identity.getUser().getUserId();

		sendSyncStartedMessageToIndividual(backchannel, externalNetwork, userId);

		mainHandler.canAccept(identity, externalNetwork);

		sendSyncCompletedMessageToIndividual(backchannel, externalNetwork,
				userId);

	}

	public abstract int syncData();

	/**
	 * Refresh data for specific user in all social networks
	 * 
	 * @param user
	 * @return
	 */
	public int syncDataForUser(User user) {
		Set<Identity> identities = user.getIdentities();

		DateTime start = new DateTime();

		for (Identity identity : identities) {

			if (identity instanceof ExternalIdentity) {
				try {
					ExternalIdentity externalIdentity = (ExternalIdentity) identity;

					SocialService socialService = ServiceFactory
							.getSocialService();
					socialService
							.checkValidityOfExternalIdentity(externalIdentity);

					if (externalIdentity.getIsActive())
						processSync(externalIdentity);

				} catch (Exception ex) {
					log.error(ex.getMessage());
				}
			}
		}

		log.info("Full sync for user: {} in {} seconds", user.getUserId(),
				new Period(start, new DateTime()).getSeconds());

		return 0;
	}

	/***
	 * Sends a step completion message to the backchannel. If the backchannel is
	 * not available, this is a no-op.
	 * 
	 * @param backchannel
	 * @param network
	 * @param message
	 * @param resourcePath
	 * @param userId
	 * 
	 */
	public void sendStepCompletedMessageToIndividual(
			MessageQueueProducer backchannel, ExternalNetwork network,
			String message, String resourcePath, Integer records, Long userId,
			ResourceType resourceType) {

		if (backchannel == null)
			return;

		Envelope envelope = new Envelope(DestinationType.Individual,
				String.valueOf(userId),
				new com.ubiquity.messaging.format.Message(
						new SynchronizationStepCompleted.Builder()
								.message(message).resourcePath(resourcePath)
								.resourceType(resourceType.name())
								.records(records)
								.timestamp(System.currentTimeMillis())
								.externalNetworkId(network.ordinal()).build()));
		try {
			backchannel.write(messageConverter.serialize(envelope).getBytes());
		} catch (IOException e) {
			log.warn("Could not send update message to user {}", userId);
		}

	}

	/***
	 * 
	 * @param backchannel
	 * @param networks
	 * @param message
	 * @param userId
	 */
	// private void sendSyncErrorMessageToIndividual(MessageQueueProducer
	// backchannel, ExternalNetwork network, String message, Long userId) {
	//
	// if(backchannel == null)
	// return;
	//
	// Envelope envelope = new Envelope(DestinationType.Individual,
	// String.valueOf(userId),
	// new com.ubiquity.messaging.format.Message(new
	// SynchronizationError.Builder()
	// .message(message)
	// .timestamp(System.currentTimeMillis())
	// .externalNetworkId(network.ordinal()).build()));
	// try {
	// backchannel.write(messageConverter.serialize(envelope).getBytes());
	// } catch (IOException e) {
	// log.warn("Could not send update message to user {}", userId);
	// }
	//
	// }

	public void sendSyncStartedMessageToIndividual(
			MessageQueueProducer backchannel, ExternalNetwork externalNetwork,
			Long userId) {
		if (backchannel == null)
			return;

		Envelope envelope = new Envelope(DestinationType.Individual,
				String.valueOf(userId),
				new com.ubiquity.messaging.format.Message(
						new SynchronizationStarted(externalNetwork.ordinal(),
								System.currentTimeMillis())));
		try {
			backchannel.write(messageConverter.serialize(envelope).getBytes());
		} catch (IOException e) {
			log.warn("Could not send update message to user {}", userId);
		}

	}

	public void sendSyncCompletedMessageToIndividual(
			MessageQueueProducer backchannel, ExternalNetwork externalNetwork,
			Long userId) {
		if (backchannel == null)
			return;

		Envelope envelope = new Envelope(DestinationType.Individual,
				String.valueOf(userId),
				new com.ubiquity.messaging.format.Message(
						new SynchronizationCompleted(externalNetwork.ordinal(),
								System.currentTimeMillis())));
		try {
			backchannel.write(messageConverter.serialize(envelope).getBytes());
		} catch (IOException e) {
			log.warn("Could not send update message to user {}", userId);
		}
	}

	public String getResoursePath(Long userId, ExternalNetwork externalNetwork,
			ResourceType resource) {
		StringBuilder resourcePath = new StringBuilder();
		if (resource.equals(ResourceType.videos))
			resourcePath.append("/content/users/");
		else
			resourcePath.append("/social/users/");
		if (!resource.equals(ResourceType.contacts))
			resourcePath.append(userId).append("/providers/")
					.append(externalNetwork.ordinal()).append("/");
		
		resourcePath.append(resource.getEndpointName());
		return resourcePath.toString();
	}
}
