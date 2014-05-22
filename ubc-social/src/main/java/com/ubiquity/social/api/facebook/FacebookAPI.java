package com.ubiquity.social.api.facebook;

import java.util.LinkedList;
import java.util.List;

import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.social.api.SocialAPI;
import com.ubiquity.social.api.exception.AuthorizationException;
import com.ubiquity.social.api.facebook.dto.FacebookGraphApiDtoAssembler;
import com.ubiquity.social.api.facebook.dto.container.FacebookDataDto;
import com.ubiquity.social.api.facebook.dto.container.FacebookRequestFailureDto;
import com.ubiquity.social.api.facebook.dto.model.FacebookActivityDto;
import com.ubiquity.social.api.facebook.dto.model.FacebookContactDto;
import com.ubiquity.social.api.facebook.dto.model.FacebookConversationDto;
import com.ubiquity.social.api.facebook.dto.model.FacebookEventDto;
import com.ubiquity.social.api.facebook.endpoints.FacebookGraphApiEndpoints;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.Event;
import com.ubiquity.social.domain.ExternalIdentity;
import com.ubiquity.social.domain.Message;

/***
 * Facebook API class
 * 
 * @author peter.tadros
 * 
 */
public class FacebookAPI implements SocialAPI {

	private static SocialAPI facebook = null;
	private Logger log = LoggerFactory.getLogger(getClass());

	private JsonConverter jsonConverter = JsonConverter.getInstance();
	private FacebookGraphApiEndpoints graphApi;

	private FacebookAPI() {
		// this initialization only needs to be done once per VM
		RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
		graphApi = ProxyFactory.create(FacebookGraphApiEndpoints.class,
				"https://graph.facebook.com");
	}

	public static SocialAPI getProviderAPI() {
		if (facebook == null)
			facebook = new FacebookAPI();
		return facebook;
	}

	@Override
	public Contact authenticateUser(ExternalIdentity identity) {

		ClientResponse<String> response = null;
		try {
			response = graphApi.getMe(identity.getAccessToken());
			checkError(response);

			FacebookContactDto contactDto = jsonConverter.parse(
					response.getEntity(), FacebookContactDto.class);
			return FacebookGraphApiDtoAssembler.assembleContact(identity,
					contactDto);

		} finally {
			if (response != null)
				response.releaseConnection();
		}
	}

	@Override
	public List<Contact> findContactsByOwnerIdentity(ExternalIdentity identity) {
		List<Contact> contacts = new LinkedList<Contact>();

		ClientResponse<String> response = null;
		try {
			response = graphApi.getFriends(identity.getAccessToken(),
					"id,name,first_name,last_name,username,link,picture");
			checkError(response);

			// convert the raw json into a container
			FacebookDataDto result = jsonConverter.parse(response.getEntity(),
					FacebookDataDto.class);
			// create a strongly typed list from the generic data container
			List<FacebookContactDto> contactsDtoList = jsonConverter
					.convertToListFromList(result.getData(),
							FacebookContactDto.class);

			// assemble from dto to entity
			for (FacebookContactDto contactDto : contactsDtoList) {
				log.debug("Assembling contact {}", contactDto);
				contacts.add(FacebookGraphApiDtoAssembler.assembleContact(
						identity.getUser(), contactDto));
			}
			return contacts;
		} finally {
			if (response != null)
				response.releaseConnection();
		}
	}

	@Override
	public List<Event> findEventsCreatedByContacts(ExternalIdentity identity,
			List<Contact> contacts) {
		List<Event> events = new LinkedList<Event>();

		for (Contact contact : contacts) {
			ClientResponse<String> response = null;
			try {
				response = graphApi.getEvents(Long.parseLong(contact
						.getSocialIdentity().getIdentifier()), identity
						.getAccessToken());
				if (response.getResponseStatus().getStatusCode() != 200) {
					// in this case, for now let's not kill the whole loop when
					// it's possible only 1 request is failing (perhaps FB
					// removed a pointer)
					log.warn(
							"Retrieving events for identity {} failed, reason: {}",
							contact.getSocialIdentity().getIdentifier(),
							response.getEntity());
					continue;
				}

				// convert the raw json into a container
				FacebookDataDto result = jsonConverter.parse(
						response.getEntity(), FacebookDataDto.class);

				// create a strongly typed list from the generic data container
				List<FacebookEventDto> eventsDtoList = jsonConverter
						.convertToListFromList(result.getData(),
								FacebookEventDto.class);
				for (FacebookEventDto eventDto : eventsDtoList) {
					events.add(FacebookGraphApiDtoAssembler.assembleEvent(
							contact, eventDto));
				}
			} finally {
				if (response != null)
					response.releaseConnection();
			}
		}
		return events;
	}

	@Override
	public Boolean postToWall(ExternalIdentity fromIdentity,
			ExternalIdentity toIdentity, String message) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Message> listMessages(ExternalIdentity externalIdentity) {

		List<Message> messages = new LinkedList<Message>();
		ClientResponse<String> response = null;
		try {
			response = graphApi.getInbox(externalIdentity.getAccessToken());
			checkError(response);

			// convert the raw json into a container
			FacebookDataDto result = jsonConverter.parse(response.getEntity(),
					FacebookDataDto.class);
			// create a strongly typed list from the generic data container
			List<FacebookConversationDto> conversationDtoList = jsonConverter
					.convertToListFromList(result.getData(),
							FacebookConversationDto.class);

			// assemble from dto to entity
			for (FacebookConversationDto conversationDto : conversationDtoList) {
				messages.add(FacebookGraphApiDtoAssembler.assemble(
						externalIdentity, conversationDto));
			}
		} finally {
			if (response != null)
				response.releaseConnection();
		}
		return messages;
	}

	private String getErrorMessage(ClientResponse<String> response) {
		String errorMessage = null;
		String errorBody = response.getEntity();
		if (errorBody != null) {
			FacebookRequestFailureDto failure = jsonConverter.parse(errorBody,
					FacebookRequestFailureDto.class);
			errorMessage = failure.getError().getMessage();
		} else {
			errorMessage = "Unable to authenticate with provided credentials";
		}
		return errorMessage;
	}

	private void checkError(ClientResponse<String> response) {
		int statusCode = response.getResponseStatus().getStatusCode();
		if (statusCode != 200) {
			if(statusCode == 401 || statusCode == 403 || statusCode == 400) // FB throws a 400 on expired tokens..
				throw new AuthorizationException(getErrorMessage(response));
			else
				throw new RuntimeException(getErrorMessage(response));
		}
	}

	@Override
	public List<Activity> listActivities(ExternalIdentity external) {
		List<Activity> activities = new LinkedList<Activity>();
		ClientResponse<String> response = null;
		try {
			response = graphApi.getFeed(external.getAccessToken());
			checkError(response);

			// convert the raw json into a container
			FacebookDataDto result = jsonConverter.parse(response.getEntity(),
					FacebookDataDto.class);
			// create a strongly typed list from the generic data container
			List<FacebookActivityDto> activitiesDtoList = jsonConverter
					.convertToListFromList(result.getData(),
							FacebookActivityDto.class);

			// assemble from dto to entity
			for (FacebookActivityDto activityDto : activitiesDtoList) {
				
				// build contact 
				Contact contact = FacebookGraphApiDtoAssembler.assembleContact(external, activityDto.getFrom());
				Activity activity = new Activity.Builder()
					.title(activityDto.getName())
					.body(activityDto.getDescription())
					.lastUpdated(System.currentTimeMillis())
					.creationDate(System.currentTimeMillis())
					.postedBy(contact)
					.build();
				
				activities.add(activity);
			}
		} finally {
			if (response != null)
				response.releaseConnection();
		}
		return activities;
	}

}
