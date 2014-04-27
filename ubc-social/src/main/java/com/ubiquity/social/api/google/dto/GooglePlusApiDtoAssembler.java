package com.ubiquity.social.api.google.dto;

import com.ubiquity.social.api.google.dto.model.GooglePersonDto;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.SocialIdentity;
import com.ubiquity.social.domain.SocialProviderType;

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
	public static Contact assembleContact(SocialIdentity identity, GooglePersonDto result) {
		
		identity.setIdentifier(result.getId());
		identity.setIsActive(Boolean.TRUE);
		identity.setLastUpdated(System.currentTimeMillis());
		identity.setSocialProviderType(SocialProviderType.Google);
		
		Contact contact = new Contact.Builder()
			.socialIdentity(identity)
			.firstName(result.getFirstName())
			.lastName(result.getLastName())
			.displayName(result.getDisplayName())
			.lastUpdated(System.currentTimeMillis())
			.owner(identity.getUser())
			.image(result.getImage())
		.build();

		return contact;
	}

	

}
