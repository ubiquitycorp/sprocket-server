package com.ubiquity.social.api.linkedin;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.social.api.Social;
import com.ubiquity.social.api.linkedin.dto.LinkedInApiDtoAssembler;
import com.ubiquity.social.api.linkedin.dto.container.LinkedInValuesDto;
import com.ubiquity.social.api.linkedin.dto.model.LinkedInConnectionDto;
import com.ubiquity.social.api.linkedin.dto.model.LinkedInMessageDto;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.Event;
import com.ubiquity.social.domain.SocialIdentity;

public class LinkedInAPIOAuth1Strategy implements Social {

	private static Social linkedin = null;

	private static final String PROFILE_URL = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,formatted-name,email-address,picture-url,public-profile-url)?format=json";
	private static final String CONNECTION_URL = "https://api.linkedin.com/v1/people/~/connections:(id,first-name,last-name,formatted-name,picture-url,public-profile-url)?modified=new&format=json";
	private static final String MESSAGE_URL = "http://api.linkedin.com/v1/people/~/mailbox";
	private static final String SCOPES = "r_basicprofile r_emailaddress r_network";

	private JsonConverter jsonConverter = JsonConverter.getInstance();

	private OAuthService oAuthService;

	private Logger log = LoggerFactory.getLogger(getClass());

	private LinkedInAPIOAuth1Strategy() {
		try {
			Configuration configuration = new PropertiesConfiguration("sprocketapi.properties");
			String consumerKey = configuration.getString("social.linkedin.app.consumerKey");
			String consumerSecret = configuration.getString("social.linkedin.app.consumerSecret");
			// start the oauth service; we can't use a rest easy proxy here to support the legacy protocols
			oAuthService = new ServiceBuilder()
			.provider(LinkedInApi.class)
			.apiKey(consumerKey)
			.apiSecret(consumerSecret)
			.scope(SCOPES)
			.build();
		} catch (Exception e) {
			throw new RuntimeException("An error occured authenticating with LinkedIn", e);
		}
	}

	public static Social getProviderAPI() {
		if (linkedin == null)
			linkedin = new LinkedInAPIOAuth1Strategy();
		return linkedin;
	}

	@Override
	public Contact authenticateUser(SocialIdentity identity) {

		OAuthRequest request = new OAuthRequest(Verb.GET, PROFILE_URL);
		Token accessToken = new Token(identity.getAccessToken(), identity.getSecretToken());
		oAuthService.signRequest(accessToken, request);

		Response response = request.send();
		log.debug(response.getCode() + "");
		log.debug(response.getBody());
		if (response.getCode() == 200) {
			InputStream is = new ByteArrayInputStream(response.getBody().getBytes());
			LinkedInConnectionDto contactDto = jsonConverter.convertFromPayload(is, LinkedInConnectionDto.class);
			Contact contact = LinkedInApiDtoAssembler.assembleContact(identity, contactDto);
			return contact;
		}	
		throw new RuntimeException("Could not authenticate with social network");
	}

	@Override
	public List<Contact> findContactsByOwnerIdentity(SocialIdentity identity) {
		List<Contact> contacts = new LinkedList<Contact>();
		Token accessToken = new Token(identity.getAccessToken(), identity.getSecretToken());
		OAuthRequest request = new OAuthRequest(Verb.GET, CONNECTION_URL);
		oAuthService.signRequest(accessToken, request);
		Response response = request.send();
		
		String contactsJson = response.getBody();

		// convert the raw json into a container
		LinkedInValuesDto result = jsonConverter.parse(contactsJson, LinkedInValuesDto.class);
		// create a strongly typed list from the generic data container
		List<LinkedInConnectionDto> connectionsDtoList = jsonConverter.convertToListFromList(result.getValues(), LinkedInConnectionDto.class);
		for(LinkedInConnectionDto connectionDto : connectionsDtoList) {
			Contact contact = LinkedInApiDtoAssembler.assembleContact(identity, connectionDto);
			contacts.add(contact);
		}
		return contacts;
	}

	@Override
	public List<Event> findEventsCreatedByContacts(SocialIdentity identity,
			List<Contact> contacts) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Boolean postToWall(SocialIdentity fromIdentity,
			SocialIdentity toIdentity, String message) {
		Token accessToken = new Token(fromIdentity.getAccessToken(), fromIdentity.getSecretToken());
		OAuthRequest request = new OAuthRequest(Verb.POST, MESSAGE_URL);
		LinkedInMessageDto linkedInMessage = new LinkedInMessageDto.Builder()
			.subject("You received a gift!")
			.body(message)
			.recipients(new String[] { toIdentity.getIdentifier() })
			.build();
		request.addPayload(JsonConverter.getInstance().convertToPayload(linkedInMessage));
		request.addHeader("Content-Type", "application/json");
		oAuthService.signRequest(accessToken, request);
		Response response = request.send();
		return response.getCode() == 201;
	}

}
