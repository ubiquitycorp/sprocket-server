package com.ubiquity.content.api.youtube;

import java.util.LinkedList;
import java.util.List;

import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.content.api.ContentAPI;
import com.ubiquity.content.api.youtube.dto.YouTubeApiDtoAssembler;
import com.ubiquity.content.api.youtube.dto.container.YouTubeItemsDto;
import com.ubiquity.content.api.youtube.dto.model.YouTubeVideoDto;
import com.ubiquity.content.api.youtube.endpoints.YouTubeApiEndpoints;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.social.api.ClientExecutorFactory;
import com.ubiquity.social.api.google.dto.container.GoogleRequestFailureDto;
import com.ubiquity.sprocket.domain.VideoContent;

public class YouTubeAPI implements ContentAPI {

	private YouTubeApiEndpoints youTubeApi;
	private String apiKey;
	private JsonConverter jsonConverter = JsonConverter.getInstance();

	private Logger log = LoggerFactory.getLogger(getClass());
	
	public YouTubeAPI(String apiKey) {
		
		this.apiKey = apiKey;
		youTubeApi = ProxyFactory.create(YouTubeApiEndpoints.class, "https://www.googleapis.com/youtube", ClientExecutorFactory.createClientExecutor());
		log.debug("using api key: " + apiKey);
	}
	
	@Override
	public List<VideoContent> findVideosByExternalIdentity(
			ExternalIdentity externalIdentity) {
		List<VideoContent> videos = new LinkedList<VideoContent>();
		ClientResponse<String> response = null;
		try {
			response = (ClientResponse<String>) youTubeApi.getVideos("snippet", "mostPopular", apiKey, "  Bearer " + externalIdentity.getAccessToken());
			checkError(response);
			
			YouTubeItemsDto result = jsonConverter.parse(response.getEntity(), YouTubeItemsDto.class);
			
			List<YouTubeVideoDto> videoDtoList = jsonConverter.convertToListFromList(result.getItems(), YouTubeVideoDto.class);
			for(YouTubeVideoDto videoDto : videoDtoList) {
				VideoContent videoContent = YouTubeApiDtoAssembler.assembleVideo(externalIdentity, videoDto);
				videos.add(videoContent);
			}
		} finally {
			if(response != null) {
				response.releaseConnection();
			}
		}
		
		return videos;
	}
	
	private String getErrorMessage(ClientResponse<String> response) {
		String errorMessage = null;
		String errorBody = response.getEntity();
		if(errorBody != null) {
			GoogleRequestFailureDto failure = jsonConverter.parse(errorBody, GoogleRequestFailureDto.class);
			errorMessage = failure.getError().getMessage();
		} else {
			errorMessage = "Unable to authenticate with provided credentials";
		}
		return errorMessage;
	}
	
	private void checkError(ClientResponse<String> response) {
		if(response.getResponseStatus().getStatusCode() != 200) {
			throw new RuntimeException(getErrorMessage(response));
		}
	}

	

}
