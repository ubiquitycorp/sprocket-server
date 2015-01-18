package com.ubiquity.sprocket.datasync.handlers;

import java.util.EnumSet;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;

import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.integration.api.exception.AuthorizationException;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.service.SocialService;
import com.ubiquity.sprocket.datasync.worker.manager.ResourceType;
import com.ubiquity.sprocket.datasync.worker.manager.SyncProcessor;
import com.ubiquity.sprocket.service.ServiceFactory;

/***
 * 
 * @author peter.tadros
 * 
 */
public class MessageHandler extends Handler {

	public MessageHandler(SyncProcessor processor) {
		super(processor);
		networks = EnumSet.of(ExternalNetwork.Twitter,
				ExternalNetwork.Facebook, ExternalNetwork.Google,
				ExternalNetwork.Tumblr);
	}

	@Override
	protected void syncData(ExternalIdentity identity, ExternalNetwork network) {
		Long userId = identity.getUser().getUserId();
		// Sync messages
		int n = processMessages(identity, network, null);
		processor.sendStepCompletedMessageToIndividual(backchannel, network,
				"Synchronized messages", processor.getResoursePath(userId,
						network, ResourceType.messages), n, userId,
				ResourceType.messages);
	}

	/***
	 * Process messages for this external network
	 * 
	 * @param identity
	 * @param network
	 */
	public int processMessages(ExternalIdentity identity,
			ExternalNetwork network, String lastMessageIdentifier) {

		List<com.ubiquity.integration.domain.Message> synced = null;
		DateTime start = new DateTime();
		Long userId = identity.getUser().getUserId();
		try {
			SocialService socialService = ServiceFactory.getSocialService();

			synced = socialService.syncMessages(identity, network,
					lastMessageIdentifier, processedMessages);

			// add messages to search results
			ServiceFactory.getSearchService().indexMessages(
					identity.getUser().getUserId(), synced);
			log.debug(" indexing messages for identity {}", identity);
			return synced.size();
		} catch (AuthorizationException e) {
			ServiceFactory.getSocialService().setActiveNetworkForUser(userId,
					network, false);
			log.error(" Could not process messages for identity: {}",
					ExceptionUtils.getStackTrace(e));
			return -1;
		} catch (Exception e) {
			log.error(" Could not process messages for identity: {}",
					ExceptionUtils.getStackTrace(e));
			return -1;
		} finally {
			int n = (synced == null) ? -1 : synced.size();
			log.debug(
					" Processed {} messages in {} seconds for user " + userId,
					n, new Period(start, new DateTime()).getSeconds());
		}
	}

}
