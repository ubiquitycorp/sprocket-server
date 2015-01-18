package com.ubiquity.sprocket.api;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;


import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.integration.domain.Activity;
import com.ubiquity.integration.domain.ActivityType;
import com.ubiquity.integration.domain.Address;
import com.ubiquity.integration.domain.AdminInterest;
import com.ubiquity.integration.domain.AdminInterestType;
import com.ubiquity.integration.domain.Category;
import com.ubiquity.integration.domain.Comment;
import com.ubiquity.integration.domain.Contact;
import com.ubiquity.integration.domain.ExternalInterest;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.Interest;
import com.ubiquity.integration.domain.Message;
import com.ubiquity.integration.domain.PostComment;
import com.ubiquity.integration.domain.PostVote;
import com.ubiquity.integration.domain.Rating;
import com.ubiquity.integration.domain.UnmappedInterest;
import com.ubiquity.integration.domain.VideoContent;
import com.ubiquity.location.domain.Geobox;
import com.ubiquity.location.domain.Location;
import com.ubiquity.location.domain.Place;
import com.ubiquity.media.domain.Image;
import com.ubiquity.media.domain.Video;
import com.ubiquity.social.domain.factories.ActivityFactory;
import com.ubiquity.sprocket.api.dto.containers.ConfigurationRulesDto;
import com.ubiquity.sprocket.api.dto.model.ActivityDto;
import com.ubiquity.sprocket.api.dto.model.AddressDto;
import com.ubiquity.sprocket.api.dto.model.AudioDto;
import com.ubiquity.sprocket.api.dto.model.CommentDto;
import com.ubiquity.sprocket.api.dto.model.ContactDto;
import com.ubiquity.sprocket.api.dto.model.DocumentDto;
import com.ubiquity.sprocket.api.dto.model.AdminInterestDto;
import com.ubiquity.sprocket.api.dto.model.ExternalNetworkConfigurationDto;
import com.ubiquity.sprocket.api.dto.model.GeoboxDto;
import com.ubiquity.sprocket.api.dto.model.IdentityDto;
import com.ubiquity.sprocket.api.dto.model.ImageDto;
import com.ubiquity.sprocket.api.dto.model.InterestDto;
import com.ubiquity.sprocket.api.dto.model.LocationDto;
import com.ubiquity.sprocket.api.dto.model.MessageDto;
import com.ubiquity.sprocket.api.dto.model.PlaceDto;
import com.ubiquity.sprocket.api.dto.model.PostCommentDto;
import com.ubiquity.sprocket.api.dto.model.PostVoteDto;
import com.ubiquity.sprocket.api.dto.model.RatingDto;
import com.ubiquity.sprocket.api.dto.model.VideoDto;
import com.ubiquity.sprocket.domain.Configuration;
import com.ubiquity.sprocket.domain.Document;
import com.ubiquity.sprocket.search.SearchKeys;

public class DtoAssembler {

	// converter for any secondary parsing (for elements with type erasure, for
	// example)
	private static JsonConverter jsonConverter;

	static {
		jsonConverter = JsonConverter.getInstance();
	}

	public static Activity assemble(ActivityDto activityDto) {
		// convert to domain entity with required fields
		Activity.Builder activityBuilder = ActivityFactory
				.createPublicActivityBuilderWithRequiredFields(ActivityType
						.valueOf(activityDto.getType().toUpperCase()),
						ExternalNetwork.getNetworkById(activityDto
								.getExternalNetworkId()),
						activityDto.getDate(), activityDto
								.getExternalIdentifier());

		// set optional fields
		activityBuilder.title(activityDto.getTitle());
		activityBuilder.body(activityDto.getBody());
		activityBuilder.link(activityDto.getLink());
		activityBuilder.ownerVote(activityDto.getOwnerVote());
		activityBuilder.commentsNum(activityDto.getCommentsNum());
		// set video / photo urls if we have them
		VideoDto videoDto = activityDto.getVideo();
		if (videoDto != null)
			activityBuilder.video(new Video.Builder().url(videoDto.getUrl())
					.embedCode(videoDto.getEmbedCode())
					.itemKey(videoDto.getItemKey()).build());

		ImageDto imageDto = activityDto.getPhoto();
		if (imageDto != null)
			activityBuilder.image(new Image(imageDto.getUrl()));

		if (activityDto.getPostedBy() != null)
			activityBuilder.postedBy(assemble(activityDto.getPostedBy()));

		// if (activityDto.getComments() != null) {
		// for (CommentDto comment : activityDto.getComments())
		// activityBuilder.addComment(assemble(comment));
		// }
		// if (activityDto.getInterests() != null) {
		// for (InterestDto interest : activityDto.getInterests())
		// activityBuilder.addInterest(assemble(interest));
		// }
		activityBuilder.rating(assemble(activityDto.getRating()));
		return activityBuilder.build();

	}

	// public static Comment assemble(CommentDto commentDto) {
	// Comment.Builder commentBuilder = new Comment.Builder();
	// commentBuilder.body(commentDto.getBody())
	// .commentId(commentDto.getCommentId())
	// .creationDate(commentDto.getCreationDate())
	// .externalIdentifier(commentDto.getExternalIdentifier())
	// .postedBy(assemble(commentDto.getPostedBy()))
	// .rating(assemble(commentDto.getRating()))
	// .ownerVote(commentDto.getOwnerVote());
	// Comment comment = commentBuilder.build();
	// for (CommentDto reply : commentDto.getReplies())
	// comment.addReply(assemble(reply));
	// return commentBuilder.build();
	// }
	// public static Captcha assemble(CaptchaDto captchaDto) {
	// return new Captcha.Builder().captchaAnswer(captchaDto.getCaptchaAnswer())
	// .identifier(captchaDto.getIdentifier()).build();
	// }
	public static PostComment assemble(PostCommentDto postCommentDto) {
		return new PostComment.Builder().body(postCommentDto.getBody())
				.parentId(postCommentDto.getParentId()).build();
	}

	public static PostVote assemble(PostVoteDto postVoteDto) {
		return new PostVote.Builder().direction(postVoteDto.getDirection())
				.stars(postVoteDto.getStars())
				.parentId(postVoteDto.getParentId()).build();
	}

	public static Contact assemble(ContactDto contactDto) {

		Contact.Builder contactBuilder = new Contact.Builder()
				.displayName(contactDto.getDisplayName())
				.firstName(contactDto.getFirstName())
				.lastName(contactDto.getLastName())
				.lastUpdated(System.currentTimeMillis())
				.profileUrl(contactDto.getProfileUrl())
				.contactId(contactDto.getContactId())
				.isDeleted(false);

		if (contactDto.getImageUrl() != null) {
			contactBuilder.image(new Image(contactDto.getImageUrl()));
		}

		contactBuilder.externalIdentity(new ExternalIdentity.Builder()
				.identifier(contactDto.getIdentity().getIdentifier())
				.externalNetwork(
						contactDto.getIdentity().getExternalNetworkId())
				.isActive(Boolean.TRUE).lastUpdated(System.currentTimeMillis())
				.build());

		return contactBuilder.build();
	}

	public static List<InterestDto> assemble(Collection<Interest> interests) {

		List<InterestDto> interestsDtoList = new LinkedList<InterestDto>();

		for (Interest interest : interests) {
			// add
			InterestDto parentDto = new InterestDto(interest.getInterestId(),
					interest.getName());
			interestsDtoList.add(parentDto);

			Set<Interest> children = interest.getChildren();
			Stack<Interest> stack = new Stack<Interest>();
			stack.addAll(children);
			while (!stack.isEmpty()) {
				Interest child = stack.pop();
				InterestDto childDto = new InterestDto(child.getInterestId(),
						child.getName());
				parentDto.getChildren().add(childDto);
			}
		}

		return interestsDtoList;

	}

	public static DocumentDto assemble(Document document) {

		Map<String, Object> fields = document.getFields();

		// determine data type from field;
		String dataType = document.getDataType();

		Object data = null;
		if (dataType.equals(VideoContent.class.getSimpleName())) {
			// if we have a reference to an entity we build from data
			data = document.getData();
			if (data != null) {
				VideoContent videoContent = (VideoContent) document.getData();
				data = assemble(videoContent);
			} else {
				Long ownerId = (Long) fields
						.get(SearchKeys.Fields.FIELD_OWNER_ID);
				ownerId = ownerId == 0 ? null : ownerId;
				data = new VideoDto.Builder()
						.externalNetworkId(
								(Integer) fields
										.get(SearchKeys.Fields.FIELD_EXTERNAL_NETWORK_ID))
						.itemKey(
								(String) fields
										.get(SearchKeys.Fields.FIELD_ITEM_KEY))
						.thumb(new ImageDto((String) fields
								.get(SearchKeys.Fields.FIELD_THUMBNAIL)))
						.title((String) fields
								.get(SearchKeys.Fields.FIELD_TITLE))
						.description(
								(String) fields
										.get(SearchKeys.Fields.FIELD_DESCRIPTION))
						.ownerId(ownerId).build();
			}
		} else if (dataType.equals(Message.class.getSimpleName())) {
			Long ownerId = (Long) fields.get(SearchKeys.Fields.FIELD_OWNER_ID);
			ownerId = ownerId == 0 ? null : ownerId;
			MessageDto message = new MessageDto.Builder()
					.subject((String) fields.get(SearchKeys.Fields.FIELD_TITLE))
					.date(System.currentTimeMillis())
					.externalNetworkId(
							(Integer) fields
									.get(SearchKeys.Fields.FIELD_EXTERNAL_NETWORK_ID))
					.body((String) fields.get(SearchKeys.Fields.FIELD_BODY))
					.ownerId(
							(Long) fields.get(SearchKeys.Fields.FIELD_OWNER_ID))
					.sender(assembleContactDtoFromDocumentFields(fields))
					.build();
			MessageDto topMessage = new MessageDto.Builder()
					.externalNetworkId(message.getExternalNetworkId())
					.lastMessageDate(message.getDate()).ownerId(ownerId)
					.build();
			topMessage.getConversation().add(message);
			data = topMessage;
		} else if (dataType.equals(Activity.class.getSimpleName())) {
			// if we have an object, build from the entity data
			if (document.getData() != null) {
				Activity activity = (Activity) document.getData();
				data = assemble(activity);

			} else {
				Long ownerId = (Long) fields
						.get(SearchKeys.Fields.FIELD_OWNER_ID);
				ownerId = ownerId == 0 ? null : ownerId;
				ActivityDto.Builder builder = new ActivityDto.Builder()
						.title((String) fields
								.get(SearchKeys.Fields.FIELD_TITLE))
						.body((String) fields.get(SearchKeys.Fields.FIELD_BODY))
						.externalIdentifier(
								(String) fields
										.get(SearchKeys.Fields.FIELD_EXTERNAL_IDENTIFIER))
						.externalNetworkId(
								(Integer) fields
										.get(SearchKeys.Fields.FIELD_EXTERNAL_NETWORK_ID))
						.date((Long) fields.get(SearchKeys.Fields.FIELD_DATE))
						.ownerId(ownerId)
						.commentsNum(
								(Integer) fields
										.get(SearchKeys.Fields.FIELD_COMMENTNUM));
				if (fields.get(SearchKeys.Fields.FIELD_RATING_NUM_RATING) != null)
					builder.rating(new RatingDto.Builder()
							.numRatings(
									(Integer) fields
											.get(SearchKeys.Fields.FIELD_RATING_NUM_RATING))
							.build());

				// add in content based on type
				String activityType = (String) fields
						.get(SearchKeys.Fields.FIELD_ACTIVITY_TYPE);
				builder.type(activityType.toLowerCase());

				if (activityType.equals(ActivityType.LINK.toString())) {
					builder.link((String) fields
							.get(SearchKeys.Fields.FIELD_URL));
				} else if (activityType.equals(ActivityType.PHOTO.toString())) {
					builder.photo(new ImageDto((String) fields
							.get(SearchKeys.Fields.FIELD_URL)));
				} else if (activityType.equals(ActivityType.VIDEO.toString())) {
					builder.video(new VideoDto.Builder()
							.url((String) fields
									.get(SearchKeys.Fields.FIELD_URL))
							.itemKey(
									(String) fields
											.get(SearchKeys.Fields.FIELD_ITEM_KEY))
							.embedCode(
									(String) fields
											.get(SearchKeys.Fields.FIELD_EMBED_CODE))
							.build());
					builder.photo(new ImageDto((String) fields
							.get(SearchKeys.Fields.FIELD_THUMBNAIL)));
				} else if (activityType.equals(ActivityType.EMBEDEDHTML
						.toString())) {
					builder.link((String) fields
							.get(SearchKeys.Fields.FIELD_URL));
				}

				// now do the contact
				builder.postedBy(assembleContactDtoFromDocumentFields(fields));

				data = builder.build();
			}

		} else if (dataType.equals(Place.class.getSimpleName())) {
			Place place = (Place) document.getData();
			PlaceDto placeDto = assemble(place);
			data = placeDto;
		} else {
			throw new IllegalArgumentException("Unknown data type: " + dataType);
		}

		DocumentDto documentDto = new DocumentDto.Builder()
				.rank((Integer) fields.get(SearchKeys.Fields.FIELD_CLICKS))
				.dataType(dataType).data(data).build();

		return documentDto;
	}

	private static ContactDto assembleContactDtoFromDocumentFields(
			Map<String, Object> fields) {
		return new ContactDto.Builder()
				.displayName(
						(String) fields
								.get(SearchKeys.Fields.FIELD_CONTACT_DISPLAY_NAME))
				.identity(
						new IdentityDto.Builder()
								.externalNetworkId(
										(Integer) fields
												.get(SearchKeys.Fields.FIELD_EXTERNAL_NETWORK_ID))
								.identifier(
										(String) fields
												.get(SearchKeys.Fields.FIELD_CONTACT_IDENTIFIER))
								.build())
				.imageUrl(
						(String) fields
								.get(SearchKeys.Fields.FIELD_CONTACT_THUMBNAIL))
				.build();

	}

	public static IdentityDto assemble(ExternalIdentity identity) {
		return new IdentityDto.Builder().identifier(identity.getIdentifier())
				.externalNetworkId(identity.getExternalNetwork()).build();
	}

	// public static CaptchaDto assemble(Captcha captcha) {
	// return new CaptchaDto.Builder().imageType(captcha.getImageType())
	// .identifier(captcha.getIdentifier()).build();
	// }

	public static ContactDto assemble(Contact contact) {
		ContactDto.Builder contactDtoBuilder = new ContactDto.Builder()
				.contactId(contact.getContactId())
				.displayName(contact.getDisplayName())
				.firstName(contact.getFirstName())
				.lastName(contact.getLastName()).email(contact.getEmail())
				.profileUrl(contact.getProfileUrl())
				.etag(UUID.randomUUID().toString());

		ExternalIdentity identity = contact.getExternalIdentity();
		if (identity != null)
			contactDtoBuilder.identity(new IdentityDto.Builder()
					.identifier(identity.getIdentifier())
					.externalNetworkId(identity.getExternalNetwork()).build());
		// Image is optional
		if (contact.getImage() != null)
			contactDtoBuilder.imageUrl(contact.getImage().getUrl());

		return contactDtoBuilder.build();
	}

	public static InterestDto assemble(Interest interest) {

		InterestDto interestDto = new InterestDto(interest.getInterestId(),
				interest.getName());
		return interestDto;
	}

	public static AdminInterestDto assemble(ExternalInterest externalInterest) {
		return new AdminInterestDto.Builder()
				.id(externalInterest.getExternalInterestId())
				.name(externalInterest.getName())
				.interestId(externalInterest.getInterest().getInterestId())
				.externalNetworkId(externalInterest.getExternalNetwork())
				.build();
	}

	public static AdminInterestDto assemble(UnmappedInterest unmappedInterest) {
		return new AdminInterestDto.Builder()
				.id(unmappedInterest.getUnmappedId())
				.name(unmappedInterest.getName())
				.externalNetworkId(unmappedInterest.getExternalNetwork())
				.build();

	}

	public static VideoDto assemble(VideoContent videoContent) {
		VideoDto.Builder videoBuilder = new VideoDto.Builder()
				.externalNetworkId(videoContent.getExternalNetwork().ordinal());

		// add video category if exists
		if (videoContent.getCategory() != null)
			videoBuilder.category(videoContent.getCategory().getCategoryName());

		if (videoContent.getVideo() != null)
			videoBuilder.itemKey(videoContent.getVideo().getItemKey());

		if (videoContent.getThumb() != null)
			videoBuilder.thumb(new ImageDto(videoContent.getThumb().getUrl()));

		videoBuilder.title(videoContent.getTitle()).description(
				videoContent.getDescription());

		return videoBuilder.build();
	}

	/***
	 * Constructs a list of messages and conversations (if the message has a
	 * conversation identifier). This method assumes the messages are sorted by
	 * latest first
	 * 
	 * @param messageDtoList
	 * @return
	 */
	public static List<MessageDto> assemble(List<Message> messages) {

		List<MessageDto> messageDtoList = new LinkedList<MessageDto>();

		// check to see if we have a new conversation
		MessageDto topLevelMessageDto = null;
		String lastConversationIdentifier = null; // we track these two values
													// because the identifier is
													// not stored in the dto
		for (Message message : messages) {

			MessageDto messageDto = assemble(message);
			String conversationIdentifier = message.getConversation()
					.getConversationIdentifier();
			if (topLevelMessageDto != null
					&& conversationIdentifier != null
					&& lastConversationIdentifier
							.equals(conversationIdentifier)) {
				// we are adding to the conversation
				topLevelMessageDto.setLastMessageDate(messageDto.getDate());
				topLevelMessageDto.getConversation().addLast(messageDto);
			} else {
				// we have a top level message
				topLevelMessageDto = new MessageDto.Builder()
						.externalNetworkId(
								message.getExternalNetwork().ordinal())
						.lastMessageDate(messageDto.getDate()).build();
				topLevelMessageDto.getConversation().add(messageDto);
				lastConversationIdentifier = conversationIdentifier;
				Set<Contact> receivers = message.getConversation()
						.getReceivers();
				if (receivers != null) {
					for (Contact contact : receivers) {
						topLevelMessageDto.getReceivers()
								.add(assemble(contact));
					}
				}
				// now add to the main list for a "roll up"
				messageDtoList.add(topLevelMessageDto);
			}
		}

		return messageDtoList;
	}

	public static MessageDto assemble(Message message) {
		return new MessageDto.Builder().subject(message.getTitle())
				.date(message.getSentDate())
				// .externalNetworkId(message.getExternalNetwork().ordinal())
				.body(message.getBody()).sender(assemble(message.getSender()))
				.build();
	}

	public static ActivityDto assemble(Activity activity) {
		ActivityDto.Builder activityDtoBuilder = new ActivityDto.Builder();

		activityDtoBuilder.body(activity.getBody())
				.type(activity.getActivityType().toString().toLowerCase())
				.date(activity.getCreationDate())
				.externalNetworkId(activity.getExternalNetwork().ordinal())
				.title(activity.getTitle()).link(activity.getLink())
				.externalIdentifier(activity.getExternalIdentifier())
				.ownerVote(activity.getOwnerVote())
				.commentsNum(activity.getCommentsNum());

		if (activity.getPostedBy() != null)
			activityDtoBuilder.postedBy(DtoAssembler.assemble(activity
					.getPostedBy()));

		if (activity.getCategory() != null)
			activityDtoBuilder.category(activity.getCategory()
					.getCategoryName());
		if (activity.getImage() != null)
			activityDtoBuilder
					.photo(new ImageDto(activity.getImage().getUrl()));
		if (activity.getVideo() != null) {
			VideoDto.Builder videoDtoBuilder = new VideoDto.Builder();
			videoDtoBuilder.itemKey(activity.getVideo().getItemKey());
			if (activity.getVideo().getEmbedCode() != null)
				videoDtoBuilder.embedCode(activity.getVideo().getEmbedCode());
			else
				videoDtoBuilder.url(activity.getVideo().getUrl());

			activityDtoBuilder.video(videoDtoBuilder.build());
		}

		if (activity.getAudio() != null) {
			AudioDto.Builder audioDtoBuilder = new AudioDto.Builder();
			if (activity.getAudio().getEmbedCode() != null)
				audioDtoBuilder.embedCode(activity.getAudio().getEmbedCode());
			else
				audioDtoBuilder.url(activity.getAudio().getUrl());

			activityDtoBuilder.audio(audioDtoBuilder.build());
		}
		if (activity.getComments() != null) {
			for (Comment comment : activity.getComments())
				activityDtoBuilder.addComment(assemble(comment));
		}
		if (activity.getInterests() != null) {
			for (Interest interest : activity.getInterests())
				activityDtoBuilder.addInterest(assemble(interest));

			if (activity.getInterests().size() == 0) {
				if (activity.getExternalNetwork()
						.equals(ExternalNetwork.Tumblr)) {
					activityDtoBuilder.addInterest(new InterestDto(1L,
							"tumblr interest 1"));
					activityDtoBuilder.addInterest(new InterestDto(2L,
							"tumblr interest 2"));
				}
			}
		}
		activityDtoBuilder.rating(assemble(activity.getRating()));
		return activityDtoBuilder.build();

	}

	public static CommentDto assemble(Comment comment) {
		CommentDto.Builder commentDtoBuilder = new CommentDto.Builder();
		commentDtoBuilder.body(comment.getBody())
				.commentId(comment.getCommentId())
				.creationDate(comment.getCreationDate())
				.externalIdentifier(comment.getExternalIdentifier())
				.postedBy(assemble(comment.getPostedBy()))
				.rating(assemble(comment.getRating()))
				.ownerVote(comment.getOwnerVote());
		for (Comment reply : comment.getReplies())
			commentDtoBuilder.addReply(assemble(reply));
		return commentDtoBuilder.build();
	}

	public static PlaceDto assembleCityOrNeighborhood(Place place) {
		if (place == null)
			return null;
		PlaceDto.Builder placeDtoBuilder = new PlaceDto.Builder();
		placeDtoBuilder.placeId(place.getPlaceId())
				.description(place.getDescription()).region(place.getRegion())
				.name(place.getName())
				.boundingBox(assemble(place.getBoundingBox()))
				.parent(assembleCityOrNeighborhood(place.getParent()));
		return placeDtoBuilder.build();
	}

	public static PlaceDto assemble(Place place) {
		if (place == null)
			return null;
		PlaceDto.Builder placeDtoBuilder = new PlaceDto.Builder();
		placeDtoBuilder.placeId(place.getPlaceId())
				.description(place.getDescription())
				.addressdto(assemble(place.getAddress()))
				.boundingBox(assemble(place.getBoundingBox()))
				.externalIdentitifer(place.getExternalIdentitifer())
				.region(place.getRegion()).name(place.getName())
				.network(place.getNetwork()).locator(place.getLocator())
				.ratingDto(assemble(place.getRating()))
				.parent(assembleCityOrNeighborhood(place.getParent()));
		if (place.getThumb() != null)
			placeDtoBuilder.thumb(new ImageDto(place.getThumb().getUrl()));
		return placeDtoBuilder.build();
	}

	public static RatingDto assemble(Rating rating) {
		if (rating == null)
			return null;
		RatingDto.Builder ratingDtoBuilder = new RatingDto.Builder();
		ratingDtoBuilder.max(rating.getMax()).min(rating.getMin())
				.numRatings(rating.getNumRatings()).rating(rating.getRating());
		return ratingDtoBuilder.build();
	}

	public static AddressDto assemble(Address address) {
		if (address == null)
			return null;
		AddressDto.Builder addressDtoBuilder = new AddressDto.Builder();
		addressDtoBuilder.city(address.getCity()).country(address.getCountry())
				.postalCode(address.getPostalCode())
				.stateOrRegion(address.getStateOrRegion())
				.streetName(address.getStreetName())
				.unitName(address.getUnitName())
				.displayPhone(address.getDisplayPhone());
		return addressDtoBuilder.build();
	}

	public static GeoboxDto assemble(Geobox geobox) {
		if (geobox == null)
			return null;
		GeoboxDto.Builder geoboxDtoBuilder = new GeoboxDto.Builder();
		geoboxDtoBuilder.center(assemble(geobox.getCenter()));
		return geoboxDtoBuilder.build();
	}

	public static LocationDto assemble(Location location) {
		if (location == null)
			return null;
		LocationDto.Builder locationDtoBuilder = new LocationDto.Builder();
		locationDtoBuilder.altitude(location.getAltitude())
				.latitude(location.getLatitude())
				.longitude(location.getLongitude());
		return locationDtoBuilder.build();
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
	public static Document assemble(DocumentDto documentDto,
			Class<?> validationGroup) {
		Document document;
		String dataType = documentDto.getDataType();
		Map<String, Object> map = (Map<String, Object>) documentDto.getData();

		if (dataType.equals(Activity.class.getSimpleName())) {
			ActivityDto activityDto = jsonConverter.convertFromObject(map,
					ActivityDto.class, validationGroup);
			// now convert to entity
			Activity activity = assemble(activityDto);
			document = new Document(dataType, activity, documentDto.getRank());

		} else if (dataType.equals(VideoContent.class.getSimpleName())) {
			VideoDto videoDto = jsonConverter.convertFromObject(map,
					VideoDto.class);
			VideoContent videoContent = assemble(videoDto);
			document = new Document(dataType, videoContent,
					documentDto.getRank());
		} else if (dataType.equals(Message.class.getSimpleName())) {
			MessageDto messageDto = jsonConverter.convertFromObject(map,
					MessageDto.class);
			Message message = assemble(messageDto);
			document = new Document(dataType, message, documentDto.getRank());

		} else {
			throw new IllegalArgumentException(
					"Uknown data type for document: " + dataType);
		}
		return document;
	}

	private static Message assemble(MessageDto messageDto) {
		// TODO Auto-generated method stub
		return null;
	}

	public static VideoContent assemble(VideoDto videoDto) {
		Video video = new Video.Builder().itemKey(videoDto.getItemKey())
				.url(videoDto.getUrl()).build();
		VideoContent content = new VideoContent.Builder()
				.video(video)
				.title(videoDto.getTitle())
				.category(
						Category.getCategoryByCategoryName(videoDto
								.getCategory()))
				.description(videoDto.getDescription())
				.externalNetwork(
						ExternalNetwork.getNetworkById(videoDto
								.getExternalNetworkId()))
				.thumb(new Image(videoDto.getThumb().getUrl()))
				.lastUpdated(System.currentTimeMillis()).build();
		return content;

	}

	public static Place assemble(PlaceDto placeDto) {
		if (placeDto == null)
			return null;
		Place.Builder placeBuilder = new Place.Builder();
		placeBuilder.placeId(placeDto.getPlaceId())
				.description(placeDto.getDescription())
				.address(assemble(placeDto.getAddressdto()))
				.boundingBox(assemble(placeDto.getBoundingBox()))
				.externalIdentifier(placeDto.getExternalIdentitifer())
				.region(placeDto.getRegion()).name(placeDto.getName())
				.parent(assemble(placeDto.getParent()))
				.locator(placeDto.getLocator());
		if (placeDto.getExternalNetworkId() != null
				&& placeDto.getExternalNetworkId() != -1)
			placeBuilder.externalNetwork(ExternalNetwork
					.getNetworkById(placeDto.getExternalNetworkId()));
		if (placeDto.getThumb() != null)
			placeBuilder.thumb(new Image(placeDto.getThumb().getUrl()));
		return placeBuilder.build();

	}

	public static Rating assemble(RatingDto ratingDto) {
		if (ratingDto == null)
			return null;
		Rating.Builder ratingBuilder = new Rating.Builder();
		ratingBuilder.max(ratingDto.getMax()).min(ratingDto.getMin())
				.numRatings(ratingDto.getNumRatings())
				.rating(ratingDto.getRating());
		return ratingBuilder.build();
	}

	public static Address assemble(AddressDto addressdto) {
		if (addressdto == null)
			return null;
		return new Address.Builder().city(addressdto.getCity())
				.country(addressdto.getCountry())
				.postalCode(addressdto.getPostalCode())
				.stateOrRegion(addressdto.getStateOrRegion())
				.streetName(addressdto.getStreetName())
				.unitName(addressdto.getUnitName()).build();
	}

	public static Geobox assemble(GeoboxDto geoboxDto) {
		if (geoboxDto == null)
			return null;
		return new Geobox.Builder().center(assemble(geoboxDto.getCenter()))
				.lowerLeft(assemble(geoboxDto.getLowerLeft()))
				.lowerRight(assemble(geoboxDto.getLowerRight()))
				.upperLeft(assemble(geoboxDto.getUpperLeft()))
				.upperRight(assemble(geoboxDto.getUpperRight())).build();
	}

	public static Location assemble(LocationDto locationDto) {
		if (locationDto == null)
			return null;
		return new Location.Builder().altitude(locationDto.getAltitude())
				.latitude(locationDto.getLatitude())
				.longitude(locationDto.getLongitude()).build();
	}

	public static AdminInterest assemble(AdminInterestDto adminInterestDto) {

		AdminInterest adminInterest = null;
		AdminInterestType adminInterestType = AdminInterestType
				.getAdminInterestTypeFromId(adminInterestDto.getInterestType());
		if (adminInterestType.equals(AdminInterestType.INTEREST)) {
			adminInterest = new Interest(adminInterestDto.getId(),adminInterestDto.getName(),
					adminInterestDto.getParentInterestId());
		} else if (adminInterestType
				.equals(AdminInterestType.EXTERNAL_INTEREST)) {
			adminInterest = new ExternalInterest(adminInterestDto.getId(),
					adminInterestDto.getName(),
					adminInterestDto.getInterestId(),
					adminInterestDto.getExternalNetwork());
		} else if (adminInterestType
				.equals(AdminInterestType.UNMAPPED_INTEREST)) {
			adminInterest = new UnmappedInterest.Builder()
			.externalNetwork(adminInterestDto.getExternalNetwork())
			.unmappedId(adminInterestDto.getId())
			.name(adminInterestDto.getName())
			.build();
		}
		return adminInterest;
	}

	public static ConfigurationRulesDto assembleConfigurationList(
			List<Configuration> rules) {
		ConfigurationRulesDto configurationRulesDto = new ConfigurationRulesDto();
		ExternalNetwork externalNetwork = null;
		ExternalNetworkConfigurationDto externalNetworkConfigurationDto = null;
		for (Configuration config : rules) {
			if (config.getExternalNetwork() == null) {
				configurationRulesDto.getGeneralRules().put(config.getName(),
						config.getValue());
			} else if (config.getExternalNetwork() == externalNetwork) {
				externalNetworkConfigurationDto.getRules().put(
						config.getName(), config.getValue());
			} else {
				if (externalNetworkConfigurationDto != null) {
					configurationRulesDto.getProviders().add(
							externalNetworkConfigurationDto);
				}
				externalNetworkConfigurationDto = new ExternalNetworkConfigurationDto(
						config.getExternalNetwork());
				externalNetwork = config.getExternalNetwork();
				externalNetworkConfigurationDto.getRules().put(
						config.getName(), config.getValue());
			}
		}
		if (externalNetworkConfigurationDto != null) {
			configurationRulesDto.getProviders().add(
					externalNetworkConfigurationDto);
		}
		return configurationRulesDto;

	}
	
	public static List<ExternalNetworkConfigurationDto> getNetworks()
	{
		List<ExternalNetworkConfigurationDto> networks = new LinkedList<ExternalNetworkConfigurationDto>();
		for (ExternalNetwork externalNetwork : ExternalNetwork.values()) {
			networks.add(new ExternalNetworkConfigurationDto(externalNetwork, null));
		}
		return networks;
	}
}
