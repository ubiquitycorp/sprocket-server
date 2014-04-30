package com.ubiquity.social.api.youtube;

import java.util.LinkedList;
import java.util.List;

import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.social.api.ContentAPI;
import com.ubiquity.social.api.youtube.dto.YouTubeApiDtoAssembler;
import com.ubiquity.social.api.youtube.dto.container.YouTubeItemsDto;
import com.ubiquity.social.api.youtube.dto.model.YouTubeVideoDto;
import com.ubiquity.social.api.youtube.endpoints.YouTubeApiEndpoints;
import com.ubiquity.social.domain.ExternalIdentity;
import com.ubiquity.social.domain.VideoContent;

public class YouTubeAPI implements ContentAPI {

	private YouTubeApiEndpoints youTubeApi;
	
	private JsonConverter jsonConverter = JsonConverter.getInstance();

	
	@Override
	public List<VideoContent> findVideosByExternalIdentity(
			ExternalIdentity externalIdentity) {
		List<VideoContent> videos = new LinkedList<VideoContent>();
		ClientResponse<String> response = null;
		try {
			response = youTubeApi.getVideos("snippet", "mostPopular", "AIzaSyCSxTEjWrO-WuY3vwBT9DRYXgFWvNXdd0I", "  Bearer ya29.1.AADtN_UjxeU5_Ec3KazgyDUgAivVwaO1_dIYLhBBUBGnkd63WkCHJg8woc0x0G4");
			
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

	public YouTubeAPI() {
		// this initialization only needs to be done once per VM
		RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
		youTubeApi = ProxyFactory.create(YouTubeApiEndpoints.class, "https://www.googleapis.com/youtube");
	}

}
