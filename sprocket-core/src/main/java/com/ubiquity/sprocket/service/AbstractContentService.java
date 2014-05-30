package com.ubiquity.sprocket.service;

import java.util.List;

import org.apache.commons.configuration.Configuration;

import com.niobium.repository.cache.DataCacheKeys;
import com.niobium.repository.cache.UserDataModificationCache;
import com.niobium.repository.cache.UserDataModificationCacheRedisImpl;
import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.social.domain.VideoContent;
import com.ubiquity.social.repository.VideoContentRepository;
import com.ubiquity.social.repository.VideoContentRepositoryJpaImpl;


public abstract class AbstractContentService {
	
	protected VideoContentRepository videoContentRepository;
	protected UserDataModificationCache dataModificationCache;

	public AbstractContentService(Configuration configuration) {
		videoContentRepository = new VideoContentRepositoryJpaImpl();
		dataModificationCache = new UserDataModificationCacheRedisImpl(configuration.getInt(
				DataCacheKeys.Databases.ENDPOINT_MODIFICATION_DATABASE_USER));
	}
	
	
	
	/***
	 * Returns a video entity by this properties or null if one does not exist
	 * 
	 * @param ownerId
	 * @param itemKey
	 * @return
	 */
	public VideoContent getVideoByItemKeyAndOwner(Long ownerId, String itemKey) {
		List<VideoContent> content = videoContentRepository.findByOwnerIdAndItemKey(ownerId, itemKey);
		return content.isEmpty() ? null : content.get(0);
	}
	
	/***
	 * Persists a video
	 * 
	 * @param videoContent
	 */
	public void create(VideoContent videoContent) {
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
	public void update(VideoContent videoContent) {
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
