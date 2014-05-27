package com.ubiquity.sprocket.api.endpoints;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.identity.domain.User;
import com.ubiquity.social.api.ContentAPI;
import com.ubiquity.social.api.ContentAPIFactory;
import com.ubiquity.social.domain.ContentProvider;
import com.ubiquity.social.domain.ExternalIdentity;
import com.ubiquity.social.domain.SocialProvider;
import com.ubiquity.social.domain.VideoContent;
import com.ubiquity.social.service.SocialService;
import com.ubiquity.sprocket.api.dto.model.ImageDto;
import com.ubiquity.sprocket.api.dto.model.VideoDto;
import com.ubiquity.sprocket.service.ServiceFactory;

@Path("/1.0/content")
public class ContentEndpoint {

	private JsonConverter jsonConverter = JsonConverter.getInstance();

	@GET
	@Path("/users/{userId}/providers/{contentProviderId}/videos")
	@Produces(MediaType.APPLICATION_JSON)
	public Response videos(@PathParam("userId") Long userId) {
		
		List<VideoDto> results = new LinkedList<VideoDto>();
		
		// Only supporting YouTube now...
		User user = ServiceFactory.getUserService().getUserById(userId);
		ContentAPI contentApi = ContentAPIFactory.createProvider(ContentProvider.YouTube);

		// Get a google identity; if we don't have one, an illegal argument exception will be thrown
		ExternalIdentity identity = SocialService.getAssociatedSocialIdentity(user, SocialProvider.Google);
		
		List<VideoContent> videos = contentApi.findVideosByExternalIdentity(identity);	
		// Return transformed
		if(videos != null) {
			for(VideoContent videoContent : videos) {
				results.add(new VideoDto.Builder()
				.contentProviderId(ContentProvider.YouTube.ordinal())
				.itemKey(videoContent.getVideo().getItemKey())
				.thumb(new ImageDto(videoContent.getThumb().getUrl()))
				.title(videoContent.getTitle())
				.description(videoContent.getDescription())
				.build());
			}
		}	
		
		// now add to the search index for this user
		ServiceFactory.getSearchService().indexVideos(videos, userId);

		return Response.ok().entity(jsonConverter.convertToPayload(results)).build();
	}

}


