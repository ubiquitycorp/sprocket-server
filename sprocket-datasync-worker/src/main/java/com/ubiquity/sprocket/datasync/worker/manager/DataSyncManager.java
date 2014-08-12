package com.ubiquity.sprocket.datasync.worker.manager;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.content.domain.VideoContent;
import com.ubiquity.content.service.ContentService;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.external.domain.Network;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.service.SocialService;
import com.ubiquity.sprocket.messaging.definition.ExternalIdentityActivated;
import com.ubiquity.sprocket.service.ServiceFactory;

public class DataSyncManager {
	
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
	private void processSync(ExternalIdentity identity)
	{
		ExternalNetwork externalNetwork = ExternalNetwork
				.getNetworkById(identity.getExternalNetwork());

		if (externalNetwork.network.equals(Network.Content))
			processVideos(identity, externalNetwork);
		else if (externalNetwork.equals(ExternalNetwork.Google))
			processVideos(identity, ExternalNetwork.YouTube);

		if(externalNetwork.equals(ExternalNetwork.Google) ) {
			processMessages(identity, externalNetwork);
		}  else if ( externalNetwork.equals(ExternalNetwork.Facebook)||externalNetwork.equals(ExternalNetwork.Twitter)) {
			processMessages(identity, externalNetwork);
			processActivities(identity, externalNetwork);
		}
		else if(externalNetwork.equals(ExternalNetwork.LinkedIn))
		{
			processActivities(identity, ExternalNetwork.LinkedIn);
		}
	}

	private void processActivities(ExternalIdentity identity,
			ExternalNetwork socialNetwork) {
		SocialService socialService = ServiceFactory.getSocialService();
		List<Activity> synced = socialService.syncActivities(identity,
				socialNetwork);

		// index for searching
		ServiceFactory.getSearchService().indexActivities(identity.getUser().getUserId(), synced);
	}

	/***
	 * Process videos for this content provider
	 * 
	 * @param identity
	 * @param externalNetwork
	 */
	private void processVideos(ExternalIdentity identity,
			ExternalNetwork externalNetwork) {

		ContentService contentService = ServiceFactory.getContentService();
		List<VideoContent> synced = contentService.sync(identity,
				externalNetwork);

		// add videos to search results for this specific user
		ServiceFactory.getSearchService().indexVideos(identity.getUser().getUserId(), synced);
	}

	/***
	 * Process messages for this external network
	 * 
	 * @param identity
	 * @param network
	 */
	private void processMessages(ExternalIdentity identity,
			ExternalNetwork network) {
		SocialService socialService = ServiceFactory.getSocialService();

		List<com.ubiquity.social.domain.Message> messages = socialService
				.syncMessages(identity, network);

		// add messages to search results
		ServiceFactory.getSearchService().indexMessages(messages);
	}
	/***
	 * Refresh data of all users in all social networks
	 * @return
	 */
	public int syncData() {

		int numRefreshed = 0;

		try {	
			List<User> users = ServiceFactory.getUserService().findAll();
			for(User user : users) {
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
		List<ExternalIdentity> identities = ServiceFactory.getExternalIdentityService().findExternalIdentityByUserID(user.getUserId());
		for (ExternalIdentity identity : identities) {
			try
			{
				processSync(identity);
			}catch(Exception ex)
			{
				log.debug(ex.getMessage());
			}
		}
		return 0;
	}
}
