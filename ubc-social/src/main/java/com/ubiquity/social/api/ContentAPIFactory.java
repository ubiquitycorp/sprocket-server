package com.ubiquity.social.api;

import com.ubiquity.social.api.youtube.YouTubeAPI;
import com.ubiquity.social.domain.ContentNetwork;

/***
 * Creates implementations of the ContentAPI interface
 * 
 * @author chris
 *
 */
public class ContentAPIFactory {
	
	/***
	 * 
	 * @param type
	 * @return
	 * @throws UnsupportedOperationException if the network is not supported
	 */
	public static ContentAPI createProvider(ContentNetwork type) {
		switch(type) {
		case YouTube:
			return new YouTubeAPI();
		default:
			throw new UnsupportedOperationException();
		}
		
	}

}
