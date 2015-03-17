package com.ubiquity.sprocket.network.api.facebook;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.ubiquity.sprocket.network.api.dto.model.Activity;
import com.ubiquity.sprocket.network.api.dto.model.ActivityType;
import com.ubiquity.sprocket.network.api.dto.model.AgeRange;
import com.ubiquity.sprocket.network.api.dto.model.Contact;
import com.ubiquity.sprocket.network.api.dto.model.Conversation;
import com.ubiquity.sprocket.network.api.dto.model.Gender;
import com.ubiquity.sprocket.network.api.dto.model.Message;
import com.ubiquity.sprocket.network.api.facebook.dto.container.FacebookDataDto;
import com.ubiquity.sprocket.network.api.facebook.dto.model.FacebookActivityDto;
import com.ubiquity.sprocket.network.api.facebook.dto.model.FacebookAgeRangeDto;
import com.ubiquity.sprocket.network.api.facebook.dto.model.FacebookContactDto;
import com.ubiquity.sprocket.network.api.facebook.dto.model.FacebookConversationDto;
import com.ubiquity.sprocket.network.api.facebook.dto.model.FacebookMessageDto;

/***
 * Assembler class for assembling a list of events from an FB graph result
 * 
 * @author chris
 * 
 */
public class FacebookGraphApiDtoAssembler {

	public static FacebookConversationDto assembleConversation(
			Conversation conversation) {

		FacebookConversationDto.Builder facebookConversationDtoBuilder = new FacebookConversationDto.Builder();
		FacebookDataDto to = new FacebookDataDto();
		FacebookDataDto comments = new FacebookDataDto();

		if (conversation.getMessages() == null) {
			return facebookConversationDtoBuilder.build();
		}
		// assemble message
		for (Message message : conversation.getMessages()) {

			Map<String, Object> senderDto = new HashMap<String, Object>();
			senderDto.put("name", message.getSender().getDisplayName());
			senderDto.put("id", message.getSender().getExternalIdentity()
					.getIdentifier());

			FacebookMessageDto fbmessage = new FacebookMessageDto.Builder()
					.id(message.getExternalIdentifier())
					.message(message.getBody())
					.createdTime(message.getSentDate() / 1000)
					// Converts Facebook time to milliseconds
					.from(senderDto).build();

			comments.getData().add(fbmessage);
		}
		// assemble receivers

		if (conversation.getReceivers().size() > 0) // unknown contact
		{
			for (Contact contact : conversation.getReceivers()) {

				Map<String, Object> senderDto = new HashMap<String, Object>();
				senderDto.put("name", contact.getDisplayName());
				senderDto.put("id", contact.getExternalIdentity()
						.getIdentifier());
				to.getData().add(senderDto);
			}
		}
		return facebookConversationDtoBuilder.to(to).comments(comments)
				.id(conversation.getConversationIdentifier()).build();
	}

	// private static Contact createContact(String identifier, String name,
	// User user) {
	// Contact contact = new Contact.Builder()
	// .externalIdentity(
	// new ExternalIdentity.Builder()
	// .identifier(identifier)
	// .externalNetwork(
	// ExternalNetwork.Facebook.ordinal())
	// .lastUpdated(System.currentTimeMillis())
	// .isActive(Boolean.TRUE).build())
	// .image(new Image(String.format(
	// "https://graph.facebook.com/%s/picture", identifier)))
	// .owner(user)
	// .displayName(name).lastUpdated(System.currentTimeMillis())
	// .build();
	// return contact;
	// }

	public static FacebookContactDto assembleContact(Contact result) {

		FacebookContactDto.Builder contactBuilder = new FacebookContactDto.Builder();
		// get gender from string value
		Gender gender = result.getGender();
		String fbgender = null;
		if (gender != null) {
			if (gender.equals(Gender.Female)) {
				fbgender = "female";
			} else if (gender.equals(Gender.Male)) {
				fbgender = "male";
			}
		}

		FacebookAgeRangeDto fbAgeRange = new FacebookAgeRangeDto();
		AgeRange ageRange = result.getAgeRange();
		fbAgeRange.setMax(ageRange.getMax());
		fbAgeRange.setMin(ageRange.getMin());

		// set the result and type on the
		contactBuilder.gender(fbgender).firstName(result.getFirstName())
				.lastName(result.getLastName())
				.displayName(result.getDisplayName()).email(result.getEmail())
				.id(result.getExternalIdentity().getIdentifier())
				.link(result.getProfileUrl()).ageRange(fbAgeRange);
		return contactBuilder.build();
	}

	// public static Contact assembleContact(User owner, FacebookContactDto
	// result) {
	// Contact contact = new Contact.Builder()
	// .externalIdentity(
	// new ExternalIdentity.Builder()
	// .identifier(result.getId())
	// .isActive(Boolean.TRUE)
	// .lastUpdated(System.currentTimeMillis())
	// .externalNetwork(
	// ExternalNetwork.Facebook.ordinal())
	// .build())
	// .firstName(result.getFirstName())
	// .lastName(result.getLastName())
	// .displayName(result.getName())
	// .owner(owner)
	// .lastUpdated(System.currentTimeMillis())
	// .image(new Image(
	// String.format("https://graph.facebook.com/%s/picture",
	// result.getId()))).build();
	//
	// return contact;
	// }
	//
	// /***
	// * Assembles an event from the api data and sets the passed-in contact as
	// * the owner
	// *
	// * @param contact
	// * Contact associated with this event
	// * @param result
	// * Event dto
	// * @return
	// */
	// public static Event assembleEvent(Contact contact, FacebookEventDto
	// result) {
	// Event event = new Event.Builder()
	// .startDate(new DateTime(result.getStartTime()).getMillis())
	// .endDate(new DateTime(result.getEndTime()).getMillis())
	// .name(result.getName())
	// .socialProviderIdentifier(result.getId()).contact(contact)
	// .build();
	// return event;
	// }
	//
	// /***
	// * Returns a list of event entities with the passed-in contact set as the
	// * owner.
	// *
	// * @param contact
	// * The owner of the event
	// * @param result
	// * List of event entities
	// * @return
	// */
	// public static List<Event> assembleEvents(Contact contact,
	// FacebookDataDto result) {
	// List<Event> events = new LinkedList<Event>();
	// return events;
	// }
	//
	public static FacebookActivityDto assembleActivity(Activity activity) {

		// create a build that populates required fields
		// Converts Facebook time to millseconds
		FacebookActivityDto.Builder activityBuilder = new FacebookActivityDto.Builder();

		// build contact, posted by
		FacebookContactDto fbContact = assembleContact(activity.getPostedBy());
		activityBuilder.from(fbContact);
		Random random = new Random();
		if (activity.getActivityType().equals(ActivityType.VIDEO)) {
			activityBuilder.type("video").source(activity.getVideo().getUrl())
					.link(activity.getLink())
					.picture(activity.getImage().getUrl())// sets the thumb for
															// the video
					.description(activity.getBody());
		} else if (activity.getActivityType().equals(ActivityType.PHOTO)) {
			activityBuilder.type("photo").picture(activity.getImage().getUrl());

			if (random.nextBoolean())
				activityBuilder.description(activity.getBody());
			else
				activityBuilder.story(activity.getBody());
		} else if (activity.getActivityType().equals(ActivityType.LINK)) {
			activityBuilder.type("link").link(activity.getLink())
					.description(activity.getBody());
		} else {
			activityBuilder.type("status");
			if (random.nextBoolean())
				activityBuilder.message(activity.getBody());
			else
				activityBuilder.story(activity.getBody());
		}
		// fill out the rest
		activityBuilder.name(activity.getTitle())
				.createdTime(activity.getCreationDate())
				.id(activity.getExternalIdentifier());

		return activityBuilder.build();

	}
	//
	// private static String trimActivityUrl(String pictureUrl) {
	// String result = "";
	// if (pictureUrl == null)
	// return "";
	// if (pictureUrl.contains("scontent-b.xx.fbcdn.net")
	// || pictureUrl.contains("fbcdn-profile-a.akamaihd.net")
	// || pictureUrl.contains("scontent-a.xx.fbcdn.net")
	// || pictureUrl.contains("fbcdn-sphotos-e-a.akamaihd.net")
	// || pictureUrl.contains("fbcdn-sphotos-f-a.akamaihd.net")
	// || pictureUrl.contains("fbcdn-sphotos-g-a.akamaihd.net")
	// || pictureUrl.contains("fbcdn-sphotos-h-a.akamaihd.net")
	// || pictureUrl.contains("fbcdn-sphotos-b-a.akamaihd.net")
	// || pictureUrl.contains("fbcdn-sphotos-a-a.akamaihd.net")
	// || pictureUrl.contains("fbcdn-sphotos-c-a.akamaihd.net")) {
	// String[] stringarray = pictureUrl.split("/");
	// // igonre element 4 5
	// for (int i = 0; i < stringarray.length; i++) {
	// if (i == 0)
	// result = stringarray[i] + result;
	// else if (stringarray[i].equalsIgnoreCase("v")) {
	// i = i + 2;
	// } else if (i != 4 && i != 5)
	// result = result + "/" + stringarray[i];
	// }
	// } else {
	// result = pictureUrl;
	// }
	// return result;
	// }

}
