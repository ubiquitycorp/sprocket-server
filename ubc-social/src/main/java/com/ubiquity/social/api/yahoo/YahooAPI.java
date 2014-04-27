package com.ubiquity.social.api.yahoo;

import java.util.List;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.YahooApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import com.google.gson.JsonElement;
import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.social.api.Social;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.Event;
import com.ubiquity.social.domain.SocialIdentity;

/***
 * Yahoo API Class
 * 
 * @author Peter
 * 
 */
public class YahooAPI implements Social {

	private static Social yahoo = null;
	// consumer key and consumer secret of Gift sender app
	String consumerKey = "dj0yJmk9dnhSdzFjR0N0T2p6JmQ9WVdrOU1teHpWRlZYTjJFbWNHbzlNVE0wTlRrMU56azJNZy0tJnM9Y29uc3VtZXJzZWNyZXQmeD1iOQ--";
	String consumerSecret = "573d1d3cd61985fe6fae834383b447bcc71e765c";
	OAuthService service;

	private final String GUID_URI = "http://social.yahooapis.com/v1/me/guid?format=JSON";

	private YahooAPI() {
		service = new ServiceBuilder().provider(YahooApi.class)
				.apiKey(consumerKey).apiSecret(consumerSecret).build();

	}

	public static Social getProviderAPI() {
		if (yahoo == null)
			yahoo = new YahooAPI();
		return yahoo;
	}

	// will be used after creating Factory design pattern for social providers
	/*
	 * private YahooAPI(String consumer_key, String consumer_secret) {
	 * this.consumerKey = consumer_key; this.consumerSecret = consumer_secret;
	 * service = new ServiceBuilder().provider(YahooApi.class)
	 * .apiKey(consumerKey).apiSecret(consumerSecret).build();
	 * 
	 * }
	 * 
	 * public static YahooAPI getYahooAPI() { if (yahoo == null) yahoo = new
	 * YahooAPI(configuration.getString("social.yahoo.app.consumerKey"),
	 * configuration.getString("social.yahoo.app.consumerSecret")); return
	 * yahoo; }
	 */

//	/***
//	 * This method authenticate user to Yahoo using given token and secret token
//	 * returns true if successfully connects to Yahoo and gets user guid, false
//	 * if user is not authenticated or expired session token
//	 * 
//	 * @param token
//	 *            , @param secretToken
//	 * @return
//	 */
//	@Override
//	public Contact authenticateUser(String token, String secretToken) {
//		try {
//			String guid = getGUID(token, secretToken);
//			if (!guid.isEmpty())
//				return null;
//			else
//				return null;
//		} catch (Exception ex) {
//			System.out.println("failed to send authentication request: "
//					+ ex.toString());
//			return null;
//		}
//	}

	public boolean authorizeUser(String oauth_token, String oauth_verifier) {
		// TODO Auto-generated method stub
		return false;
	}

	/***
	 * This method retrieves the GUID of user returns GUID as string
	 * 
	 * @return
	 */
	public String getGUID(String token, String secretToken) {
		Token accessToken = new Token(token, secretToken);
		String guid = "";
		OAuthRequest request = new OAuthRequest(Verb.GET, GUID_URI);
		service.signRequest(accessToken, request);
		Response response = request.send();
		System.out.println("Got it! Lets see what we found...");
		System.out.println(response.getCode());
		System.out.println(response.getBody());
		if (response.getCode() == 200) {
			JsonElement jsonObj = JsonConverter.getInstance().parse(
					response.getBody(), JsonElement.class);
			guid = jsonObj.getAsJsonObject().get("guid").getAsJsonObject()
					.get("value").getAsString();
		}
		System.out.println("guid =" + guid);
		return guid;
	}

//	/***
//	 * This method retrieves user contacts. No return till now (not completed)
//	 * returns
//	 * 
//	 * @return
//	 */
//	@Override
//	public List<Contact> getContacts(String token, String secretToken,
//			Long userId) {
//		String Contacts_URI = "http://social.yahooapis.com/v1/user/%s/contacts?format=JSON";
//		Token accessToken = new Token(token, secretToken);
//		String GUID = getGUID(token, secretToken);
//		OAuthRequest request = new OAuthRequest(Verb.GET, String.format(Contacts_URI, GUID));
//		service.signRequest(accessToken, request);
//		Response response = request.send();
//		System.out.println("Got it! Lets see what we found...");
//		System.out.println(response.getCode());
//		System.out.println(response.getBody());
//		return null;
//	}

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
	public Contact authenticateUser(SocialIdentity identity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Contact> findContactsByOwnerIdentity(SocialIdentity identity) {
		throw new UnsupportedOperationException();
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
