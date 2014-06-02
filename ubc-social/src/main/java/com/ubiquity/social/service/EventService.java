package com.ubiquity.social.service;

import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.common.date.DateUtil;
import com.niobium.repository.CollectionVariant;
import com.niobium.repository.cache.DataCacheKeys;
import com.niobium.repository.cache.UserDataModificationCache;
import com.niobium.repository.cache.UserDataModificationCacheRedisImpl;
import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.repository.UserRepository;
import com.ubiquity.identity.repository.UserRepositoryJpaImpl;
import com.ubiquity.social.api.SocialAPI;
import com.ubiquity.social.api.SocialAPIFactory;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.Event;
import com.ubiquity.social.domain.SocialNetwork;
import com.ubiquity.social.repository.ContactRepository;
import com.ubiquity.social.repository.ContactRepositoryJpaImpl;
import com.ubiquity.social.repository.EventRepository;
import com.ubiquity.social.repository.EventRepositoryJpaImpl;
import com.ubiquity.social.repository.cache.SocialCacheKeys;

public class EventService {

	private Logger log = LoggerFactory.getLogger(getClass());
	private int eventViewInWeeks;
	private EventRepository eventRepository;
	private ContactRepository contactRepository;
	private UserRepository userRepository;
	private UserDataModificationCache dataModificationCache;

	public EventService(Configuration configuration) {
		eventViewInWeeks = configuration.getInt("social.events.viewInWeeks", 4);
		eventRepository = new EventRepositoryJpaImpl();
		contactRepository = new ContactRepositoryJpaImpl();
		userRepository = new UserRepositoryJpaImpl();
		dataModificationCache = new UserDataModificationCacheRedisImpl(
				configuration.getInt(DataCacheKeys.Databases.ENDPOINT_MODIFICATION_DATABASE_USER));
	}

	

	/***
	 * Creates an event, updating all underlying caches
	 * 
	 * @param event
	 * @return
	 */
	public void create(Event event) {
		
		try {
			EntityManagerSupport.beginTransaction();
			eventRepository.create(event);
			EntityManagerSupport.commit();
			
			dataModificationCache.put(event.getUser().getUserId(), SocialCacheKeys.UserProperties.EVENTS, System.currentTimeMillis());

		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	
	}
	
	/***
	 * Returns events for the next month
	 * 
	 * @return
	 */
	public CollectionVariant<Event> findEvents(Long userId, Long ifModifiedSince) {

		Long lastModified = dataModificationCache.getLastModified(userId, SocialCacheKeys.UserProperties.EVENTS, ifModifiedSince);

		// If there is no cache entry, there are no contacts; add a zero value so it returns an empty collection with a zero value
		if(lastModified == null) {
			return null;
		}

		// Get the time interval for a week
		long now = new java.util.Date().getTime();
		long nextWeek = new DateTime().plusWeeks(4).getMillis();

		// First get events for this week
		try {
			List<Event> events = eventRepository.findByUserIdAndTimeInterval(userId, now, nextWeek);
			return new CollectionVariant<Event>(events, lastModified);
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}

	/***
	 * Determines if this use has events.
	 * 
	 * @param userId
	 * @return
	 */
	public boolean hasEvents(Long userId) {
		try {
			int count = eventRepository.countAllEventsByOwnerIdAndSocialIdentityProvider(userId);
			return count > 0 ? true : false;
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}

	
	public int refreshEventsForSocialIdentity(ExternalIdentity identity) {
		
		if(identity.getIdentityProvider() != SocialNetwork.Facebook.getValue())
			throw new UnsupportedOperationException();
		
		SocialAPI social = SocialAPIFactory.createProvider(SocialNetwork.Facebook, identity.getUser().getClientPlatform());

		// get the list of local contacts for this user
		List<Contact> contacts = contactRepository.findByOwnerIdAndSocialIdentityProvider(identity.getUser().getUserId(), SocialNetwork.Facebook);
		// the returned list will be events will be events with no identifiers, but with contacts set
		List<Event> events = social.findEventsCreatedByContacts(identity, contacts);

		// get the cached list
		List<Event> cached = eventRepository.findByUserId(identity.getUser().getUserId());

		// go through recently downloaded events
		for(Event event : events) {

			// set the last modified date (whether it's a create or update)
			event.setLastUpdated(System.currentTimeMillis());

			// if any are within in the next month, let's process them
			if(DateUtil.isWithinWeeksFromNow(event.getStartDate(), eventViewInWeeks)) {
		
				try {
					// Start transaction
					EntityManagerSupport.beginTransaction();
					
					// search event cache by identifier provided by FB
					int idx = cached.indexOf(event);
					if(idx >= 0) {
						log.debug("Found cached event, it's an update");
						Event inCache = cached.get(idx);
						// set the id so we replace the record with data that's coming from the social network
						event.setEventId(inCache.getEventId());
						eventRepository.update(event);
					} else {
						// it's a new one
						log.debug("Creating a new event");
						event.setUser(identity.getUser());
						eventRepository.create(event);
					}
					
					// Commit 
					EntityManagerSupport.commit();
				} finally {
					EntityManagerSupport.closeEntityManager();
				}
				
				dataModificationCache.put(identity.getUser().getUserId(), SocialCacheKeys.UserProperties.EVENTS, System.currentTimeMillis());
			}
		}
		
		return 0;
	}
	
	public int refreshEventsForUser(User user) {
		return 0;
	}
	
	
	/***
	 * Sets the value of this cache to -1, which will tell the web tier to respond with a 204 to indicate a long
	 * loading event that has not yet produced content
	 * 
	 * @param userId
	 */
	public void resetEventsCacheTime(Long userId) {
		dataModificationCache.put(userId, SocialCacheKeys.UserProperties.EVENTS, -1l);
	}

	public void updateCacheTime(Long ownerId){
		// Update last modified cache
		dataModificationCache.put(ownerId, SocialCacheKeys.UserProperties.EVENTS, System.currentTimeMillis());
	}
	
	public int refreshEvents() {

		int numRefreshed = 0;

		try {	
			List<User> users = userRepository.findAll();
			for(User user : users) {
				numRefreshed += refreshEventsForUser(user);
			}
		} finally {
			EntityManagerSupport.closeEntityManager();
		}

		return numRefreshed;

	}



	

}
