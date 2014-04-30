package com.ubiquity.social.api;

import com.ubiquity.social.api.youtube.YouTubeAPI;
import com.ubiquity.social.domain.ContentProvider;

public class ContentAPIFactory {
	
	public static ContentAPI createProvider(ContentProvider type) {
		switch(type) {
		case YouTube:
			return new YouTubeAPI();
		default:
			throw new UnsupportedOperationException();
		}
		
	}

}
