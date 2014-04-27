package com.ubiquity.social.service;

import java.util.List;

import org.apache.commons.configuration.Configuration;

import com.niobium.repository.CollectionVariant;
import com.niobium.repository.cache.DataCacheKeys;
import com.niobium.repository.cache.UserDataModificationCache;
import com.niobium.repository.cache.UserDataModificationCacheRedisImpl;
import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.SocialProviderType;
import com.ubiquity.social.repository.ContactRepository;
import com.ubiquity.social.repository.ContactRepositoryJpaImpl;
import com.ubiquity.social.repository.cache.SocialCacheKeys;

public class ContactService {

	private UserDataModificationCache dataModificationCache;
	private ContactRepository contactRepository;

	/***
	 * Parameterized constructor builds a manager with required configuration property
	 * 
	 * @param configuration
	 */
	public ContactService(Configuration configuration) {
		dataModificationCache = new UserDataModificationCacheRedisImpl(configuration.getInt(
				DataCacheKeys.Databases.ENDPOINT_MODIFICATION_DATABASE_USER));
		contactRepository = new ContactRepositoryJpaImpl();
	}

	/***
	 * Finds contact by contact Id
	 * @param contactId
	 * 
	 * @return contact
	 * 
	 */
	public Contact getByContactId(Long contactId) {
		try {
			return contactRepository.read(contactId);
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}
	
	public Contact getBySocialIdentityId(Long identityId) {
		try {
			return contactRepository.getBySocialIdentityId(identityId);
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}

	/***
	 * Returns the full list of contacts if there has been any modification. If there is no entry,
	 * this routine will create time stamp record of zero
	 * 
	 * @param ownerId Owner of the contacts
	 * @param ifModifiedBy or null for the whole list
	 * 
	 * @return
	 */
	public CollectionVariant<Contact> findAllContactsByOwnerId(Long ownerId, Long ifModifiedBy) {

		Long lastModified = dataModificationCache.getLastModified(ownerId, SocialCacheKeys.UserProperties.CONTACTS, ifModifiedBy);

		// If there is no cache entry, there are no contacts
		if(lastModified == null) {
			return null;
		}
		
		List<Contact> contacts;
		try {				
			contacts = contactRepository.findByOwnerId(ownerId, Boolean.TRUE);
			return new CollectionVariant<Contact>(contacts, lastModified);
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}


	/***
	 * Determines if this use has contacts for the specified provider type by measuring
	 * the number of database entries in the local database.
	 * 
	 * @param userId
	 * @param type
	 * @return
	 */
	public boolean hasContactsForProvider(Long userId, SocialProviderType type) {
		try {
			int count = contactRepository.countAllContactsByOwnerIdAndSocialIdentityProvider(userId, type);
			return count > 0 ? true : false;
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}

	/***
	 * Creates a contact in the underlying database.
	 * 
	 * @param contact
	 */
	public void create(Contact contact) {
		EntityManagerSupport.beginTransaction();
		try {
			contactRepository.create(contact);
			EntityManagerSupport.commit();
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
		// Update last modified cache
		updateCacheTime(contact.getOwner().getUserId());
	}


	public void updateCacheTime(Long ownerId){
		// Update last modified cache
		dataModificationCache.put(ownerId, SocialCacheKeys.UserProperties.CONTACTS, System.currentTimeMillis());
	}


}
