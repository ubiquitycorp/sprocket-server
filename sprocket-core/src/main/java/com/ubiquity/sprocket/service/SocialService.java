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
import com.ubiquity.identity.domain.Identity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.social.api.SocialAPI;
import com.ubiquity.social.api.SocialAPIFactory;
import com.ubiquity.social.domain.Message;
import com.ubiquity.social.domain.SocialNetwork;
import com.ubiquity.social.repository.MessageRepository;
import com.ubiquity.social.repository.MessageRepositoryJpaImpl;
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
	private MessageRepository messageContentRepository;	
	public SocialService(Configuration configuration) {
		messageContentRepository = new MessageRepositoryJpaImpl();	
		dataModificationCache = new UserDataModificationCacheRedisImpl(configuration.getInt(
				DataCacheKeys.Databases.ENDPOINT_MODIFICATION_DATABASE_USER));
		
		
		ContentAPIFactory.initialize(configuration);
		
	}
	
	
	/***
	 * Returns video content or null if there is no entry for this user in the cache
	 * 
	 * @param ownerId
	 * @param ifModifiedSince
	 * @return
	 */
	public CollectionVariant<Message> findAllMessagesByOwnerIdAndSocialNetwork(Long ownerId, SocialNetwork socialNetwork) {

		
		List<Message> messages =  messageContentRepository.findByOwnerIdAndSocialNetwork(ownerId,socialNetwork);
		//List<VideoContent> videos = videoContentRepository.findByOwnerId(ownerId);
		//return new CollectionVariant<VideoContent>(videos, lastModified);
		
		return new CollectionVariant<Message>(messages, null);
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
			// find video by this id
			Message persisted = getMessageByOwnerIdAndSocialNetwork(ownerId, network);
			if(persisted == null) {
				// save the video we got from from the content network
				create(messages);
			} else {
				// the only thing we want to retain from the persisted record is the pk value
				
				messages.setMessageId(persisted.getMessageId());
				update(messages);
			}
			
			processedIds.add(messages.getMessageId());
			
			
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
	
	
	
	/***
	 * Returns a video entity by this properties or null if one does not exist
	 * 
	 * @param ownerId
	 * @param itemKey
	 * @return
	 */
	private Message getMessageByOwnerIdAndSocialNetwork(Long ownerId, SocialNetwork socialNetwork) {
		List<Message> messages =  messageContentRepository.findByOwnerIdAndSocialNetwork(ownerId, socialNetwork);
		return messages.isEmpty() ? null : messages.get(0);
	}
	
	/***
	 * Persists a  message
	 * 
	 * @param Message
	 */
	private void create(Message message) {
		try {	
			EntityManagerSupport.beginTransaction();
			messageContentRepository.create(message);
			EntityManagerSupport.commit();
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}
	
	/***
	 * Updates message, setting the last update date to the current date/time
	 * 
	 * @param Message
	 */
	private void update(Message message) {
		try {	
			//message.setLastUpdated(System.currentTimeMillis());
			EntityManagerSupport.beginTransaction();
			messageContentRepository.update(message);
			EntityManagerSupport.commit();
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}
	
	
	
}
