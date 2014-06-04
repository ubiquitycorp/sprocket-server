package com.ubiquity.content.api.youtube;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
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
import com.ubiquity.social.api.JsonContentTypeResponseFilter;
import com.ubiquity.social.api.google.dto.container.GoogleRequestFailureDto;
import com.ubiquity.sprocket.domain.VideoContent;

public class YouTubeAPI implements ContentAPI {

	private YouTubeApiEndpoints youTubeApi;
	private String apiKey;
	private JsonConverter jsonConverter = JsonConverter.getInstance();

	private Logger log = LoggerFactory.getLogger(getClass());
	
	public YouTubeAPI(String apiKey) {
		
		this.apiKey = apiKey;
		
		 Client client = ClientBuilder.newBuilder().register(JsonContentTypeResponseFilter.class).build();
         WebTarget target = client.target("https://www.googleapis.com/youtube");
         ResteasyWebTarget rtarget = (ResteasyWebTarget)target;
         
         youTubeApi = rtarget.proxy(YouTubeApiEndpoints.class);

         
		
		//youTubeApi = ProxyFactory.create(YouTubeApiEndpoints.class, "https://www.googleapis.com/youtube", ClientExecutorFactory.createClientExecutor());
		log.debug("using api key: " + apiKey);
	
	
	}
	
	public List<VideoContent> findVideosByExternalIdentity(
			ExternalIdentity externalIdentity) {
		List<VideoContent> videos = new LinkedList<VideoContent>();
		Response response = null;
		try {
			response = youTubeApi.getVideos("snippet", "mostPopular", apiKey, "  Bearer " + externalIdentity.getAccessToken());
			checkError(response);
			
			YouTubeItemsDto result = jsonConverter.parse(response.readEntity(String.class), YouTubeItemsDto.class);
			
			List<YouTubeVideoDto> videoDtoList = jsonConverter.convertToListFromList(result.getItems(), YouTubeVideoDto.class);
			for(YouTubeVideoDto videoDto : videoDtoList) {
				VideoContent videoContent = YouTubeApiDtoAssembler.assembleVideo(externalIdentity, videoDto);
				videos.add(videoContent);
			}
		} finally {
			if(response != null) {
				response.close();
			}
		}
		
		return videos;
	}
	
	private String getErrorMessage(Response response) {
		String errorMessage = null;
		String errorBody = response.readEntity(String.class);
		if(errorBody != null) {
			GoogleRequestFailureDto failure = jsonConverter.parse(errorBody, GoogleRequestFailureDto.class);
			errorMessage = failure.getError().getMessage();
		} else {
			errorMessage = "Unable to authenticate with provided credentials";
		}
		return errorMessage;
	}
	
	private void checkError(Response response) {
		if(response.getStatus() != 200) {
			throw new RuntimeException(getErrorMessage(response));
		}
	}

	

}
