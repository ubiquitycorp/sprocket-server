package com.ubiquity.sprocket.network.api.youtube;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ubiquity.sprocket.network.api.dto.model.Category;
import com.ubiquity.sprocket.network.api.dto.model.VideoContent;
import com.ubiquity.sprocket.network.api.random.generator.RandomListGenerator;
import com.ubiquity.sprocket.network.api.youtube.dto.container.YouTubeItemsDto;
import com.ubiquity.sprocket.network.api.youtube.dto.model.YouTubeVideoDto;
import com.ubiquity.sprocket.network.api.youtube.dto.model.YouTubeVideoSnippetDto;

public class YoutubeMockNetwork {

	public static YouTubeItemsDto getVideos(Long userId, Long lastRequest,
			Long thisRequest, Category category,Integer maxResults) {
		YouTubeItemsDto youTubeItemsDto = new YouTubeItemsDto();
		List<VideoContent> videos = RandomListGenerator.GenerateVideoList(
				userId, lastRequest, thisRequest,maxResults);
		for (VideoContent videoContent : videos)
			youTubeItemsDto.getItems().add(
					YouTubeApiDtoAssembler
							.assembleVideo(videoContent, category));
		return youTubeItemsDto;
	}
	
	public static YouTubeItemsDto searchVideos(Long userId, Long lastRequest,
			Long thisRequest, Integer maxResults) {
		YouTubeItemsDto youTubeItemsDto = new YouTubeItemsDto();
		List<VideoContent> videos = RandomListGenerator.GenerateVideoList(
				userId, lastRequest, thisRequest,maxResults);
		for (VideoContent videoContent : videos)
			youTubeItemsDto.getItems().add(
					YouTubeApiDtoAssembler
							.assembleVideo(videoContent));
		youTubeItemsDto.setPaging("testEtag", "NextPageToken", "PrevPageToken");
		return youTubeItemsDto;
	}
	
	public static YouTubeItemsDto getChannels(int num) {
		YouTubeItemsDto youTubeItemsDto = new YouTubeItemsDto();
		YouTubeVideoDto youTubeVideoDto = new YouTubeVideoDto.Builder().build();

		youTubeVideoDto.getContentDetails().getRelatedPlaylists()
				.setWatchHistory("sodjsilajdls");
		youTubeItemsDto.getItems().add(youTubeVideoDto);
		youTubeItemsDto.setEtag("sdkjasldjks");
		return youTubeItemsDto;
	}

	public static YouTubeItemsDto getSubscriptions(int num) {
		YouTubeItemsDto youTubeItemsDto = new YouTubeItemsDto();
		for (int i = 0; i < num; i++) {
			youTubeItemsDto.getItems().add(getSubscription());
		}
		return youTubeItemsDto;
	}

	private static YouTubeVideoDto getSubscription() {
		Map<String, String> resourceId = new HashMap<String, String>();
		resourceId.put("channelId", "dssdsaddsfdskfj");
		return new YouTubeVideoDto.Builder().snippet(
				new YouTubeVideoSnippetDto.Builder().resourceId(resourceId)
						.build()).build();
	}
}
