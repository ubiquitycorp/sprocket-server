package com.ubiquity.social.api;

import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.social.api.facebook.FacebookAPI;
import com.ubiquity.social.api.google.GoogleAPI;
import com.ubiquity.social.api.linkedin.LinkedInAPIOAuth1Strategy;
import com.ubiquity.social.api.linkedin.LinkedInAPIOAuth2Strategy;
import com.ubiquity.social.api.yahoo.YahooAPI;
import com.ubiquity.social.domain.SocialProviderType;


/***
 * This class implements Factory design pattern to create
 * Social provider API object depending on the given social type
 * @author peter.tadros 
 */
public class SocialFactory {

	/***
	 * Creates a social provider based on the type and the client platform. The client platform may determine
	 * which strategy to use for providers that have different client SDK implementations.
	 * 
	 * @param providerType
	 * @param client
	 * @return
	 * 
	 * @throws IllegalArgumentException if the provider type is unknown
	 */
	public static Social createProvider(SocialProviderType providerType, ClientPlatform client) {
		Social provider = null;
		switch (providerType) {
		case Facebook:
			provider = FacebookAPI.getProviderAPI();
			break;
		case Yahoo:
			provider = YahooAPI.getProviderAPI();
			break;
		case LinkedIn:
			if (client.equals(ClientPlatform.Android))
				provider = LinkedInAPIOAuth1Strategy.getProviderAPI();
			else if (client.equals(ClientPlatform.IOS))
				provider = LinkedInAPIOAuth2Strategy.getProviderAPI();
			break;
		case Google:
			provider = GoogleAPI.getProviderAPI();
			break;
		default:
			throw new IllegalArgumentException("Unknown provider type");
		}

		return provider;
	}
}
