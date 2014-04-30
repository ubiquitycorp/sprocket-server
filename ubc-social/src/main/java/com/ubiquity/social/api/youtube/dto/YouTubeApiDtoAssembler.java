package com.ubiquity.social.api.youtube.dto;

import com.ubiquity.media.domain.Image;
import com.ubiquity.media.domain.Video;
import com.ubiquity.social.api.youtube.dto.model.YouTubeVideoDto;
import com.ubiquity.social.domain.ExternalIdentity;
import com.ubiquity.social.domain.VideoContent;

public class YouTubeApiDtoAssembler {

	public static VideoContent assembleVideo(ExternalIdentity socialIdentity,
			YouTubeVideoDto videoDto) {
		
		Video video = new Video.Builder().itemKey(videoDto.getId()).build();
		VideoContent videoContent = new VideoContent.Builder()
			.title(videoDto.getTitle())
			.video(video)
			.thumb(new Image(videoDto.getThumbnails().get("default").get("url")))
			.description(videoDto.getDescription())
			.identity(socialIdentity)
			.lastUpdated(System.currentTimeMillis())
			.build();
		return videoContent;
	}

}
