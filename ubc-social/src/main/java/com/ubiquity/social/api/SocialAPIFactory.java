package com.ubiquity.social.api;

import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.social.api.facebook.FacebookAPI;
import com.ubiquity.social.api.google.GoogleAPI;
import com.ubiquity.social.api.linkedin.LinkedInAPIOAuth1Strategy;
import com.ubiquity.social.api.linkedin.LinkedInAPIOAuth2Strategy;
import com.ubiquity.social.domain.SocialNetwork;


/***
 * This class implements Factory design pattern to create
 * Social provider API object depending on the given social type
 * @author peter.tadros 
 */
public class SocialAPIFactory {

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
	public static SocialAPI createProvider(SocialNetwork providerType, ClientPlatform client) {
		SocialAPI provider = null;
		switch (providerType) {
		case Facebook:
			provider = FacebookAPI.getProviderAPI();
			break;
		case Yahoo:
			throw new UnsupportedOperationException();
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
