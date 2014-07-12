package com.ubiquity.sprocket.api;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.ubiquity.content.domain.VideoContent;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.Message;
import com.ubiquity.sprocket.api.dto.model.ActivityDto;
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
		
		// determine data type from field;
		String dataType = document.getDataType();
		
		
		Object data = null;
		if(dataType.equals(VideoContent.class.getSimpleName())) {
			data = new VideoDto.Builder()
			.externalNetworkId(ExternalNetwork.YouTube.ordinal())
			.itemKey((String)fields.get(SearchKeys.Fields.FIELD_ITEM_KEY))
			.thumb(new ImageDto((String)fields.get(SearchKeys.Fields.FIELD_THUMBNAIL)))
			.title((String)fields.get(SearchKeys.Fields.FIELD_TITLE))
			.description((String)fields.get(SearchKeys.Fields.FIELD_DESCRIPTION))
			.build();
		} else if(dataType.equals(Message.class.getSimpleName())) {
			
			data = new MessageDto.Builder()
			.subject((String)fields.get(SearchKeys.Fields.FIELD_TITLE))
			.date(System.currentTimeMillis())
			.externalNetworkId((Integer)fields.get(SearchKeys.Fields.FIELD_SOCIAL_NETWORK_ID))
			.body((String)fields.get(SearchKeys.Fields.FIELD_BODY))
			.sender(new ContactDto.Builder().contactId(1l).displayName((String)fields.get(SearchKeys.Fields.FIELD_SENDER)).imageUrl(SearchKeys.Fields.FIELD_THUMBNAIL).build())
			.build();

		} else if(dataType.equals(Activity.class.getSimpleName())) {
			// if we have an object, build from the entity data
			if(document.getData() != null) {
				Activity activity = (Activity)document.getData();
				data = assemble(activity);
				
			
			} else {
				data = new ActivityDto.Builder()
				.title((String)fields.get(SearchKeys.Fields.FIELD_TITLE))
				.date(System.currentTimeMillis())
				.externalNetworkId((Integer)fields.get(SearchKeys.Fields.FIELD_SOCIAL_NETWORK_ID))
				.body((String)fields.get(SearchKeys.Fields.FIELD_BODY))
				.postedBy(new ContactDto.Builder().contactId(1l).displayName((String)fields.get(SearchKeys.Fields.FIELD_POSTED_BY)).imageUrl(SearchKeys.Fields.FIELD_THUMBNAIL).build())
				.build();
			}
			
		} else {
			throw new IllegalArgumentException("Unknown data type: " + dataType);
		}
		
		DocumentDto documentDto = new DocumentDto.Builder()
		.rank(document.getRank())
		.dataType(dataType)
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
						.externalNetworkId(identity.getExternalNetwork())
						.build());
		// Image is optional
		if (contact.getImage() != null)
			contactDtoBuilder.imageUrl(contact.getImage().getUrl());

		return contactDtoBuilder.build();
	}
	
	public static VideoDto assemble(VideoContent videoContent) {
		return new VideoDto.Builder()
			.externalNetworkId(ExternalNetwork.YouTube.ordinal())
			.itemKey(videoContent.getVideo().getItemKey())
			.thumb(new ImageDto(videoContent.getThumb().getUrl()))
			.title(videoContent.getTitle())
			.description(videoContent.getDescription())
			.build();
		
	}
	
	
	/***
	 * Constructs a list of messages and conversations (if the message has a conversation identifier). This method assumes the messages
	 * are sorted by latest first
	 * 
	 * @param messageDtoList
	 * @return
	 */
	public static List<MessageDto> assemble(List<Message> messages) {
		List<MessageDto> messageDtoList = new LinkedList<MessageDto>();
		
		// check to see if we have a new conversation
		MessageDto topLevelMessageDto = null;
		String lastConversationIdentifier = null; // we track these two values because the identifier is not stored in the dto
		for(Message message : messages) {
			
			MessageDto messageDto = assemble(message);
			String conversationIdentifier = message.getConversationIdentifier();
			if(topLevelMessageDto != null && conversationIdentifier != null && lastConversationIdentifier.equals(conversationIdentifier)) {
				// we are adding to the conversation
				topLevelMessageDto.getConversation().addLast(messageDto);
			} else {
				// we have a top level message
				topLevelMessageDto = messageDto;
				lastConversationIdentifier = conversationIdentifier;
				
				// now add to the main list for a "roll up"
				messageDtoList.add(topLevelMessageDto);
			}
		}
				
		return messageDtoList;
	}
	
	
	public static MessageDto assemble(Message message) {
		return new MessageDto.Builder()
			.subject(message.getTitle())
			.date(message.getSentDate())
			.externalNetworkId(message.getExternalNetwork().ordinal())
			.body(message.getBody())
			.sender(
					assemble(message.getSender())).build();
	}

	public static ActivityDto assemble(Activity activity) {
		 ActivityDto.Builder activityDtoBuilder = new ActivityDto.Builder();
		
		 activityDtoBuilder.body(activity.getBody())
			.type(activity.getActivityType().toString().toLowerCase())
			.date(System.currentTimeMillis())
			.externalNetworkId(activity.getExternalNetwork().ordinal())
			.title(activity.getTitle())
			.link(activity.getLink())
			.postedBy(DtoAssembler.assemble(activity.getPostedBy()));
		 	
		 if(activity.getImage() != null)
			 activityDtoBuilder.photo(new ImageDto(activity.getImage().getUrl()));
		 if(activity.getVideo() != null)
			 activityDtoBuilder.video(new VideoDto.Builder().url(activity.getVideo().getUrl()).itemKey(activity.getVideo().getItemKey()).build());
		 
		 return activityDtoBuilder.build();
		
			
	}
	
}
