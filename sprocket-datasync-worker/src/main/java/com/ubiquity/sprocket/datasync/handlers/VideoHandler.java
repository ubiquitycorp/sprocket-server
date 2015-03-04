package com.ubiquity.sprocket.datasync.handlers;

import java.util.EnumSet;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;

import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.ExternalNetworkApplication;
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
	protected void syncData(ExternalIdentity identity, ExternalNetwork network,ExternalNetworkApplication externalNetworkApplication) {
		Long userId = identity.getUser().getUserId();
		int n = processVideos(identity, network, externalNetworkApplication);

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
	private int processVideos(ExternalIdentity identity, ExternalNetwork network, ExternalNetworkApplication externalNetworkApplication) {
		List<VideoContent> synced = null;
		DateTime start = new DateTime();
		Long userId = identity.getUser().getUserId();
		int size = -1;
		try {
			ContentService contentService = ServiceFactory.getContentService();
			synced = contentService.sync(identity, network,externalNetworkApplication);

			// add videos to search results for this specific user
			ServiceFactory.getSearchService()
					.indexVideos(userId, synced, false);
		} catch (AuthorizationException e) {
			identity.setIsActive(false);
			ServiceFactory.getExternalIdentityService().update(identity);
			log.error(" Unable to sync for identity: {} set active to false ,exception: {}",
					identity.getIdentityId(), ExceptionUtils.getStackTrace(e));
		} catch (Exception e) {
			log.error(" Unable to sync for identity: {} ,exception: {}",
					identity.getIdentityId(), ExceptionUtils.getStackTrace(e));
		} finally {
			size = (synced == null) ? -1 : synced.size();
			log.debug(" Processed {} videos in {} seconds for user " + userId,
					size, new Period(start, new DateTime()).getSeconds());
		}
		
		return size;
	}
}
