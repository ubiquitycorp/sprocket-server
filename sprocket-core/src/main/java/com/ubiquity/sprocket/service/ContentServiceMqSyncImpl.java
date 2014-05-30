package com.ubiquity.sprocket.service;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;

import com.niobium.repository.cache.DataCacheKeys;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.messaging.format.Message;
import com.ubiquity.social.domain.ContentNetwork;
import com.ubiquity.social.repository.cache.SocialCacheKeys;
import com.ubiquity.sprocket.messaging.MessageConverterFactory;
import com.ubiquity.sprocket.messaging.MessageQueueFactory;
import com.ubiquity.sprocket.messaging.definition.UserRegistered;

public class ContentServiceMqSyncImpl extends AbstractContentService implements ContentService {

	public ContentServiceMqSyncImpl(Configuration configuration) {
		super(configuration);
	}

	@Override
	public void sync(ExternalIdentity identity,
			ContentNetwork network) {
		
		User user = identity.getUser();
		//  Sets the value of this cache to -1, which will be available to the code processing collection variant;
		dataModificationCache.put(user.getUserId(), SocialCacheKeys.UserProperties.VIDEOS, DataCacheKeys.Values.NO_CONTENT);
		
		// send notification interested consumers
		String message = MessageConverterFactory.getMessageConverter().serialize(new Message(new UserRegistered(user.getUserId())));
		try {
			MessageQueueFactory.getCacheInvalidationQueueProducer().write(message.getBytes());
		} catch (IOException e) {
			throw new RuntimeException("Unable to connect to Message Queue: {}", e);
		}

	}
	
	

}
