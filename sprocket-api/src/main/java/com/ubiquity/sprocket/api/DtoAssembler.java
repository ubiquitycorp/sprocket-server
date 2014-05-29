package com.ubiquity.sprocket.api;

import java.util.UUID;

import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.ExternalIdentity;
import com.ubiquity.social.domain.Message;
import com.ubiquity.social.domain.SocialProvider;
import com.ubiquity.sprocket.api.dto.model.ActivityDto;
import com.ubiquity.sprocket.api.dto.model.ContactDto;
import com.ubiquity.sprocket.api.dto.model.IdentityDto;
import com.ubiquity.sprocket.api.dto.model.MessageDto;

public class DtoAssembler {

	public static ContactDto assemble(Contact contact) {
		ContactDto.Builder contactDtoBuilder = new ContactDto.Builder()
				.contactId(contact.getContactId())
				.displayName(contact.getDisplayName())
				.firstName(contact.getFirstName())
				.lastName(contact.getLastName()).email(contact.getEmail())
				.profileUrl(contact.getProfileUrl())
				.etag(UUID.randomUUID().toString());

		ExternalIdentity identity = contact.getSocialIdentity();
		if (identity != null)
			contactDtoBuilder
					.identity(new IdentityDto.Builder()
							.identifier(identity.getIdentifier())
							.identityProviderId(
									identity.getSocialProvider().getValue())
							.build());
		// Image is optional
		if (contact.getImage() != null)
			contactDtoBuilder.imageUrl(contact.getImage().getUrl());

		return contactDtoBuilder.build();
	}

	public static MessageDto assemble(Message message) {
		return new MessageDto.Builder().subject(message.getTitle())
				.date(System.currentTimeMillis())
				.socialProviderId(SocialProvider.Google.getValue())
				.body(message.getBody()).sender(assemble(message.getSender()))
				.build();
	}

	public static ActivityDto assemble(Activity activity,
			SocialProvider socialProvider) {
		return new ActivityDto.Builder().body(activity.getBody())
				.date(System.currentTimeMillis())
				.socialProviderId(socialProvider.getValue())
				.title(activity.getTitle()).imageUrl(null)
				.postedBy(DtoAssembler.assemble(activity.getPostedBy()))
				.build();
	}

}
