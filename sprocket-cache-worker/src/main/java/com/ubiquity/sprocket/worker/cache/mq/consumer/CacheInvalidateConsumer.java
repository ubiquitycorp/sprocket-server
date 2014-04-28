package com.ubiquity.sprocket.worker.cache.mq.consumer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.amqp.AbstractConsumerThread;
import com.niobium.amqp.MessageQueueChannel;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.Identity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.messaging.MessageConverter;
import com.ubiquity.messaging.format.Message;
import com.ubiquity.social.api.Social;
import com.ubiquity.social.api.SocialFactory;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.SocialIdentity;
import com.ubiquity.social.service.ContactService;
import com.ubiquity.social.service.EventService;
import com.ubiquity.sprocket.messaging.MessageConverterFactory;
import com.ubiquity.sprocket.messaging.definition.SocialIdentityActivated;
import com.ubiquity.sprocket.service.ServiceFactory;

public class CacheInvalidateConsumer extends AbstractConsumerThread {

	private Logger log = LoggerFactory.getLogger(getClass());

	private MessageConverter messageConverter;

	public CacheInvalidateConsumer(MessageQueueChannel queueChannel) {
		super(queueChannel);
		this.messageConverter = MessageConverterFactory.getMessageConverter();
	}

	@Override
	public void processMessage(byte[] msg) {
		// Currently just automatically fan these out to all parties

		try {
			Message message = messageConverter.deserialize(msg, Message.class);
			log.debug("message received: {}", message);
			if(message.getType().equals(SocialIdentityActivated.class.getSimpleName()))
				process((SocialIdentityActivated)message.getContent());


		} catch (Exception e) {
			// For now, log an error and exit until we know all the circumstances under which this can happen
			log.error("Could not process message: {}", e);
			System.exit(0);
		}


	}

	private void process(SocialIdentityActivated content) {
		log.debug("found: {}", content);
		createContacts(content);
		createEvents(content);
		createMessages(content);
	}

	private void createEvents(SocialIdentityActivated content) {
		EventService eventService = ServiceFactory.getEventService();
		eventService.resetEventsCacheTime(content.getUserId());

		// now get this user and sync the contacts for this social identity
		User user = ServiceFactory.getUserService().getUserById(content.getUserId());
		SocialIdentity identity = getSocialIdentity(user, content.getIdentityId());

		// break if the id is bad, possible if there is a reset in the data and the queue is not empty
		if(identity == null) {
			log.warn("Unrecognized social identity {} for user {}, skipping...", content.getIdentityId(), user.getUserId());
			return;
		}
		
		eventService.refreshEventsForSocialIdentity(identity);
		
		


	}

	private void createContacts(SocialIdentityActivated content) {
		// first, set the cache entry to -1 to indicate the cache is being reset
		ContactService contactService = ServiceFactory.getContactService();
		contactService.resetContactsCacheTime(content.getUserId());

		// now get this user and sync the contacts for this social identity
		User user = ServiceFactory.getUserService().getUserById(content.getUserId());
		SocialIdentity identity = getSocialIdentity(user, content.getIdentityId());


		// break if the id is bad, possible if there is a reset in the data and the queue is not empty
		if(identity == null) {
			log.warn("Unrecognized social identity {} for user {}, skipping...", content.getIdentityId(), user.getUserId());
			return;
		}

		ClientPlatform platform = ClientPlatform.getEnum(content.getClientPlatformId());
		Social social = SocialFactory.createProvider(identity.getSocialProviderType(), platform);
		// returns contact entities prepared for insertion into db
		List<Contact> contacts = social.findContactsByOwnerIdentity(identity);
		for(Contact contact : contacts) 
			contactService.create(contact);

		// finally, set the cache time
		contactService.updateCacheTime(System.currentTimeMillis());
	}

	private void createMessages(SocialIdentityActivated content) {

	}
	
	private SocialIdentity getSocialIdentity(User user, Long identityId) {
		SocialIdentity socialIdentity = null;
		for(Identity identity : user.getIdentities()) {
			if(identity.getIdentityId().longValue() == identityId.longValue()) {
				// now we know which social network to sync
				socialIdentity = (SocialIdentity)identity;
				break;
			}
		}
		return socialIdentity;
	}

}
