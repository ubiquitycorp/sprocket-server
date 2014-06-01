package com.ubiquity.content.api;

import java.util.List;

import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.sprocket.domain.VideoContent;

/***
 * Interface defining common operations for content networks
 * 
 * @author chris
 *
 */
public interface ContentAPI {
	
	/**
	 * Find a list of videos for this identity; implementations will set their own rules for 
	 * how filters are applied
	 * 
	 * @param externalIdentity
	 * 
	 * @return
	 */
	List<VideoContent> findVideosByExternalIdentity(ExternalIdentity externalIdentity);

}
