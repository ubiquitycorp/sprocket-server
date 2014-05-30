package com.ubiquity.sprocket.service;

import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.social.domain.ContentNetwork;
import com.ubiquity.social.domain.VideoContent;

public interface ContentService {
	
	VideoContent getVideoByItemKeyAndOwner(Long ownerId, String itemKey);
	
	void create(VideoContent videoContent);
	
	void update(VideoContent videoContent);

	void sync(ExternalIdentity identity, ContentNetwork network);

}
