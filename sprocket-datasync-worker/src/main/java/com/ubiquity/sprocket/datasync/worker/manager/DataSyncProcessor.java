package com.ubiquity.sprocket.datasync.worker.manager;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.amqp.MessageQueueProducer;
import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.Identity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.integration.api.exception.AuthorizationException;
import com.ubiquity.integration.domain.Activity;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.Network;
import com.ubiquity.integration.domain.VideoContent;
import com.ubiquity.integration.service.ContentService;
import com.ubiquity.integration.service.SocialService;
import com.ubiquity.messaging.MessageConverter;
import com.ubiquity.messaging.format.DestinationType;
import com.ubiquity.messaging.format.Envelope;
import com.ubiquity.sprocket.messaging.MessageConverterFactory;
import com.ubiquity.sprocket.messaging.MessageQueueFactory;
import com.ubiquity.sprocket.messaging.definition.ExternalIdentityActivated;
import com.ubiquity.sprocket.messaging.definition.SynchronizationCompleted;
import com.ubiquity.sprocket.messaging.definition.SynchronizationStarted;
import com.ubiquity.sprocket.messaging.definition.SynchronizationStepCompleted;
import com.ubiquity.sprocket.service.ServiceFactory;

/***
 * Handles the processing of each feed type
 * 
 * @author mina
 *
 */
public class DataSyncProcessor extends Thread {

	private int from;
	private int to;
	private List<User> users;

	private MessageConverter messageConverter = MessageConverterFactory.getMessageConverter();


	/**
	 * Starts a processor with the underlying list 
	 * @param block
	 * @param from
	 * @param to
	 */
	public DataSyncProcessor(List<User> users, int from, int to) {
		this.from = from;
		this.to = to;
		this.users = users;
	}

	/***
	 * Creates a data sync processor that operate
	 */
	public DataSyncProcessor() {}

	private  Logger log = LoggerFactory.getLogger(getClass());


	/**
	 * If an identity has been activated, process all available content;
	 * 
	 * @param content
	 * @throws IOException 
	 */
	public void processSync(ExternalIdentityActivated activated) {
		// get identity from message
		ExternalIdentity identity = ServiceFactory.getExternalIdentityService().getExternalIdentityById(activated.getIdentityId());
		if(identity == null){
			log.error("Can't find identity in DB");
		}
		processSync(identity);
	} 



	public void run() {
		log.info("Synchronizing data from {} to {}", from, to);
		syncData();
	}

	/**
	 * Synchronizes an identity by network
	 * 
	 * @param identity
	 * @throws IOException 
	 */
	private void processSync(ExternalIdentity identity) {

		// get the back channel mq; we don't want to skip sync because we can't send an update notificaiton
		MessageQueueProducer backchannel = null;
		try { 
			backchannel = MessageQueueFactory.getBackChannelQueueProducer();
		} catch (Exception e) {
			log.warn("Unable to connect to MQ", backchannel);
		}
		
		ExternalNetwork externalNetwork = ExternalNetwork
				.getNetworkById(identity.getExternalNetwork());

		Long userId = identity.getUser().getUserId();
		
		sendSyncStartedMessageToIndividual(backchannel, externalNetwork, userId);

		
		if (externalNetwork.network.equals(Network.Content)) {
			DateTime start = new DateTime();
			int n = processVideos(identity, externalNetwork);
			log.info("Processed {} videos in {} seconds for user "+ userId, n, new Period(start, new DateTime()).getSeconds());
			
			sendStepCompletedMessageToIndividual(backchannel, externalNetwork, "Synchronized videos", getResoursePath(userId, externalNetwork, ResourceType.videos), n, userId, ResourceType.videos);
		} 
		else if (externalNetwork.equals(ExternalNetwork.Google)) {
			DateTime start = new DateTime();
			int n = processMessages(identity, externalNetwork, null);
			log.info("Processed {} messages in {} seconds for user "+ userId, n, new Period(start, new DateTime()).getSeconds());
			sendStepCompletedMessageToIndividual(backchannel, externalNetwork, "Synchronized messages", getResoursePath(userId, externalNetwork, ResourceType.messages), n, userId, ResourceType.messages);


		}  else if ( externalNetwork.equals(ExternalNetwork.Facebook) || externalNetwork.equals(ExternalNetwork.Twitter)|| externalNetwork.equals(ExternalNetwork.Tumblr)) {
			DateTime start = new DateTime();
			int n = processActivities(identity, externalNetwork); 
			log.info("Processed {} activities in {} seconds for user "+ userId, n, new Period(start, new DateTime()).getSeconds());
			sendStepCompletedMessageToIndividual(backchannel, externalNetwork, "Synchronized feed", getResoursePath(userId, externalNetwork, ResourceType.activities), n, userId, ResourceType.activities);

			if (externalNetwork.equals(ExternalNetwork.Facebook)) {
				start = new DateTime();
				n = processLocalActivities(identity, externalNetwork);
				log.info("Processed {} local activities in {} seconds for user "+ userId, n, new Period(start, new DateTime()).getSeconds());
				sendStepCompletedMessageToIndividual(backchannel, externalNetwork, "Synchronized local feed", getResoursePath(userId, externalNetwork, ResourceType.localfeed), n, userId, ResourceType.localfeed);
			}

			start = new DateTime();
			n = processMessages(identity, externalNetwork, null);
			log.info("Processed {} messages in {} seconds for user "+ userId, n, new Period(start, new DateTime()).getSeconds());
			sendStepCompletedMessageToIndividual(backchannel, externalNetwork, "Synchronized messages", getResoursePath(userId, externalNetwork, ResourceType.messages), n, userId, ResourceType.messages);
		}else if(externalNetwork.equals(ExternalNetwork.LinkedIn) || externalNetwork.equals(ExternalNetwork.Reddit)) {			
			DateTime start = new DateTime();
			int n = processActivities(identity, externalNetwork);
			log.info("Processed {} local activities in {} seconds for user "+ userId, n, new Period(start, new DateTime()).getSeconds());
			sendStepCompletedMessageToIndividual(backchannel, externalNetwork, "Synchronized feed", getResoursePath(userId, externalNetwork, ResourceType.activities), n, userId, ResourceType.activities);
		}
		
		sendSyncCompletedMessageToIndividual(backchannel, externalNetwork, userId);
	
	}

	private int processActivities(ExternalIdentity identity, ExternalNetwork socialNetwork) {
		List<Activity> synced;
		try {
			SocialService socialService = ServiceFactory.getSocialService();
			synced = socialService.syncActivities(identity, socialNetwork);

			// index for searching
			ServiceFactory.getSearchService().indexActivities(identity.getUser().getUserId(), synced, false);
			log.info("indexing activities for identity {}",identity);
			return synced.size();
		} catch (Exception e) {
			if(e instanceof AuthorizationException)
				ServiceFactory.getSocialService().setActiveNetworkForUser(identity.getUser().getUserId(), socialNetwork, false);
			
			log.error("Could not process activities for identity: {}", ExceptionUtils.getRootCauseMessage(e));
			return -1;
		}
	}

	private int processLocalActivities(ExternalIdentity identity, ExternalNetwork socialNetwork){
		List<Activity> localActivities;
		try {
			localActivities = ServiceFactory.getSocialService().syncLocalNewsFeed(identity, socialNetwork);
			return localActivities.size();
		} catch (Exception e) {
			if(e instanceof AuthorizationException)
				ServiceFactory.getSocialService().setActiveNetworkForUser(identity.getUser().getUserId(), socialNetwork, false);
			
			log.error("Unable to sync local activities for identity: {}", identity.getIdentityId(), ExceptionUtils.getRootCauseMessage(e));
			return -1;
		}
	}

	/***
	 * Process videos for this content provider
	 * 
	 * @param identity
	 * @param externalNetwork
	 */
	private int processVideos(ExternalIdentity identity,
			ExternalNetwork externalNetwork) {
		List<VideoContent> synced;
		try {
			ContentService contentService = ServiceFactory.getContentService();
			synced = contentService.sync(identity,
					externalNetwork);

			// add videos to search results for this specific user
			ServiceFactory.getSearchService().indexVideos(identity.getUser().getUserId(), synced, false);
			return synced.size();
		} catch (Exception e) {
			if(e instanceof AuthorizationException)
				ServiceFactory.getSocialService().setActiveNetworkForUser(identity.getUser().getUserId(), externalNetwork, false);
			
			log.error("Unable to sync for identity: {}", identity.getIdentityId(), ExceptionUtils.getRootCauseMessage(e));
			return -1;
		}
	}

	/***
	 * Process messages for this external network
	 * 
	 * @param identity
	 * @param network
	 */
	private int processMessages(ExternalIdentity identity,
			ExternalNetwork network,String lastMessageIdentifier) {

		List<com.ubiquity.integration.domain.Message> messages;
		try {
			SocialService socialService = ServiceFactory.getSocialService();

			messages = socialService
					.syncMessages(identity, network, lastMessageIdentifier);

			// add messages to search results
			ServiceFactory.getSearchService().indexMessages(identity.getUser().getUserId(), messages);
			log.info("indexing messages for identity {}",identity);
			return messages.size();
		} catch (Exception e) {
			if(e instanceof AuthorizationException)
				ServiceFactory.getSocialService().setActiveNetworkForUser(identity.getUser().getUserId(), network, false);
			
			log.error("Could not process messages for identity: {}", ExceptionUtils.getRootCauseMessage(e));
			e.printStackTrace();
			return -1;
		}
	}
	/***
	 * Refresh data of all users in all social networks
	 * @return
	 */
	public int syncData() {

		int numRefreshed = 0;

		try {	
			List<User> subList = users.subList(from, to);
			for(User user : subList) {
				numRefreshed += syncDataForUser(user);
			}
		} finally {
			EntityManagerSupport.closeEntityManager();
		}

		return numRefreshed;

	}
	/**
	 * Refresh data for specific user in all social networks
	 * @param user
	 * @return
	 */
	public int syncDataForUser(User user) {
		Set<Identity> identities = user.getIdentities();

		DateTime start = new DateTime();

		for (Identity identity : identities) {


			if(identity instanceof ExternalIdentity) {
				try
				{
					ExternalIdentity externalIdentity = (ExternalIdentity)identity;

					ServiceFactory.getSocialService().checkValidityOfExternalIdentity(externalIdentity);
					processSync(externalIdentity);


				} catch(Exception ex) {
					log.error(ex.getMessage());
				}
			}



		}

		log.info("Full sync for user: {} in {} seconds", user.getUserId(), new Period(start, new DateTime()).getSeconds());

		return 0;
	}


	/***
	 * Sends a step completion message to the backchannel. If the backchannel is not available, this is a no-op.
	 * 
	 * @param backchannel
	 * @param network
	 * @param message
	 * @param resourcePath
	 * @param userId
	 * 
	 */
	private void sendStepCompletedMessageToIndividual(MessageQueueProducer backchannel, ExternalNetwork network, String message, String resourcePath, Integer records, Long userId, ResourceType resourceType)  {

		if(backchannel == null)
			return;
		
		Envelope envelope = new Envelope(DestinationType.Individual, String.valueOf(userId), 
				new com.ubiquity.messaging.format.Message(new SynchronizationStepCompleted.Builder()
					.message(message)
					.resourcePath(resourcePath)
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
	 * @param network
	 * @param message
	 * @param userId
	 */
//	private void sendSyncErrorMessageToIndividual(MessageQueueProducer backchannel, ExternalNetwork network, String message, Long userId)  {
//
//		if(backchannel == null)
//			return;
//		
//		Envelope envelope = new Envelope(DestinationType.Individual, String.valueOf(userId), 
//				new com.ubiquity.messaging.format.Message(new SynchronizationError.Builder()
//					.message(message)
//					.timestamp(System.currentTimeMillis())
//					.externalNetworkId(network.ordinal()).build()));
//		try {
//			backchannel.write(messageConverter.serialize(envelope).getBytes());
//		} catch (IOException e) {
//			log.warn("Could not send update message to user {}", userId);
//		}
//
//	}

	private void sendSyncStartedMessageToIndividual(
			MessageQueueProducer backchannel, ExternalNetwork externalNetwork,
			Long userId) {
		if(backchannel == null)
			return;
		
		Envelope envelope = new Envelope(DestinationType.Individual, String.valueOf(userId), 
				new com.ubiquity.messaging.format.Message(new SynchronizationStarted(externalNetwork.ordinal(), System.currentTimeMillis())));
		try {
			backchannel.write(messageConverter.serialize(envelope).getBytes());
		} catch (IOException e) {
			log.warn("Could not send update message to user {}", userId);
		}
		
	}
	
	private void sendSyncCompletedMessageToIndividual(
			MessageQueueProducer backchannel, ExternalNetwork externalNetwork,
			Long userId) {
		if(backchannel == null)
			return;
		
		Envelope envelope = new Envelope(DestinationType.Individual, String.valueOf(userId), 
				new com.ubiquity.messaging.format.Message(new SynchronizationCompleted(externalNetwork.ordinal(), System.currentTimeMillis())));
		try {
			backchannel.write(messageConverter.serialize(envelope).getBytes());
		} catch (IOException e) {
			log.warn("Could not send update message to user {}", userId);
		}
		
	}
	
	private String getResoursePath(Long userId, ExternalNetwork externalNetwork, ResourceType resource){
		StringBuilder resourcePath = new StringBuilder();
		if(resource.equals(ResourceType.videos))
			resourcePath.append("/content/users/");
		else
			resourcePath.append("/social/users/");
		resourcePath.append(userId)
				.append("/providers/").append(externalNetwork.ordinal())
				.append("/").append(resource.name());
		return resourcePath.toString();
	}
}
