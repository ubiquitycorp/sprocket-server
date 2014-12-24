package com.ubiquity.sprocket.domain.factory;

import com.ubiquity.integration.domain.Activity;
import com.ubiquity.integration.domain.VideoContent;
import com.ubiquity.media.domain.Video;
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

	/***
	 * Creates content with a content id based on whether the video has an item key or a url
	 * @param video
	 * @return
	 */
	public static Content createContent(VideoContent videoContent) {
		
		Video video = videoContent.getVideo();
		String identifier = video.getItemKey() == null ? video.getUrl() : video.getItemKey();
		// create some public content
		Content content = new Content.Builder()
		.videoContent(videoContent)
		.contentId(new ContentPK(videoContent.getExternalNetwork(), identifier))
		.build();
		return content;
	}

}
