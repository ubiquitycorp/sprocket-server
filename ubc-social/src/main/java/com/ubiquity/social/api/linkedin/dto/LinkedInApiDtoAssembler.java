package com.ubiquity.social.api.linkedin.dto;

import com.ubiquity.media.domain.Image;
import com.ubiquity.social.api.linkedin.dto.model.LinkedInConnectionDto;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.SocialIdentity;
import com.ubiquity.social.domain.SocialProviderType;

public class LinkedInApiDtoAssembler {

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
