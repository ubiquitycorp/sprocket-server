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
import com.ubiquity.integration.service.SocialService;
import com.ubiquity.sprocket.datasync.worker.manager.SyncProcessor;
import com.ubiquity.sprocket.datasync.worker.manager.ResourceType;
import com.ubiquity.sprocket.service.ServiceFactory;

/***
 * 
 * @author peter.tadros
 * 
 */
public class ActivityHandler extends Handler {

	public ActivityHandler(SyncProcessor processor) {
		super(processor);
		networks = EnumSet.of(ExternalNetwork.Twitter,
				ExternalNetwork.Facebook, ExternalNetwork.LinkedIn,
				ExternalNetwork.Tumblr, ExternalNetwork.Reddit);
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
		int size = -1;
		try {
			SocialService socialService = ServiceFactory.getSocialService();
			synced = socialService.syncActivities(identity, network);
			// index for searching
			ServiceFactory.getSearchService().indexActivities(userId, synced,
					false);
			log.debug(" indexing activities for identity {}", identity);

		} catch (AuthorizationException e) {
			ServiceFactory.getExternalIdentityService().deactivateExternalIdentity(identity);
			log.error("Could not process activities for identity {}: set active to false ,exception: {}", identity,
					ExceptionUtils.getStackTrace(e));
			return -1;
		} catch (Exception e) {
			log.error("Could not process activities for identity{}: {}", identity,
					ExceptionUtils.getStackTrace(e));
			return -1;
		} finally {
			size = (synced == null) ? -1 : synced.size();
			log.debug(" Processed {} activities in {} seconds for user "
					+ userId, size,
					new Period(start, new DateTime()).getSeconds());
		}

		return size;
	}

}
