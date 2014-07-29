package com.ubiquity.sprocket.api;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.content.domain.VideoContent;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.media.domain.Image;
import com.ubiquity.media.domain.Video;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.domain.ActivityType;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.Message;
import com.ubiquity.social.domain.factories.ActivityFactory;
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

	// converter for any secondary parsing (for elements with type erasure, for example)
	private static JsonConverter jsonConverter;

	static {
		jsonConverter = JsonConverter.getInstance();
	}

	public static Activity assemble(ActivityDto activityDto) {
		// convert to domain entity with required fields
		Activity.Builder activityBuilder = ActivityFactory.createPublicActivityBuilderWithRequiredFields(ActivityType.valueOf(activityDto.getType().toUpperCase()), 
				ExternalNetwork.getNetworkById(activityDto.getExternalNetworkId()), 
				activityDto.getDate(), 
				activityDto.getExternalIdentifier());

		// set optional fields
		activityBuilder.title(activityDto.getTitle());
		activityBuilder.body(activityDto.getBody());
		activityBuilder.link(activityDto.getLink());

		// set video / photo urls if we have them
		VideoDto videoDto = activityDto.getVideo();
		if(videoDto != null)
			activityBuilder.video(new Video.Builder().url(videoDto.getUrl()).build());

		ImageDto imageDto = activityDto.getPhoto();
		if(imageDto != null)
			activityBuilder.image(new Image(imageDto.getUrl()));

		activityBuilder.postedBy(assemble(activityDto.getPostedBy()));

		return activityBuilder.build();

	}


	public static Contact assemble(ContactDto contactDto) {

		Contact.Builder contactBuilder = new Contact.Builder()
		.displayName(contactDto.getDisplayName())
		.firstName(contactDto.getFirstName())
		.lastName(contactDto.getLastName())
		.lastUpdated(System.currentTimeMillis())
		.profileUrl(contactDto.getProfileUrl());

		if(contactDto.getImageUrl() != null) {
			contactBuilder.image(new Image(contactDto.getImageUrl()));
		}

		contactBuilder.externalIdentity(
				new ExternalIdentity.Builder()
				.identifier(contactDto.getIdentity().getIdentifier())
				.externalNetwork(contactDto.getIdentity().getExternalNetworkId())
				.isActive(Boolean.TRUE)
				.lastUpdated(System.currentTimeMillis())
				.build()
				);

		return contactBuilder.build();
	}

	public static DocumentDto assemble(Document document) {

		Map<String, Object> fields = document.getFields();

		// determine data type from field;
		String dataType = document.getDataType();


		Object data = null;
		if(dataType.equals(VideoContent.class.getSimpleName())) {
			//if we have a reference to an entity we build from data

			data = document.getData();
			if(data != null) {
				VideoContent videoContent = (VideoContent)document.getData();
				data = assemble(videoContent);
			} else {

				data = new VideoDto.Builder()
				.externalNetworkId(ExternalNetwork.YouTube.ordinal())
				.itemKey((String)fields.get(SearchKeys.Fields.FIELD_ITEM_KEY))
				.thumb(new ImageDto((String)fields.get(SearchKeys.Fields.FIELD_THUMBNAIL)))
				.title((String)fields.get(SearchKeys.Fields.FIELD_TITLE))
				.description((String)fields.get(SearchKeys.Fields.FIELD_DESCRIPTION))
				.build();
			}
		} else if(dataType.equals(Message.class.getSimpleName())) {

			data = new MessageDto.Builder()
			.subject((String)fields.get(SearchKeys.Fields.FIELD_TITLE))
			.date(System.currentTimeMillis())
			.externalNetworkId((Integer)fields.get(SearchKeys.Fields.FIELD_EXTERNAL_NETWORK_ID))
			.body((String)fields.get(SearchKeys.Fields.FIELD_BODY))
			.sender(assemble(fields))
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
				.externalNetworkId((Integer)fields.get(SearchKeys.Fields.FIELD_EXTERNAL_NETWORK_ID))
				.body((String)fields.get(SearchKeys.Fields.FIELD_BODY))
				.postedBy(assemble(fields))
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

	private static ContactDto assemble(Map<String, Object> fields) {
		return new ContactDto.Builder()
		.displayName((String)fields.get(SearchKeys.Fields.FIELD_CONTACT_DISPLAY_NAME))
		.identity(new IdentityDto.Builder()
		.externalNetworkId((Integer)fields.get(SearchKeys.Fields.FIELD_EXTERNAL_NETWORK_ID))
		.identifier((String)fields.get(SearchKeys.Fields.FIELD_CONTACT_IDENTIFIER))
		.build())
		.imageUrl(SearchKeys.Fields.FIELD_CONTACT_THUMBNAIL).build();

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
		.externalNetworkId(videoContent.getExternalNetwork().ordinal())
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
				topLevelMessageDto.setLastMessageDate(messageDto.getDate());
				topLevelMessageDto.getConversation().addLast(messageDto);
			} else {
				// we have a top level message
				topLevelMessageDto = new MessageDto.Builder()
							.externalNetworkId(message.getExternalNetwork().ordinal())
							.lastMessageDate(messageDto.getDate())
							.build();
				topLevelMessageDto.getConversation().add(messageDto);
				lastConversationIdentifier = conversationIdentifier;
				for (Contact contact : message.getReceivers()) {
					topLevelMessageDto.getReceivers().add(assemble(contact));
				}
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
		//.externalNetworkId(message.getExternalNetwork().ordinal())
		.body(message.getBody())
		.sender(assemble(message.getSender())).build();
	}

	public static ActivityDto assemble(Activity activity) {
		ActivityDto.Builder activityDtoBuilder = new ActivityDto.Builder();

		activityDtoBuilder.body(activity.getBody())
		.type(activity.getActivityType().toString().toLowerCase())
		.date(activity.getCreationDate())
		.externalNetworkId(activity.getExternalNetwork().ordinal())
		.title(activity.getTitle())
		.link(activity.getLink())
		.externalIdentifier(activity.getExternalIdentifier())
		.postedBy(DtoAssembler.assemble(activity.getPostedBy()));

		if(activity.getImage() != null)
			activityDtoBuilder.photo(new ImageDto(activity.getImage().getUrl()));
		if(activity.getVideo() != null)
			activityDtoBuilder.video(new VideoDto.Builder().url(activity.getVideo().getUrl()).itemKey(activity.getVideo().getItemKey()).build());

		return activityDtoBuilder.build();


	}



	@SuppressWarnings("unchecked")
	/***
	 * 
	 * Assembles a document after validating input
	 * 
	 * @param documentDto document dto
	 * @param validationGroup validator for the "data" property of the document dto
	 * @return
	 */
	public static Document assemble(DocumentDto documentDto, Class<?> validationGroup) {
		Document document;
		String dataType = documentDto.getDataType();
		Map<String, Object> map = (Map<String, Object>)documentDto.getData();

		if(dataType.equals(Activity.class.getSimpleName())) {			
			ActivityDto activityDto = jsonConverter.convertFromObject(map, ActivityDto.class, validationGroup);
			// now convert to entity
			Activity activity = assemble(activityDto);
			document = new Document(dataType, activity, documentDto.getRank());

		} else if(dataType.equals(VideoContent.class.getSimpleName())) {
			VideoDto videoDto = jsonConverter.convertFromObject(map, VideoDto.class);
			VideoContent videoContent = assemble(videoDto);
			document = new Document(dataType, videoContent, documentDto.getRank());
		} else if(dataType.equals(Message.class.getSimpleName())) {
			MessageDto messageDto = jsonConverter.convertFromObject(map, MessageDto.class);
			Message message = assemble(messageDto);
			document = new Document(dataType, message, documentDto.getRank());

		} else {
			throw new IllegalArgumentException("Uknown data type for document: " + dataType);
		}
		return document;
	}


	private static Message assemble(MessageDto messageDto) {
		// TODO Auto-generated method stub
		return null;
	}


	private static VideoContent assemble(VideoDto videoDto) {
		Video video = new Video.Builder().itemKey(videoDto.getItemKey()).url(videoDto.getUrl()).build();
		VideoContent content = new VideoContent.Builder()
		.video(video)
		.title(videoDto.getTitle())
		.category(videoDto.getCategory())
		.description(videoDto.getDescription())
		.externalNetwork(ExternalNetwork.getNetworkById(videoDto.getExternalNetworkId()))
		.thumb(new Image(videoDto.getThumb().getUrl()))
		.lastUpdated(System.currentTimeMillis())
		.build();
		return content;

	}

}
