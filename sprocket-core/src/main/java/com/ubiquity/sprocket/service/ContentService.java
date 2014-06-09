package com.ubiquity.sprocket.service;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.CollectionVariant;
import com.niobium.repository.cache.DataCacheKeys;
import com.niobium.repository.cache.UserDataModificationCache;
import com.niobium.repository.cache.UserDataModificationCacheRedisImpl;
import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.content.api.ContentAPIFactory;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.sprocket.domain.ContentNetwork;
import com.ubiquity.sprocket.domain.VideoContent;
import com.ubiquity.sprocket.repository.VideoContentRepository;
import com.ubiquity.sprocket.repository.VideoContentRepositoryJpaImpl;
import com.ubiquity.sprocket.repository.cache.SprocketCacheKeys;


public class ContentService {
	
	private VideoContentRepository videoContentRepository;
	private UserDataModificationCache dataModificationCache;
	private Logger log = LoggerFactory.getLogger(getClass());
	
	public ContentService(Configuration configuration) {
		videoContentRepository = new VideoContentRepositoryJpaImpl();
		dataModificationCache = new UserDataModificationCacheRedisImpl(configuration.getInt(
				DataCacheKeys.Databases.ENDPOINT_MODIFICATION_DATABASE_USER));
		
		ContentAPIFactory.initialize(configuration);
	}
	
	
	/**
	 * Service will synchronize videos with external content network.
	 * 
	 * @param identity
	 * @param network
	 */
	public List<VideoContent> sync(ExternalIdentity identity, ContentNetwork network) {
		
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
			try {
			
				VideoContent persisted = getVideoByItemKeyAndOwner(ownerId, videoContent.getVideo().getItemKey());
				if(persisted == null) {
					// save the video we got from from the content network
					
					
						create(videoContent);
					
				} else {
					// the only thing we want to retain from the persisted record is the pk value
					videoContent.setVideoContentId(persisted.getVideoContentId());
					update(videoContent);
				}
			
				processedIds.add(videoContent.getVideoContentId());
			} catch (Exception e) {
				log.warn("Could not process content, skipping record.", e);
			}
		}
		
		if(!processedIds.isEmpty()) {
			// now remove old videos
			EntityManagerSupport.beginTransaction();
			videoContentRepository.deleteWithoutIds(ownerId, processedIds);
			EntityManagerSupport.commit();
			
			// update data modification cache
			dataModificationCache.put(ownerId, SprocketCacheKeys.UserProperties.VIDEOS, System.currentTimeMillis());
		}
		
		return videoContentList;

	}
	
	/***
	 * Returns a video entity by this properties or null if one does not exist
	 * 
	 * @param ownerId
	 * @param itemKey
	 * @return
	 */
	private VideoContent getVideoByItemKeyAndOwner(Long ownerId, String itemKey) {
		List<VideoContent> content = videoContentRepository.findByOwnerIdAndItemKey(ownerId, itemKey);
		return content.isEmpty() ? null : content.get(0);
	}
	
	/***
	 * Returns video content or null if there is no entry for this user in the cache
	 * 
	 * @param ownerId
	 * @param ifModifiedSince
	 * @return
	 */
	public CollectionVariant<VideoContent> findAllVideosByOwnerIdAndContentNetwork(Long ownerId, ContentNetwork contentNetwork, Long ifModifiedSince) {

		Long lastModified = dataModificationCache.getLastModified(ownerId, SprocketCacheKeys.UserProperties.VIDEOS, ifModifiedSince);

		// If there is no cache entry, there is no data
		if(lastModified == null) {
			return null;
		}

		List<VideoContent> videos = videoContentRepository.findByOwnerId(ownerId);
		return new CollectionVariant<VideoContent>(videos, lastModified);
	}
	
	/***
	 * Persists a video
	 * 
	 * @param videoContent
	 */
	private void create(VideoContent videoContent) {
		try {	
			EntityManagerSupport.beginTransaction();
			videoContentRepository.create(videoContent);
			EntityManagerSupport.commit();
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}
	
	/***
	 * Updates video, setting the last update date to the current date/time
	 * 
	 * @param videoContent
	 */
	private void update(VideoContent videoContent) {
		try {	
			videoContent.setLastUpdated(System.currentTimeMillis());
			EntityManagerSupport.beginTransaction();
			videoContentRepository.update(videoContent);
			EntityManagerSupport.commit();
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}

}
