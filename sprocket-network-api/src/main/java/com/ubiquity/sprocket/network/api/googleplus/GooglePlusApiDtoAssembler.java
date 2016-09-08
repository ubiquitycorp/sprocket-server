package com.ubiquity.sprocket.network.api.googleplus;

import java.util.UUID;

import com.ubiquity.sprocket.network.api.dto.model.Contact;
import com.ubiquity.sprocket.network.api.googleplus.model.GoogleEmail;
import com.ubiquity.sprocket.network.api.googleplus.model.GooglePersonDto;

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
	 * @param result
	 *            A "me" result
	 * 
	 * @return a contact entity without an internal pk
	 */
	public static GooglePersonDto assembleContact(Contact contact) {

		/**
		 * Set values provided by google for this identity
		 */
		GooglePersonDto.Builder googlePersonDtoBuilder = new GooglePersonDto.Builder();
		GoogleEmail.Builder googleEmailBuilder = new GoogleEmail.Builder();
		googleEmailBuilder.type("  ").value(UUID.randomUUID().toString());

		GooglePersonDto googlePersonDto = googlePersonDtoBuilder
				.id(contact.getExternalIdentity().getIdentifier())
				.firstName(contact.getFirstName())
				.lastName(contact.getLastName())
				.displayName(contact.getDisplayName())
				.image(contact.getImage()).build();
		googlePersonDto.getEmail()[0] = googleEmailBuilder.build();
		return googlePersonDto;
	}
}
