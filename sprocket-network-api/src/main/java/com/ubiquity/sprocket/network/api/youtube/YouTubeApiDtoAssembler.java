package com.ubiquity.sprocket.network.api.youtube;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ubiquity.sprocket.network.api.dto.model.Category;
import com.ubiquity.sprocket.network.api.dto.model.VideoContent;
import com.ubiquity.sprocket.network.api.youtube.dto.model.YouTubeCompositeKeyDto;
import com.ubiquity.sprocket.network.api.youtube.dto.model.YouTubeSearchResultDto;
import com.ubiquity.sprocket.network.api.youtube.dto.model.YouTubeVideoDto;
import com.ubiquity.sprocket.network.api.youtube.dto.model.YouTubeVideoSnippetDto;

public class YouTubeApiDtoAssembler {

	public static YouTubeVideoDto assembleVideo(VideoContent videoContent,
			Category category) {
		YouTubeVideoDto.Builder youTubeVideoDto = new YouTubeVideoDto.Builder();
		YouTubeVideoSnippetDto.Builder youTubeVideoSnippetDto = new YouTubeVideoSnippetDto.Builder();

		if (category == Category.MyHistory) {
			Map<String, String> resourceId = new HashMap<String, String>();
			resourceId.put("videoId", videoContent.getVideo().getItemKey());
			youTubeVideoSnippetDto.resourceId(resourceId);
		} else if (category == Category.Subscriptions) {
			Map<String, String> resourceId = new HashMap<String, String>();
			resourceId.put("videoId", videoContent.getVideo().getItemKey());

			youTubeVideoSnippetDto.resourceId(resourceId).publishedAt(
					ConvertDateToString(videoContent.getPublishedAt()));
		} else {
			youTubeVideoDto.id(videoContent.getVideo().getItemKey());
		}
		Map<String, Map<String, String>> thumbnails = new HashMap<String, Map<String, String>>();
		Map<String, String> url = new HashMap<String, String>();
		url.put("url", videoContent.getThumb().getItemKey());
		thumbnails.put("default", url);
		return youTubeVideoDto.snippet(
				youTubeVideoSnippetDto
						.title(videoContent.getTitle())
						.categoryId(
								videoContent.getCategoryExternalIdentifier())
						.thumbnails(thumbnails)
						.description(videoContent.getDescription()).build())
				.build();

	}

	public static YouTubeSearchResultDto assembleVideo(VideoContent videoContent) {

		YouTubeSearchResultDto.Builder youTubeSearchResultDto = new YouTubeSearchResultDto.Builder();
		youTubeSearchResultDto.id(new YouTubeCompositeKeyDto.Builder().videoId(
				videoContent.getVideo().getItemKey()).build());

		Map<String, Map<String, String>> thumbnails = new HashMap<String, Map<String, String>>();
		Map<String, String> url = new HashMap<String, String>();
		url.put("url", videoContent.getThumb().getItemKey());
		thumbnails.put("default", url);

		YouTubeVideoSnippetDto snippet = new YouTubeVideoSnippetDto.Builder()
				.title(videoContent.getTitle())
				.categoryId(videoContent.getCategoryExternalIdentifier())
				.description(videoContent.getDescription()).build();
		return youTubeSearchResultDto.snippet(snippet).build();
	}

	private static String ConvertDateToString(Long time) {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date d = new Date(time);
		return f.format(d);
	}

}
