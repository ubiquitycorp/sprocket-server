package com.ubiquity.social.api.linkedin.dto;

import com.ubiquity.identity.domain.User;
import com.ubiquity.media.domain.Image;
import com.ubiquity.social.api.linkedin.dto.model.LinkedInConnectionDto;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.SocialIdentity;
import com.ubiquity.social.domain.SocialProviderType;

public class LinkedInApiDtoAssembler {

	public static Contact assembleContact(User owner, LinkedInConnectionDto result) {

		Contact.Builder contactBuilder = new Contact.Builder()
		.socialIdentity(new SocialIdentity.Builder()
			.identifier(result.getId())
			.isActive(Boolean.TRUE)
			.lastUpdated(System.currentTimeMillis())
			.socialProviderType(SocialProviderType.Google).build())
		.firstName(result.getFirstName())
		.lastName(result.getLastName())
		.lastUpdated(System.currentTimeMillis())
		.displayName(result.getFormattedName())
		.owner(owner);

		if(result.getPictureUrl() != null)
			contactBuilder.image(new Image(result.getPictureUrl()));

		return contactBuilder.build();


	}
	public static Contact assembleContact(SocialIdentity identity, LinkedInConnectionDto result) {

		identity.setIdentifier(result.getId());
		identity.setIsActive(Boolean.TRUE);
		identity.setLastUpdated(System.currentTimeMillis());
		identity.setSocialProviderType(SocialProviderType.LinkedIn);

		Contact.Builder contactBuilder = new Contact.Builder()
		.socialIdentity(identity)
		.firstName(result.getFirstName())
		.lastName(result.getLastName())
		.lastUpdated(System.currentTimeMillis())
		.displayName(result.getFormattedName())
		.owner(identity.getUser());

		if(result.getPictureUrl() != null)
			contactBuilder.image(new Image(result.getPictureUrl()));

		return contactBuilder.build();
	}


}
