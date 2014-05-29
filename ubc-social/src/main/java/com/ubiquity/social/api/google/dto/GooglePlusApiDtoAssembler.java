package com.ubiquity.social.api.google.dto;

import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.social.api.google.dto.model.GooglePersonDto;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.SocialNetwork;

/***
 * Assembler class for assembling a list of events from an FB graph result
 * 
 * @author chris
 *
 */
public class GooglePlusApiDtoAssembler {
		
	/***
	 * Returns a contact 
	 * 
	 * @param result A "me" result
	 * 
	 * @return a contact entity without an internal pk
	 */
	public static Contact assembleContact(ExternalIdentity identity, GooglePersonDto result) {
		
		/**
		 * Set values provided by google for this identity 
		 */
		identity.setIdentifier(result.getId());
		identity.setIsActive(Boolean.TRUE);
		identity.setLastUpdated(System.currentTimeMillis());
		identity.setIdentityProvider(SocialNetwork.Google.getValue());
		
		Contact contact = new Contact.Builder()
			.externalIdentity(identity)
			.firstName(result.getFirstName())
			.lastName(result.getLastName())
			.displayName(result.getDisplayName())
			.lastUpdated(System.currentTimeMillis())
			.owner(identity.getUser())
			.image(result.getImage())
		.build();

		return contact;
	}
	
	public static Contact assembleContact(User owner, GooglePersonDto result) {
	
		Contact contact = new Contact.Builder()
		.externalIdentity(new ExternalIdentity.Builder()
			.identifier(result.getId())
			.isActive(Boolean.TRUE)
			.lastUpdated(System.currentTimeMillis())
			.identityProvider(SocialNetwork.Google.getValue()).build())
		.firstName(result.getFirstName())
		.lastName(result.getLastName())
		.displayName(result.getDisplayName())
		.lastUpdated(System.currentTimeMillis())
		.owner(owner)
		.image(result.getImage())
		.build();
		
		return contact;
		
		
	}

	

}
