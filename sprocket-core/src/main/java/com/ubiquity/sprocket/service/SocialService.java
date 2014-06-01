package com.ubiquity.sprocket.service;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import com.niobium.repository.CollectionVariant;
import com.niobium.repository.cache.DataCacheKeys;
import com.niobium.repository.cache.UserDataModificationCache;
import com.niobium.repository.cache.UserDataModificationCacheRedisImpl;
import com.niobium.repository.jpa.EntityManagerSupport;
import com.sun.mail.handlers.message_rfc822;
import com.ubiquity.content.api.ContentAPIFactory;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.social.api.SocialAPI;
import com.ubiquity.social.api.SocialAPIFactory;
import com.ubiquity.social.domain.Message;
import com.ubiquity.social.domain.SocialNetwork;
import com.ubiquity.sprocket.domain.ContentNetwork;
import com.ubiquity.sprocket.domain.VideoContent;
import com.ubiquity.sprocket.repository.VideoContentRepository;
import com.ubiquity.sprocket.repository.VideoContentRepositoryJpaImpl;
import com.ubiquity.sprocket.repository.cache.SprocketCacheKeys;
/***
 * 
 * @author peter.tadros
 *
 */
public class SocialService {
	private UserDataModificationCache dataModificationCache;
		
	public SocialService(Configuration configuration) {
		dataModificationCache = new UserDataModificationCacheRedisImpl(configuration.getInt(
				DataCacheKeys.Databases.ENDPOINT_MODIFICATION_DATABASE_USER));
	}
	
	
	/***
	 * Returns video content or null if there is no entry for this user in the cache
	 * 
	 * @param ownerId
	 * @param ifModifiedSince
	 * @return
	 */
	public CollectionVariant<Message> findAllMessagesByOwnerIdAndSocialNetwork(Long ownerId, SocialNetwork socialNetwork, Long ifModifiedSince) {

		Long lastModified = dataModificationCache.getLastModified(ownerId, SprocketCacheKeys.UserProperties.MESSAGES, ifModifiedSince);

		// If there is no cache entry, there is no data
		if(lastModified == null) {
			return null;
		}
		return null;

		//List<VideoContent> videos = videoContentRepository.findByOwnerId(ownerId);
		//return new CollectionVariant<VideoContent>(videos, lastModified);
	}
	/**
	 * Service will synchronize videos with external content network.
	 * 
	 * @param identity
	 * @param network
	 */
	public List<Message> sync(ExternalIdentity identity, SocialNetwork network, User user) {
		
		SocialAPI socialApi = SocialAPIFactory.createProvider(network, user.getClientPlatform());
		
		List<Message> messagesList = socialApi.listMessages(identity);
		
		// the owner is this identitie's user
		Long ownerId = identity.getUser().getUserId();
		
		// Keep track of processed ids
		List<Long> processedIds = new LinkedList<Long>();
		
		// currently we are not supporting eTag until we know which feeds we'll be processing for YouTube; right now
		// most popular will likely be changing daily, so we simply remove / update entries by the item key of the video
		for(Message messages : messagesList) {
			/*// find video by this id
			VideoContent persisted = getVideoByItemKeyAndOwner(ownerId, videoContent.getVideo().getItemKey());
			if(persisted == null) {
				// save the video we got from from the content network
				create(videoContent);
			} else {
				// the only thing we want to retain from the persisted record is the pk value
				videoContent.setVideoContentId(persisted.getVideoContentId());
				update(videoContent);
			}
			
			processedIds.add(videoContent.getVideoContentId());*/
		}
		
		if(!processedIds.isEmpty()) {
			// now remove old videos
			EntityManagerSupport.beginTransaction();
			//videoContentRepository.deleteWithoutIds(ownerId, processedIds);
			EntityManagerSupport.commit();
			
			// update data modification cache
			dataModificationCache.put(ownerId, SprocketCacheKeys.UserProperties.VIDEOS, System.currentTimeMillis());
		}
		return null;
		//return videoContentList;

	}
}
