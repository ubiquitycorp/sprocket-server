package com.ubiquity.social.api.google;

import java.util.LinkedList;
import java.util.List;

import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.social.api.Social;
import com.ubiquity.social.api.google.dto.GooglePlusApiDtoAssembler;
import com.ubiquity.social.api.google.dto.container.GoogleItemsDto;
import com.ubiquity.social.api.google.dto.model.GooglePersonDto;
import com.ubiquity.social.api.google.endpoints.GooglePlusApiEndpoints;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.Event;
import com.ubiquity.social.domain.SocialIdentity;

/***
 * Google API class
 * 
 * @author Peter
 * 
 */

public class GoogleAPI implements Social {

	private static Social google = null;
	OAuthService service = null;
	
	private Logger log = LoggerFactory.getLogger(getClass());

	private GooglePlusApiEndpoints googleApi;
	
	private JsonConverter jsonConverter = JsonConverter.getInstance();
	
	private GoogleAPI() {
		// this initialization only needs to be done once per VM
		RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
		googleApi = ProxyFactory.create(GooglePlusApiEndpoints.class, "https://www.googleapis.com/plus/v1");
	}

	public static Social getProviderAPI() {
		if (google == null)
			google = new GoogleAPI();
		return google;
	}

	@Override
	public Contact authenticateUser(SocialIdentity identity) {
		ClientResponse<String> response = null;
		try {
			response = googleApi.getMe(identity.getAccessToken());
			log.debug("response code: {}", response.getResponseStatus().getStatusCode());
			GooglePersonDto result = jsonConverter.parse(response.getEntity(), GooglePersonDto.class);
			Contact contact = GooglePlusApiDtoAssembler.assembleContact(identity, result);
			return contact;
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
			response = googleApi.getFriends(identity.getAccessToken());
			GoogleItemsDto result = jsonConverter.parse(response.getEntity(), GoogleItemsDto.class);
			
			List<GooglePersonDto> peopleDtoList = jsonConverter.convertToListFromList(result.getItems(), GooglePersonDto.class);
			for(GooglePersonDto personDto : peopleDtoList) {
				Contact contact = GooglePlusApiDtoAssembler.assembleContact(identity, personDto);
				contacts.add(contact);
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
		throw new UnsupportedOperationException();

	}

	@Override
	public Boolean postToWall(SocialIdentity fromIdentity,
			SocialIdentity toIdentity, String message) {
		throw new UnsupportedOperationException();
	}
}
