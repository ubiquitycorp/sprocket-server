package com.ubiquity.sprocket.domain.factory;

import com.ubiquity.integration.domain.Activity;
import com.ubiquity.sprocket.domain.Content;
import com.ubiquity.sprocket.domain.ContentPK;

public class ContentFactory {
	
	public static Content createContent(Activity activity) {
		// create some public content
		Content content = new Content.Builder()
			.activity(activity)
			.contentId(new ContentPK(activity.getExternalNetwork(), activity.getExternalIdentifier()))
			.build();
		return content;
	}

}
