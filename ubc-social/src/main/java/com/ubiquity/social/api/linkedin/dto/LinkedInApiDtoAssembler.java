package com.ubiquity.social.api.linkedin.dto;

import com.ubiquity.media.domain.Image;
import com.ubiquity.social.api.linkedin.dto.model.LinkedInConnectionDto;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.SocialIdentity;
import com.ubiquity.social.domain.SocialProviderType;

public class LinkedInApiDtoAssembler {

	public static Contact assembleContact(LinkedInConnectionDto result) {
		Contact.Builder contactBuilder = new Contact.Builder()
		.socialIdentity(new SocialIdentity.Builder()
		.identifier(result.getId())
		.isActive(Boolean.TRUE)
		.socialProviderType(SocialProviderType.LinkedIn).build())
		.firstName(result.getFirstName())
		.lastName(result.getLastName())
		.displayName(result.getFormattedName());

		if(result.getPictureUrl() != null)
			contactBuilder.image(new Image(result.getPictureUrl()));

		return contactBuilder.build();
	}


}
