package com.ubiquity.social.api.linkedin.dto;

import com.ubiquity.identity.domain.User;
import com.ubiquity.media.domain.Image;
import com.ubiquity.social.api.linkedin.dto.model.LinkedInConnectionDto;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.ExternalIdentity;
import com.ubiquity.social.domain.SocialProvider;

public class LinkedInApiDtoAssembler {

	public static Contact assembleContact(User owner, LinkedInConnectionDto result) {

		Contact.Builder contactBuilder = new Contact.Builder()
		.socialIdentity(new ExternalIdentity.Builder()
			.identifier(result.getId())
			.isActive(Boolean.TRUE)
			.lastUpdated(System.currentTimeMillis())
			.socialProvider(SocialProvider.Google).build())
		.firstName(result.getFirstName())
		.lastName(result.getLastName())
		.lastUpdated(System.currentTimeMillis())
		.displayName(result.getFormattedName())
		.owner(owner);

		if(result.getPictureUrl() != null)
			contactBuilder.image(new Image(result.getPictureUrl()));

		return contactBuilder.build();


	}
	public static Contact assembleContact(ExternalIdentity identity, LinkedInConnectionDto result) {

		identity.setIdentifier(result.getId());
		identity.setIsActive(Boolean.TRUE);
		identity.setLastUpdated(System.currentTimeMillis());
		identity.setSocialProviderType(SocialProvider.LinkedIn);

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
