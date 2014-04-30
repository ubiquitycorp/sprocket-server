package com.ubiquity.sprocket.api.endpoints;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.social.domain.ContentProvider;
import com.ubiquity.sprocket.api.dto.model.ImageDto;
import com.ubiquity.sprocket.api.dto.model.VideoDto;

@Path("/1.0/content")
public class ContentEndpoint {

	private JsonConverter jsonConverter = JsonConverter.getInstance();
	
	@GET
	@Path("/videos")
	@Produces(MediaType.APPLICATION_JSON)
	public Response ping() {
		List<VideoDto> videos = new LinkedList<VideoDto>();
		
		videos.add(new VideoDto.Builder().contentProviderId(ContentProvider.YouTube.ordinal()).itemKey("").thumb(new ImageDto("")).build());
		videos.add(new VideoDto.Builder().contentProviderId(ContentProvider.YouTube.ordinal()).itemKey("").thumb(new ImageDto("")).build());
		videos.add(new VideoDto.Builder().contentProviderId(ContentProvider.YouTube.ordinal()).itemKey("").thumb(new ImageDto("")).build());
		videos.add(new VideoDto.Builder().contentProviderId(ContentProvider.YouTube.ordinal()).itemKey("").thumb(new ImageDto("")).build());
		videos.add(new VideoDto.Builder().contentProviderId(ContentProvider.YouTube.ordinal()).itemKey("").thumb(new ImageDto("")).build());
		videos.add(new VideoDto.Builder().contentProviderId(ContentProvider.YouTube.ordinal()).itemKey("").thumb(new ImageDto("")).build());

		return Response.ok().entity(jsonConverter.convertToPayload(videos)).build();
	}
	
}


