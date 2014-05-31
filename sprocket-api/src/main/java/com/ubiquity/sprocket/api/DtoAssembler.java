package com.ubiquity.sprocket.api;

import java.util.Map;
import java.util.UUID;

import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.Message;
import com.ubiquity.social.domain.SocialNetwork;
import com.ubiquity.sprocket.api.dto.model.ActivityDto;
import com.ubiquity.sprocket.api.dto.model.ContactDto;
import com.ubiquity.sprocket.api.dto.model.DocumentDto;
import com.ubiquity.sprocket.api.dto.model.IdentityDto;
import com.ubiquity.sprocket.api.dto.model.ImageDto;
import com.ubiquity.sprocket.api.dto.model.MessageDto;
import com.ubiquity.sprocket.api.dto.model.VideoDto;
import com.ubiquity.sprocket.domain.ContentNetwork;
import com.ubiquity.sprocket.domain.Document;
import com.ubiquity.sprocket.domain.VideoContent;
import com.ubiquity.sprocket.search.SearchKeys;

public class DtoAssembler {

	public static DocumentDto assemble(Document document) {
		
		Map<String, Object> fields = document.getFields();
		String dataType = (String)fields.get(SearchKeys.CommonFields.FIELD_DATA_TYPE);
		
		Object data = null;
		String apiModelDataType = null; // TODO: would use the class name but the API is set already and so close to demo...
		if(dataType.equals(VideoContent.class.getSimpleName())) {
			data = new VideoDto.Builder()
			.contentNetworkId(ContentNetwork.YouTube.ordinal())
			.itemKey((String)fields.get(SearchKeys.VideoContentFields.FIELD_ITEM_KEY))
			.thumb(new ImageDto((String)fields.get(SearchKeys.CommonFields.FIELD_THUMBNAIL)))
			.title((String)fields.get(SearchKeys.CommonFields.FIELD_TITLE))
			.description((String)fields.get(SearchKeys.CommonFields.FIELD_DESCRIPTION))
			.build();
			apiModelDataType = "Video";
		} else if(dataType.equals(Message.class.getSimpleName())) {
			
			data = new MessageDto.Builder()
			.subject((String)fields.get(SearchKeys.CommonFields.FIELD_TITLE))
			.date(System.currentTimeMillis())
			.socialProviderId((Integer)fields.get(SearchKeys.CommonFields.FIELD_SOCIAL_NETWORK_ID))
			.body((String)fields.get(SearchKeys.CommonFields.FIELD_BODY))
			.sender(new ContactDto.Builder().contactId(1l).displayName((String)fields.get(SearchKeys.MessageContentFields.FIELD_SENDER)).imageUrl(SearchKeys.CommonFields.FIELD_THUMBNAIL).build())
			.build();
			apiModelDataType = "Message";

		} else if(dataType.equals(Activity.class.getSimpleName())) {
			data = new ActivityDto.Builder()
			.title((String)fields.get(SearchKeys.CommonFields.FIELD_TITLE))
			.date(System.currentTimeMillis())
			.socialProviderId((Integer)fields.get(SearchKeys.CommonFields.FIELD_SOCIAL_NETWORK_ID))
			.body((String)fields.get(SearchKeys.CommonFields.FIELD_BODY))
			.postedBy(new ContactDto.Builder().contactId(1l).displayName((String)fields.get(SearchKeys.ActivityContentFields.FIELD_POSTED_BY)).imageUrl(SearchKeys.CommonFields.FIELD_THUMBNAIL).build())
			.build();
			apiModelDataType = "Activity";
		} else {
			throw new IllegalArgumentException("Unknown data type: " + dataType);
		}
		
		DocumentDto documentDto = new DocumentDto.Builder()
		.rank(1)
		.dataType(apiModelDataType)
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
		
		ExternalIdentity identity = contact.getExternalIdentity();
		if(identity != null)
			contactDtoBuilder.identity(
					new IdentityDto.Builder()
						.identifier(identity.getIdentifier())
						.identityProviderId(identity.getIdentityProvider())
						.build());
		// Image is optional
		if (contact.getImage() != null)
			contactDtoBuilder.imageUrl(contact.getImage().getUrl());

		return contactDtoBuilder.build();
	}
	
	public static VideoDto assemble(VideoContent videoContent) {
		return new VideoDto.Builder()
			.contentNetworkId(ContentNetwork.YouTube.ordinal())
			.itemKey(videoContent.getVideo().getItemKey())
			.thumb(new ImageDto(videoContent.getThumb().getUrl()))
			.title(videoContent.getTitle())
			.description(videoContent.getDescription())
			.build();
		
	}
	
	public static MessageDto assemble(Message message) {
		return new MessageDto.Builder()
			.subject(message.getTitle())
			.date(System.currentTimeMillis())
			.socialProviderId(SocialNetwork.Google.getValue())
			.body(message.getBody())
			.sender(
					assemble(message.getSender())).build();
	}
	
}
