package com.ubiquity.sprocket.repository;

import java.util.List;

import com.niobium.repository.Repository;
import com.ubiquity.sprocket.domain.ContentNetwork;
import com.ubiquity.sprocket.domain.VideoContent;

/***
 * 
 * Interface exposing CRUD methods for the video content
 * 
 * @author chris
 *
 */
public interface VideoContentRepository extends Repository <Long, VideoContent> {
	/***
	 * Finds all videos owned by this user
	 * 
	 * @param ownerId
	 * 
	 * @return
	 */
	List<VideoContent> findByOwnerId(Long ownerId);
	
	/***
	 * Find all videos that match the item key of the video
	 * 
	 * @param ownerId
	 * @param itemKey
	 * 
	 * @return
	 */
	List<VideoContent> findByOwnerIdAndItemKey(Long ownerId, String itemKey);
	
	/***
	 * Finds videos by owner and content network
	 * 
	 * @param ownerId
	 * @param contentNetwork
	 * @return
	 */
	List<VideoContent> findByOwnerIdAndContentNetwork(Long ownerId, ContentNetwork contentNetwork);
	
	/***
	 * Deletes are records without the videocontent ids
	 * @param videoContentIds
	 */
	int deleteWithoutIds(Long ownerId, List<Long> videoContentIds);

}

