package com.ubiquity.sprocket.datasync.handlers;

import java.util.EnumSet;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;

import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.integration.api.exception.AuthorizationException;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.VideoContent;
import com.ubiquity.integration.service.ContentService;
import com.ubiquity.sprocket.datasync.worker.manager.DataSyncProcessor;
import com.ubiquity.sprocket.datasync.worker.manager.ResourceType;
import com.ubiquity.sprocket.service.ServiceFactory;

/***
 * 
 * @author peter.tadros
 * 
 */
public class VideoHandler extends Handler {

	public VideoHandler(DataSyncProcessor processor) {
		super(processor);
		networks = EnumSet.of(ExternalNetwork.YouTube, ExternalNetwork.Vimeo);
	}

	@Override
	protected void syncData(ExternalIdentity identity, ExternalNetwork network) {
		Long userId = identity.getUser().getUserId();
		int n = processVideos(identity, network);

		processor
				.sendStepCompletedMessageToIndividual(backchannel, network,
						"Synchronized videos", processor.getResoursePath(
								userId, network, ResourceType.videos), n,
						userId, ResourceType.videos);
	}

	/***
	 * Process videos for this content provider
	 * 
	 * @param identity
	 * @param network
	 */
	private int processVideos(ExternalIdentity identity, ExternalNetwork network) {
		List<VideoContent> synced = null;
		DateTime start = new DateTime();
		Long userId = identity.getUser().getUserId();
		try {
			ContentService contentService = ServiceFactory.getContentService();
			synced = contentService.sync(identity, network);

			// add videos to search results for this specific user
			ServiceFactory.getSearchService()
					.indexVideos(userId, synced, false);
			return synced.size();
		} catch (AuthorizationException e) {
			ServiceFactory.getSocialService().setActiveNetworkForUser(userId,
					network, false);
			log.error(" Unable to sync for identity: {}",
					identity.getIdentityId(), ExceptionUtils.getStackTrace(e));
			return -1;
		} catch (Exception e) {
			log.error(" Unable to sync for identity: {}",
					identity.getIdentityId(), ExceptionUtils.getStackTrace(e));
			return -1;
		} finally {
			int n = (synced == null) ? -1 : synced.size();
			log.debug(" Processed {} videos in {} seconds for user " + userId,
					n, new Period(start, new DateTime()).getSeconds());
		}
	}
}
