package com.ubiquity.social.api.youtube;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.social.api.ContentAPI;
import com.ubiquity.social.api.google.dto.container.GoogleRequestFailureDto;
import com.ubiquity.social.api.youtube.dto.YouTubeApiDtoAssembler;
import com.ubiquity.social.api.youtube.dto.container.YouTubeItemsDto;
import com.ubiquity.social.api.youtube.dto.model.YouTubeVideoDto;
import com.ubiquity.social.api.youtube.endpoints.YouTubeApiEndpoints;
import com.ubiquity.social.domain.ExternalIdentity;
import com.ubiquity.social.domain.VideoContent;

public class YouTubeAPI implements ContentAPI {

	private YouTubeApiEndpoints youTubeApi;
	private String apiKey;
	private JsonConverter jsonConverter = JsonConverter.getInstance();

	private Logger log = LoggerFactory.getLogger(getClass());
	
	public YouTubeAPI() {
		
		// TOOD: remove me and put me in a service :/
		try {
			Configuration configuration = new PropertiesConfiguration(
					"sprocketapi.properties");
			apiKey = configuration.getString("social.google.apikey");
			
		} catch (ConfigurationException e) {
			throw new RuntimeException("Unable to configure access to YouTube");
		}
		
		log.info("Using api key: {}", apiKey);

		// this initialization only needs to be done once per VM
		RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
		youTubeApi = ProxyFactory.create(YouTubeApiEndpoints.class, "https://www.googleapis.com/youtube");
	}
	
	@Override
	public List<VideoContent> findVideosByExternalIdentity(
			ExternalIdentity externalIdentity) {
		List<VideoContent> videos = new LinkedList<VideoContent>();
		ClientResponse<String> response = null;
		try {
			log.info("Access ing YouTube with api key: {} and token: {}", apiKey, externalIdentity.getAccessToken());
			response = youTubeApi.getVideos("snippet", "mostPopular", apiKey, "  Bearer " + externalIdentity.getAccessToken());
			checkError(response);
			
			YouTubeItemsDto result = jsonConverter.parse(response.getEntity(), YouTubeItemsDto.class);
			
			List<YouTubeVideoDto> videoDtoList = jsonConverter.convertToListFromList(result.getItems(), YouTubeVideoDto.class);
			for(YouTubeVideoDto videoDto : videoDtoList) {
				VideoContent videoContent = YouTubeApiDtoAssembler.assembleVideo(externalIdentity, videoDto);
				videos.add(videoContent);
			}
		} finally {
			if(response != null)
				response.releaseConnection();
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
