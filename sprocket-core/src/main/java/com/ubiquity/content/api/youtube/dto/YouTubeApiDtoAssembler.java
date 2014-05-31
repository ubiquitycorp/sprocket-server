package com.ubiquity.content.api.youtube.dto;

import com.ubiquity.content.api.youtube.dto.model.YouTubeVideoDto;
import com.ubiquity.content.api.youtube.dto.model.YouTubeVideoSnippetDto;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.media.domain.Image;
import com.ubiquity.media.domain.Video;
import com.ubiquity.sprocket.domain.VideoContent;

public class YouTubeApiDtoAssembler {

	public static VideoContent assembleVideo(ExternalIdentity socialIdentity,
			YouTubeVideoDto videoDto) {
		
		Video video = new Video.Builder().itemKey(videoDto.getId()).build();
		YouTubeVideoSnippetDto snippet = videoDto.getSnippet();
		VideoContent videoContent = new VideoContent.Builder()
			.videoContentId(1l)
			.title(snippet.getTitle())
			.video(video)
			.thumb(new Image(snippet.getThumbnails().get("default").get("url")))
			.description(snippet.getDescription())
			.owner(socialIdentity.getUser())
			.lastUpdated(System.currentTimeMillis())
			.build();
		return videoContent;
	}

}
