package com.ubiquity.sprocket.datasync.handlers;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;

import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.integration.api.exception.AuthorizationException;
import com.ubiquity.integration.domain.Activity;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.service.SocialService;
import com.ubiquity.sprocket.datasync.worker.manager.DataSyncProcessor;
import com.ubiquity.sprocket.datasync.worker.manager.ResourceType;
import com.ubiquity.sprocket.service.ServiceFactory;
/***
 * 
 * @author peter.tadros
 *
 */
public class ActivityHandler extends Handler {

	public ActivityHandler(DataSyncProcessor processor, Set<ExternalNetwork> network) {
		super(processor, network);
	}

	@Override
	protected void syncData(ExternalIdentity identity, ExternalNetwork network) {
		Long userId = identity.getUser().getUserId();
		// Sync activities
		int n = processActivities(identity, network);
		processor.sendStepCompletedMessageToIndividual(backchannel, network,
				"Synchronized feed", processor.getResoursePath(userId, network,
						ResourceType.activities), n, userId,
				ResourceType.activities);
	}
	
	
	private int processActivities(ExternalIdentity identity,
			ExternalNetwork network) {
		List<Activity> synced = null;
		DateTime start = new DateTime();
		Long userId = identity.getUser().getUserId();
		String threadName = Thread.currentThread().getName();
		try {
			SocialService socialService = ServiceFactory.getSocialService();
			synced = socialService.syncActivities(identity, network);
			// index for searching
			ServiceFactory.getSearchService().indexActivities(userId, synced,
					false);
			log.info("{}: indexing activities for identity {}", threadName,
					identity);
			return synced.size();
		} catch (AuthorizationException e) {
			ServiceFactory.getSocialService().setActiveNetworkForUser(userId,
					network, false);
			log.error("{}: Could not process activities for identity: {}",
					threadName, ExceptionUtils.getRootCauseMessage(e));
			return -1;
		} catch (Exception e) {
			log.error("{}: Could not process activities for identity: {}",
					threadName, ExceptionUtils.getRootCauseMessage(e));
			return -1;
		} finally {
			int n = (synced == null) ? -1 : synced.size();
			log.info(threadName
					+ " Processed {} activities in {} seconds for user "
					+ userId, n, new Period(start, new DateTime()).getSeconds());
		}
	}

}
