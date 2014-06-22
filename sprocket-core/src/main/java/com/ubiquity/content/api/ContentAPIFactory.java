package com.ubiquity.content.api;

import org.apache.commons.configuration.Configuration;

import com.ubiquity.content.api.youtube.YouTubeAPI;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.sprocket.domain.ContentNetwork;

/***
 * Creates implementations of the ContentAPI interface
 * 
 * @author chris
 *
 */
public class ContentAPIFactory {
	
	private static Configuration configuration;
	
	public static void initialize(Configuration conf) {
		configuration = conf;
	}
	
	/***
	 * 
	 * @param type
	 * @return
	 * @throws UnsupportedOperationException if the network is not supported
	 */
	public static ContentAPI createProvider(ContentNetwork type, ClientPlatform platform) {
		switch(type) {
		case YouTube:
			String apiKey = "";
			if(platform.equals(ClientPlatform.Android))
				apiKey = configuration.getString("social.google.android.apikey");
			else if(platform.equals(ClientPlatform.IOS))
				apiKey = configuration.getString("social.google.ios.apikey");
			else if(platform.equals(ClientPlatform.WEB))
				apiKey = configuration.getString("social.google.web.apikey");
			
			return new YouTubeAPI(apiKey);
		default:
			throw new UnsupportedOperationException();
		}
		
	}

}
