package com.ubiquity.social.repository;

import java.util.List;

import com.niobium.repository.Repository;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.SocialProvider;

/***
 * 
 * Interface exposing CRUD methods for the contact entity
 * 
 * @author chris
 *
 */
public interface ContactRepository extends Repository <Long, Contact> {
	/***
	 * Finds all contacts
	 * 
	 * @param ownerId
	 * @param active  Flag to indicate whether to return all or just active contacts
	 * 
	 * @return
	 */
	List<Contact> findByOwnerId(Long ownerId, Boolean active);

	
	/**
	 * Finds a contact in the system with by identity pk
	 * 
	 * @param identityId
	 * @return
	 */
	Contact getBySocialIdentityId(Long identityId);
	/***
	 * Returns all contacts by owner and provider
	 * 
	 * @param ownerId
	 * @param type Type of provider (i.e. Facebook)
	 * @return
	 */
	List<Contact> findByOwnerIdAndSocialIdentityProvider(Long ownerId, SocialProvider type);
	
	/***
	 * Counts the number of contacts for this user
	 * 
	 * @param ownerId
	 * @param socialProviderType
	 * @return
	 */
	int countAllContactsByOwnerIdAndSocialIdentityProvider(Long ownerId, SocialProvider socialProviderType);
}

