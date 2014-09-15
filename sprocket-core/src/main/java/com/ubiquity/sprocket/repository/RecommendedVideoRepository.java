package com.ubiquity.sprocket.repository;

import java.util.List;

import com.niobium.repository.Repository;
import com.ubiquity.content.domain.VideoContent;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.sprocket.domain.RecommendedVideo;


public interface RecommendedVideoRepository extends Repository <Long, RecommendedVideo>  {
	
	/**
	 * Returns recommended videos
	 * 
	 * @param group
	 * @return List of average engaged videos
	 */
	List<VideoContent> findRecommendedVideosByGroup(String group);

	/**
	 * Returns all by network
	 * 
	 * @param network
	 * @return
	 */
	List<RecommendedVideo> findAllByExternalNetwork(ExternalNetwork network);

}
