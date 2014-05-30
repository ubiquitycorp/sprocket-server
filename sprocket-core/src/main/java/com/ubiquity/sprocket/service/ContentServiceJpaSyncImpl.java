package com.ubiquity.sprocket.service;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.social.api.ContentAPIFactory;
import com.ubiquity.social.domain.ContentNetwork;
import com.ubiquity.social.domain.VideoContent;
import com.ubiquity.social.repository.cache.SocialCacheKeys;

public class ContentServiceJpaSyncImpl extends AbstractContentService implements ContentService {

	public ContentServiceJpaSyncImpl(Configuration configuration) {
		super(configuration);
	}

	/**
	 * Service will synchronize videos with external content network.
	 * 
	 * @param identity
	 * @param network
	 */
	@Override
	public void sync(ExternalIdentity identity, ContentNetwork network) {
		
		// For YouTube, use the item key as the external identifier;
		List<VideoContent> videoContentList = ContentAPIFactory.createProvider(network).findVideosByExternalIdentity(identity);
		
		// the owner is this identitie's user
		Long ownerId = identity.getUser().getUserId();
		
		// Keep track of processed ids
		List<Long> processedIds = new LinkedList<Long>();
		
		// currently we are not supporting eTag until we know which feeds we'll be processing for YouTube; right now
		// most popular will likely be changing daily, so we simply remove / update entries by the item key of the video
		for(VideoContent videoContent : videoContentList) {
			// find video by this id
			VideoContent persisted = getVideoByItemKeyAndOwner(ownerId, videoContent.getVideo().getItemKey());
			if(persisted == null) {
				// save the video we got from from the content network
				create(videoContent);
			} else {
				update(videoContent);
			}
			
			processedIds.add(videoContent.getVideoContentId());
		}
		
		if(!processedIds.isEmpty()) {
			// now remove old videos
			EntityManagerSupport.beginTransaction();
			videoContentRepository.deleteWithoutIds(ownerId, processedIds);
			EntityManagerSupport.commit();
			
			// update data modification cache
			dataModificationCache.put(ownerId, SocialCacheKeys.UserProperties.VIDEOS, System.currentTimeMillis());
		}

	}

}
