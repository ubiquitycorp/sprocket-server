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
import com.ubiquity.social.api.Social;
import com.ubiquity.social.api.facebook.dto.FacebookGraphApiDtoAssembler;
import com.ubiquity.social.api.facebook.dto.container.FacebookDataDto;
import com.ubiquity.social.api.facebook.dto.model.FacebookContactDto;
import com.ubiquity.social.api.facebook.dto.model.FacebookEventDto;
import com.ubiquity.social.api.facebook.endpoints.FacebookGraphApiEndpoints;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.Event;
import com.ubiquity.social.domain.SocialIdentity;

/***
 * Facebook API class
 * 
 * @author peter.tadros
 * 
 */
public class FacebookAPI implements Social {

	private static Social facebook = null;
	private Logger log = LoggerFactory.getLogger(getClass());

	private JsonConverter jsonConverter = JsonConverter.getInstance();
	private FacebookGraphApiEndpoints graphApi;

	private FacebookAPI() {
		// this initialization only needs to be done once per VM
		RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
		graphApi = ProxyFactory.create(FacebookGraphApiEndpoints.class, "https://graph.facebook.com");

	}

	public static Social getProviderAPI() {
		if (facebook == null)
			facebook = new FacebookAPI();
		return facebook;
	}

	
	@Override
	public Contact authenticateUser(SocialIdentity identity) {

		ClientResponse<String> response = null;
		try {
			response = graphApi.getMe(identity.getAccessToken());
			if(response.getResponseStatus().getStatusCode() != 200)
				throw new RuntimeException("Unable to authenticate with the provided credentials");
				
			FacebookContactDto contactDto = jsonConverter.parse(response.getEntity(), FacebookContactDto.class);
			return FacebookGraphApiDtoAssembler.assembleContact(identity, contactDto);
			
		} finally {
			if(response != null)
				response.releaseConnection();
		}
	}

	@Override
	public List<Contact> findContactsByOwnerIdentity(SocialIdentity identity) {
		List<Contact> contacts = new LinkedList<Contact>();
		
		ClientResponse<String> response = null;
		try {
			response = graphApi.getFriends(identity.getAccessToken(), "id,name,first_name,last_name,username,link,picture");
			// convert the raw json into a container
			FacebookDataDto result = jsonConverter.parse(response.getEntity(), FacebookDataDto.class);
			// create a strongly typed list from the generic data container
			List<FacebookContactDto> contactsDtoList = jsonConverter.convertToListFromList(result.getData(), FacebookContactDto.class);
			
			// assemble from dto to entity
			for(FacebookContactDto contactDto : contactsDtoList) {
				log.debug("Assembling contact {}", contactDto);
				contacts.add(FacebookGraphApiDtoAssembler.assembleContact(identity.getUser(), contactDto));
			}
			return contacts;
		} finally {
			if(response != null)
				response.releaseConnection();
		}
	}

	@Override
	public List<Event> findEventsCreatedByContacts(SocialIdentity identity,
			List<Contact> contacts) {
		List<Event> events = new LinkedList<Event>();

		for(Contact contact : contacts) {
			ClientResponse<String> response = null;
			try {
				response = graphApi.getEvents(Long.parseLong(contact.getSocialIdentity().getIdentifier()), identity.getAccessToken());
				if(response.getResponseStatus().getStatusCode() != 200) {
					// in this case, for now let's not kill the whole loop when it's possible only 1 request is failing (perhaps FB removed a pointer)
					log.warn("Retrieving events for identity {} failed, reason: {}", contact.getSocialIdentity().getIdentifier(), response.getEntity());
					continue;
				}
					
				// convert the raw json into a container
				FacebookDataDto result = jsonConverter.parse(response.getEntity(), FacebookDataDto.class);

				// create a strongly typed list from the generic data container
				List<FacebookEventDto> eventsDtoList = jsonConverter.convertToListFromList(result.getData(), FacebookEventDto.class);
				for(FacebookEventDto eventDto : eventsDtoList) {
					events.add(FacebookGraphApiDtoAssembler.assembleEvent(contact, eventDto));
				}
			} finally {
				if(response != null)
					response.releaseConnection();
			}
		}
		return events;
	}

	@Override
	public Boolean postToWall(SocialIdentity fromIdentity,
			SocialIdentity toIdentity, String message) {
		throw new UnsupportedOperationException();
	}


}
