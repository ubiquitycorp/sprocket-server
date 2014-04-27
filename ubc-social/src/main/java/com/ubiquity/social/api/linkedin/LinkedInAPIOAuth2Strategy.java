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

	//	/***
	//	 * This method authenticate user to LinkedIn using given token and secret
	//	 * token returns true if successfully connects to Linked and gets user
	//	 * profile, false if user is not authenticated or expired session token
	//	 * 
	//	 * @param token
	//	 *            , @param secretToken
	//	 * @return
	//	 */
	//	@Override
	//	public Contact authenticateUser(String token, String secretToken) {
	//		try {
	//			String url = profileUrl + "?format=json&oauth2_access_token=" + token;
	//			HttpResponse response = HttpUtility.sendGetRequest(url, null);
	//			if (response.getStatusLine().getStatusCode() == 200){
	//				AbstractContact profile = (AbstractContact) JsonConverter
	//						.getInstance().convertFromPayload(response.getEntity().getContent(), LinkedInConnectionDto.class);
	//				return profile.Convert();
	//			} else
	//				return null;
	//		} catch (Exception ex) {
	//			log.error("failed to send authentication request: " + ex.toString());
	//			return null;
	//		}
	//	}

	//	/***
	//	 * This method retrieved LinkedIn connections from their APi then parsed it to list of our contact domain
	//	 * @param token
	//	 * @param secretToken
	//	 * @return list of Contact domain
	//	 */
	//	@Override
	//	public List<Contact> getContacts(String token, String secretToken,
	//			Long userId) {
	//		String url = connectionUrl + "?modified=new&format=json&oauth2_access_token=" + token;
	//		List<Contact> contactsList = new ArrayList<Contact>();
	//		try {
	//			HttpResponse response = HttpUtility.sendGetRequest(url, null);
	//			String contactsJson = HttpUtility.getResponseBodyAsString(response);
	//			contactsJson = JsonConverter.getInstance().getValueByName(
	//					contactsJson, "values");
	//			List<LinkedInConnectionDto> contacts = new ArrayList<LinkedInConnectionDto>();
	//			if (!contactsJson.isEmpty()) {
	//				contacts = JsonConverter.getInstance()
	//						.convertToListFromPayload(contactsJson,
	//								LinkedInConnectionDto.class);
	//				parseprofileImage(contacts, contactsJson);
	//				for (LinkedInConnectionDto socialContact : contacts) {
	//					socialContact.setOwnerId(userId); // set owner id of
	//														// contacts to userId
	//					contactsList.add(socialContact.Convert()); // convert to
	//																// Contact JPA
	//																// object
	//				}
	//			}
	//
	//		} catch (Exception ex) {
	//			log.error("failed to send authentication request: " + ex.toString());
	//		}
	//		return contactsList;
	//	}
	//
	//	public void parseprofileImage(List<LinkedInConnectionDto> contacts,
	//			String jsonContacts) {
	//		String url;
	//		JsonArray array = JsonConverter.getInstance()
	//				.parse(jsonContacts, JsonElement.class).getAsJsonArray();
	//		for (int i = 0; i < contacts.size(); i++) {
	//			if (array.get(i).getAsJsonObject().get("pictureUrl") != null) {
	//				url = array.get(i).getAsJsonObject().get("pictureUrl")
	//						.getAsString();
	//				contacts.get(i).setImage(new Image(url));
	//			}
	//		}
	//	}
	//
	//	@Override
	//	public List<Event> findEvents(String token, String secretToken,
	//			String socialIdentifier, List<Contact> contact) {
	//		throw new UnsupportedOperationException();
	//	}
	//
	//	@Override
	//	public Boolean postToWall(String token, String secretToken,
	//			String socialIdentifier, String toSocialIdentifier,String message) {
	//		String url = messageUrl + "?oauth2_access_token=" + token;
	//		try {
	//			Message linkedInMessage = new Message();
	//			linkedInMessage.setSubject("You received a gift!");
	//			linkedInMessage.setBody(message);
	//			linkedInMessage.setRecipients(new String[] {toSocialIdentifier});
	//			Map<String, String> headerparameters = new HashMap<String, String>();
	//			headerparameters.put("Content-Type", "application/json");
	//			HttpResponse response = HttpUtility.sendPostRequest(url, JsonConverter.getInstance().convertToPayload(linkedInMessage), headerparameters);
	//			
	//			if(response.getStatusLine().getStatusCode() == 201){
	//				return true;
	//			} else {
	//				return false;
	//			}
	//		} catch (Exception ex) {
	//			log.error("failed to post to LinkedIn contact: " + ex.toString());
	//			return false;
	//		}
	//	}

	@Override
	public Contact authenticateUser(SocialIdentity identity) {
		ClientResponse<String> response = null;
		try {
			response = linkedInApi.getProfile(identity.getAccessToken(), "json");
			// convert the raw json into a container
			LinkedInConnectionDto result = jsonConverter.parse(response.getEntity(), LinkedInConnectionDto.class);
			// create a strongly typed list from the generic data container
			Contact contact = LinkedInApiDtoAssembler.assembleContact(result);
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
				contacts.add(LinkedInApiDtoAssembler.assembleContact(connectionDto));
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
