package com.ubiquity.social.api.linkedin;

import java.io.ByteArrayInputStream;
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
import com.ubiquity.social.api.linkedin.dto.LinkedInApiDtoAssembler;
import com.ubiquity.social.api.linkedin.dto.container.LinkedInValuesDto;
import com.ubiquity.social.api.linkedin.dto.model.LinkedInConnectionDto;
import com.ubiquity.social.api.linkedin.dto.model.LinkedInMessageDto;
import com.ubiquity.social.api.linkedin.endpoints.LinkedInApiEndpoints;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.Event;
import com.ubiquity.social.domain.SocialIdentity;

public class LinkedInAPIOAuth2Strategy implements Social {

	private static Social linkedin = null;

	private LinkedInApiEndpoints linkedInApi;

	private JsonConverter jsonConverter = JsonConverter.getInstance();

	String profileUrl = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,formatted-name,email-address,picture-url,public-profile-url)";
	String connectionUrl = "https://api.linkedin.com/v1/people/~/connections:(id,first-name,last-name,formatted-name,picture-url,public-profile-url)";
	String messageUrl = "http://api.linkedin.com/v1/people/~/mailbox";

	private Logger log = LoggerFactory.getLogger(getClass());

	private LinkedInAPIOAuth2Strategy() {
		// this initialization only needs to be done once per VM
		RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
		linkedInApi = ProxyFactory.create(LinkedInApiEndpoints.class, "https://api.linkedin.com/v1");
	}

	public static Social getProviderAPI() {
		if (linkedin == null)
			linkedin = new LinkedInAPIOAuth2Strategy();
		return linkedin;
	}

	

	@Override
	public Contact authenticateUser(SocialIdentity identity) {
		ClientResponse<String> response = null;
		try {
			response = linkedInApi.getProfile(identity.getAccessToken(), "json");
			// convert the raw json into a container
			LinkedInConnectionDto result = jsonConverter.parse(response.getEntity(), LinkedInConnectionDto.class);
			// create a strongly typed list from the generic data container
			Contact contact = LinkedInApiDtoAssembler.assembleContact(identity, result);
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
			response = linkedInApi.getConnections(identity.getAccessToken(), "new", "json");
			// convert the raw json into a container
			LinkedInValuesDto result = jsonConverter.parse(response.getEntity(), LinkedInValuesDto.class);
			// create a strongly typed list from the generic data container
			List<LinkedInConnectionDto> connectionsDtoList = jsonConverter.convertToListFromList(result.getValues(), LinkedInConnectionDto.class);

			// assemble from dto to entity
			for(LinkedInConnectionDto connectionDto : connectionsDtoList) {
				log.debug("Assembling contact {}", connectionDto);
				contacts.add(LinkedInApiDtoAssembler.assembleContact(identity, connectionDto));
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

		LinkedInMessageDto linkedInMessageDto = new LinkedInMessageDto.Builder()
			.subject("You received a gift!")
			.body(message)
			.recipients(new String[] { toIdentity.getIdentifier() })
			.build();

		ClientResponse<String> response = null;
		try {
			String payload = jsonConverter.convertToPayload(linkedInMessageDto);
			response = linkedInApi.postMessage(new ByteArrayInputStream(payload.getBytes()), fromIdentity.getAccessToken());
			return response.getResponseStatus().getStatusCode() == 201;
		} finally {
			if(response != null)
				response.releaseConnection();
		}
	}
}
