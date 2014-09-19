package com.ubiquity.sprocket.datasync.worker.manager;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.content.domain.VideoContent;
import com.ubiquity.content.service.ContentService;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.external.domain.Network;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.Identity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.service.SocialService;
import com.ubiquity.sprocket.messaging.definition.ExternalIdentityActivated;
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
	
	public DataSyncProcessor() {}
	
	private  Logger log = LoggerFactory.getLogger(getClass());
	/**
	 * If an identity has been activated, process all available content;
	 * 
	 * @param content
	 */
	public void processSync(ExternalIdentityActivated activated) {
		log.debug("found: {}", activated);
		// get identity from message
		ExternalIdentity identity = ServiceFactory.getExternalIdentityService()
				.getExternalIdentityById(activated.getIdentityId());
		processSync(identity);
	} 
	
	
	
	public void run() {
		log.info("Synchronizing data from {} to {}", from, to);
		syncData();
	}
	
	private void processSync(ExternalIdentity identity )
	{

		ExternalNetwork externalNetwork = ExternalNetwork
				.getNetworkById(identity.getExternalNetwork());

		if (externalNetwork.network.equals(Network.Content)) {
			DateTime start = new DateTime();
			int n = processVideos(identity, externalNetwork);
			
			log.info("Processed {} videos in {} seconds", n, new Period(start, new DateTime()).getSeconds());

		} else if (externalNetwork.equals(ExternalNetwork.Google)) {
			DateTime start = new DateTime();
			int n = processVideos(identity, ExternalNetwork.YouTube);
			log.info("Processed {} videos in {} seconds", n, new Period(start, new DateTime()).getSeconds());
		} else if(externalNetwork.equals(ExternalNetwork.Google) ) {
			DateTime start = new DateTime();
			int n = processMessages(identity, externalNetwork, null);
			log.info("Processed {} messages in {} seconds", n, new Period(start, new DateTime()).getSeconds());
		}  else if ( externalNetwork.equals(ExternalNetwork.Facebook) || externalNetwork.equals(ExternalNetwork.Twitter)) {
			DateTime start = new DateTime();
			int n = processActivities(identity, externalNetwork); 
			log.info("Processed {} activities in {} seconds", n, new Period(start, new DateTime()).getSeconds());
			if (externalNetwork.equals(ExternalNetwork.Facebook)) {
				start = new DateTime();
				n = processLocalActivities(identity, externalNetwork);
				log.info("Processed {} local activities in {} seconds", n, new Period(start, new DateTime()).getSeconds());
			}
			start = new DateTime();
			n = processMessages(identity, externalNetwork, null);
			log.info("Processed {} messages in {} seconds", n, new Period(start, new DateTime()).getSeconds());
		}
		else if(externalNetwork.equals(ExternalNetwork.LinkedIn))
		{
			DateTime start = new DateTime();
			int n = processActivities(identity, ExternalNetwork.LinkedIn);
			log.info("Processed {} local activities in {} seconds", n, new Period(start, new DateTime()).getSeconds());
		}
	
	}

	private int processActivities(ExternalIdentity identity,
			ExternalNetwork socialNetwork) {
		log.info("processing identity {}", identity.getIdentityId());
		List<Activity> synced;
		try {
			SocialService socialService = ServiceFactory.getSocialService();
			synced = socialService.syncActivities(identity,
					socialNetwork);

			// index for searching
			ServiceFactory.getSearchService().indexActivities(identity.getUser().getUserId(), synced);
			return synced.size();
		} catch (Exception e) {
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
			ServiceFactory.getSearchService().indexVideos(identity.getUser().getUserId(), synced);
			return synced.size();
		} catch (Exception e) {
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

		List<com.ubiquity.social.domain.Message> messages;
		try {
			SocialService socialService = ServiceFactory.getSocialService();

			messages = socialService
					.syncMessages(identity, network, lastMessageIdentifier);

			// add messages to search results
			ServiceFactory.getSearchService().indexMessages(messages);
			return messages.size();
		} catch (Exception e) {
			log.error("Could not process messages for identity: {}", ExceptionUtils.getRootCauseMessage(e));
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
}
