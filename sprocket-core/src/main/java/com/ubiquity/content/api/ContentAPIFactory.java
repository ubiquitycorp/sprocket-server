package com.ubiquity.content.api;

import org.apache.commons.configuration.Configuration;

import com.ubiquity.content.api.youtube.YouTubeAPI;
import com.ubiquity.social.domain.ContentNetwork;

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
	public static ContentAPI createProvider(ContentNetwork type) {
		switch(type) {
		case YouTube:
			return new YouTubeAPI(configuration.getString("social.google.apikey"));
		default:
			throw new UnsupportedOperationException();
		}
		
	}

}
