package com.ubiquity.sprocket.api;

import java.util.Map;
import java.util.UUID;

import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.ContentProvider;
import com.ubiquity.social.domain.ExternalIdentity;
import com.ubiquity.social.domain.Message;
import com.ubiquity.social.domain.SocialProvider;
import com.ubiquity.social.domain.VideoContent;
import com.ubiquity.sprocket.api.dto.model.ContactDto;
import com.ubiquity.sprocket.api.dto.model.DocumentDto;
import com.ubiquity.sprocket.api.dto.model.IdentityDto;
import com.ubiquity.sprocket.api.dto.model.ImageDto;
import com.ubiquity.sprocket.api.dto.model.MessageDto;
import com.ubiquity.sprocket.api.dto.model.VideoDto;
import com.ubiquity.sprocket.domain.Document;
import com.ubiquity.sprocket.search.SearchKeys;

public class DtoAssembler {

	public static DocumentDto assemble(Document document) {
		
		Map<String, Object> fields = document.getFields();
		String dataType = (String)fields.get(SearchKeys.CommonFields.FIELD_DATA_TYPE);
		
		Object data = null;
		if(dataType.equals(VideoContent.class.getSimpleName())) {
			data = new VideoDto.Builder()
			.contentProviderId(ContentProvider.YouTube.ordinal())
			.itemKey((String)fields.get(SearchKeys.VideoContentFields.FIELD_ITEM_KEY))
			.thumb(new ImageDto((String)fields.get(SearchKeys.CommonFields.FIELD_THUMBNAIL)))
			.title((String)fields.get(SearchKeys.CommonFields.FIELD_TITLE))
			.description((String)fields.get(SearchKeys.CommonFields.FIELD_DESCRIPTION))
			.build();
		} else if(dataType.equals(Message.class.getSimpleName())) {
			data = new MessageDto.Builder();
				// TODO: finish this
			throw new IllegalArgumentException("Unknown data type: " + dataType);
		}
		
		DocumentDto documentDto = new DocumentDto.Builder()
		.rank(1)
		.dataType(data.getClass().getSimpleName())
		.data(data)
		.build();
		
		
		return documentDto;
	}
	
	
	public static ContactDto assemble(Contact contact) {
		ContactDto.Builder contactDtoBuilder = new ContactDto.Builder()
			.contactId(contact.getContactId())
			.displayName(contact.getDisplayName())
			.firstName(contact.getFirstName())
			.lastName(contact.getLastName()).email(contact.getEmail())
			.profileUrl(contact.getProfileUrl())
			.etag(UUID.randomUUID().toString());
		
		ExternalIdentity identity = contact.getSocialIdentity();
		if(identity != null)
			contactDtoBuilder.identity(
					new IdentityDto.Builder()
						.identifier(identity.getIdentifier())
						.identityProviderId(identity.getSocialProvider().getValue())
						.build());
		// Image is optional
		if (contact.getImage() != null)
			contactDtoBuilder.imageUrl(contact.getImage().getUrl());

		return contactDtoBuilder.build();
	}
	
	public static MessageDto assemble(Message message) {
		return new MessageDto.Builder()
			.subject(message.getTitle())
			.date(System.currentTimeMillis())
			.socialProviderId(SocialProvider.Google.getValue())
			.body(message.getBody())
			.sender(
					assemble(message.getSender())).build();
	}
	
}
