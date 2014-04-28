package com.ubiquity.sprocket.api;

import java.util.UUID;

import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.SocialIdentity;
import com.ubiquity.sprocket.api.dto.model.ContactDto;
import com.ubiquity.sprocket.api.dto.model.IdentityDto;

public class DtoAssembler {

	public static ContactDto assembleContactDto(Contact contact) {
		ContactDto.Builder contactDtoBuilder = new ContactDto.Builder()
			.contactId(contact.getContactId())
			.displayName(contact.getDisplayName())
			.firstName(contact.getFirstName())
			.lastName(contact.getLastName()).email(contact.getEmail())
			.profileUrl(contact.getProfileUrl())
			.etag(UUID.randomUUID().toString());
		
		SocialIdentity identity = contact.getSocialIdentity();
		contactDtoBuilder.identity(
				new IdentityDto.Builder()
					.identifier(identity.getIdentifier())
					.identityProviderId(identity.getSocialProviderType().getValue())
					.build());
		// Image is optional
		if (contact.getImage() != null)
			contactDtoBuilder.imageUrl(contact.getImage().getUrl());

		return contactDtoBuilder.build();
	}
}
