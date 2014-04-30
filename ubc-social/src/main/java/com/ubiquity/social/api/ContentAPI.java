package com.ubiquity.social.api;

import java.util.List;

import com.ubiquity.social.domain.ExternalIdentity;
import com.ubiquity.social.domain.VideoContent;

public interface ContentAPI {
	
	List<VideoContent> findVideosByExternalIdentity(ExternalIdentity externalIdentity);

}
