package com.ubiquity.sprocket.datasync.handlers;

import java.util.EnumSet;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;

import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.integration.api.exception.AuthorizationException;
import com.ubiquity.integration.domain.Activity;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.sprocket.datasync.worker.manager.ResourceType;
import com.ubiquity.sprocket.datasync.worker.manager.SyncProcessor;
import com.ubiquity.sprocket.service.ServiceFactory;

/***
 * 
 * @author peter.tadros
 * 
 */
public class LocalActivityHandler extends Handler {

	public LocalActivityHandler(SyncProcessor processor) {
		super(processor);
		networks = EnumSet.of(ExternalNetwork.Facebook);
	}

	@Override
	protected void syncData(ExternalIdentity identity, ExternalNetwork network) {
		Long userId = identity.getUser().getUserId();
		// Sync local feed
		int n = processLocalActivities(identity, network);
		processor.sendStepCompletedMessageToIndividual(backchannel, network,
				"Synchronized local feed", processor.getResoursePath(userId,
						network, ResourceType.localfeed), n, userId,
				ResourceType.localfeed);
	}

	private int processLocalActivities(ExternalIdentity identity,
			ExternalNetwork network) {
		List<Activity> synced = null;
		DateTime start = new DateTime();
		Long userId = identity.getUser().getUserId();
		int size = -1;
		try {
			synced = ServiceFactory.getSocialService().syncLocalNewsFeed(
					identity, network);
			int activitiesSize =synced.size();
			if(activitiesSize ==0){
				activitiesSize = ServiceFactory.getSocialService().getCountOfLastLocalActivities(identity,network);
			}
			
		} catch (AuthorizationException e) {
			identity.setIsActive(false);
			ServiceFactory.getExternalIdentityService().update(identity);
			log.error(" Unable to sync local activities for identity {}: {}",
					identity, ExceptionUtils.getStackTrace(e));
			return -1;
		} catch (Exception e) {
			log.error(" Unable to sync local activities for identity {}: {}",
					identity, ExceptionUtils.getStackTrace(e));
		} finally {
			size = (synced == null) ? -1 : synced.size();
			log.debug(" Processed {} local activities in {} seconds for user "
					+ userId, size, new Period(start, new DateTime()).getSeconds());
		}
		return size;
	}

}
