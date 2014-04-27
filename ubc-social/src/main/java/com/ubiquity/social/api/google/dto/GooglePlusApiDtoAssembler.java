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
	public static Contact assembleContact(GooglePersonDto result) {
		Contact contact = new Contact.Builder()
			.socialIdentity(new SocialIdentity.Builder()
				.identifier(result.getId())
				.isActive(Boolean.TRUE)
				.socialProviderType(SocialProviderType.Google).build())
			.firstName(result.getFirstName())
			.lastName(result.getLastName())
			.displayName(result.getDisplayName())
			.image(result.getImage())
		.build();

		return contact;
	}

	

}
