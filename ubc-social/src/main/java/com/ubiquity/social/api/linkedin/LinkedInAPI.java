package com.ubiquity.social.api.linkedin;

import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.social.api.Social;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.Event;
import com.ubiquity.social.domain.SocialIdentity;

public class LinkedInAPI implements Social {

	private static Social linkedin = null;
	String consumerKey;
	String consumerSecret;
	String profileUrl = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,formatted-name,email-address,picture-url,public-profile-url)?format=json";
	String scopes = "r_basicprofile r_emailaddress r_network";
	OAuthService service;
	// private String friends_fields =
	// "id,name,first_name,last_name,username,link,picture";
	private Logger log = LoggerFactory.getLogger(getClass());

	private LinkedInAPI() {
		try {
			Configuration configuration = new PropertiesConfiguration("giftsenderapi.properties");
			consumerKey = configuration.getString("social.linkedin.app.consumerKey");
			consumerSecret = configuration.getString("social.linkedin.app.consumerSecret");
			service = new ServiceBuilder().provider(LinkedInApi.class)
					.apiKey(consumerKey).apiSecret(consumerSecret)
					.scope(scopes).build();
		} catch (Exception ex) {
			log.error("linkedIn API: Execption occurs " + ex.getMessage());
		}
	}

	public static Social getProviderAPI() {
		if (linkedin == null)
			linkedin = new LinkedInAPI();
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
//			OAuthRequest request = new OAuthRequest(Verb.GET, profileUrl);
//			Token accessToken = new Token(token, secretToken);
//			service.signRequest(accessToken, request);
//			Response response = request.send();
//			log.debug("Got it! Lets see what we found...");
//			log.debug(response.getCode() + "");
//			log.debug(response.getBody());
//			if (response.getCode() == 200){
//				InputStream is = new ByteArrayInputStream(response.getBody().getBytes());
//				AbstractContact profile = (AbstractContact) JsonConverter
//						.getInstance().convertFromPayload(is, LinkedInConnectionDto.class);
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
//		String contactsUrl = "http://api.linkedin.com/v1/people/~/connections:(id,first-name,last-name,formatted-name,picture-url,public-profile-url)?modified=new&format=json";
//		List<Contact> contactsList = new ArrayList<Contact>();
//		try {
//			Token accessToken = new Token(token, secretToken);
//			OAuthRequest request = new OAuthRequest(Verb.GET, contactsUrl);
//			service.signRequest(accessToken, request);
//			Response response = request.send();
//			String contactsJson = response.getBody();
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
//	public List<Event> findEvents(String token, String secretToken, String socialIdentifier, List<Contact> contact) {
//		throw new UnsupportedOperationException();
//	}
//
//	@Override
//	public Boolean postToWall(String token, String secretToken,
//			String socialIdentifier, String toSocialIdentifier,String message) {
//		throw new UnsupportedOperationException();
//	}

	@Override
	public Contact authenticateUser(
			SocialIdentity identity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<com.ubiquity.social.domain.Contact> findContactsByOwnerIdentity(
			SocialIdentity identity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Event> findEventsCreatedByContacts(SocialIdentity identity, List<Contact> contacts) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Boolean postToWall(SocialIdentity fromIdentity,
			SocialIdentity toIdentity, String message) {
		throw new UnsupportedOperationException();
	}
}
