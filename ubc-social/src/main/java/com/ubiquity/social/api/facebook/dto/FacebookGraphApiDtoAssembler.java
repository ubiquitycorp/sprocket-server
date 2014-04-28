package com.ubiquity.social.api.facebook.dto;

import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;

import com.ubiquity.identity.domain.User;
import com.ubiquity.media.domain.Image;
import com.ubiquity.social.api.facebook.dto.container.FacebookDataDto;
import com.ubiquity.social.api.facebook.dto.model.FacebookContactDto;
import com.ubiquity.social.api.facebook.dto.model.FacebookEventDto;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.Event;
import com.ubiquity.social.domain.SocialIdentity;
import com.ubiquity.social.domain.SocialProviderType;

/***
 * Assembler class for assembling a list of events from an FB graph result
 * 
 * @author chris
 *
 */
public class FacebookGraphApiDtoAssembler {
		
	/***
	 * Assembles a contact and sets the identity user property as the owner. It also sets the 
	 * identitifer and social provider on the passed in identity reference.
	 * 
	 * @param identity
	 * @param result
	 * @return
	 */
	public static Contact assembleContact(SocialIdentity identity, FacebookContactDto result) {
		
		identity.setIdentifier(result.getId());
		identity.setIsActive(Boolean.TRUE);
		identity.setLastUpdated(System.currentTimeMillis());
		identity.setSocialProviderType(SocialProviderType.Facebook);
		
		// set the result and type on the 
		Contact contact = new Contact.Builder()
			.socialIdentity(identity)
			.owner(identity.getUser())
			.firstName(result.getFirstName())
			.lastName(result.getLastName())
			.displayName(result.getName())
			.lastUpdated(System.currentTimeMillis())
			.image(new Image(String.format("https://graph.facebook.com/%s/picture", result.getId())))
		.build();

		return contact;
	}
	
	
	/***
	 * Assembles a contact with this owner set as the owner in the return object
	 * 
	 * @param owner
	 * @param result
	 * 
	 * @return
	 */
	public static Contact assembleContact(User owner, FacebookContactDto result) {
		Contact contact = new Contact.Builder()
			.socialIdentity(new SocialIdentity.Builder()
				.identifier(result.getId())
				.isActive(Boolean.TRUE)
				.lastUpdated(System.currentTimeMillis())
				.socialProviderType(SocialProviderType.Facebook).build())
			.firstName(result.getFirstName())
			.lastName(result.getLastName())
			.displayName(result.getName())
			.owner(owner)
			.lastUpdated(System.currentTimeMillis())
			.image(new Image(String.format("https://graph.facebook.com/%s/picture", result.getId())))
		.build();

		return contact;
	}
	
	/***
	 * Assembles an event from the api data and sets the passed-in contact as the owner
	 * 
	 * @param contact Contact associated with this event
	 * @param result Event dto
	 * @return
	 */
	public static Event assembleEvent(Contact contact, FacebookEventDto result) {
		Event event = new Event.Builder()
		.startDate(new DateTime(result.getStartTime()).getMillis())
		.endDate(new DateTime(result.getEndTime()).getMillis())
		.name(result.getName())
		.socialProviderIdentifier(result.getId())
		.contact(contact)
		.build();
		return event;
	}
	/***
	 * Returns a list of event entities with the passed-in contact set as the owner.
	 * 
	 * @param contact The owner of the event
	 * @param result List of event entities
	 * @return
	 */
	public static List<Event> assembleEvents(Contact contact, FacebookDataDto result) {
		List<Event> events = new LinkedList<Event>();
//		List<Map<String, String>> data = result.getData();
//
//		for(Map<String, String> element : data) {
//			String startTime = element.get("start_time");
//			String endTime = element.get("end_time");
//			String name = element.get("name");
//			String identifier = element.get("id");
//			
//			Event event = new Event.Builder()
//					.startDate(new DateTime(startTime).getMillis())
//					.endDate(new DateTime(endTime).getMillis())
//					.name(name)
//					.socialProviderIdentifier(identifier)
//					.contact(contact)
//					.build();
//			events.add(event);
//		}
		return events;
	}
	

}
