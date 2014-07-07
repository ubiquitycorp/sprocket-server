package com.ubiquity.sprocket.api.endpoints;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.niobium.common.serialize.JsonConverter;
import com.niobium.repository.CollectionVariant;
import com.ubiquity.content.domain.ContentNetwork;
import com.ubiquity.sprocket.api.DtoAssembler;
import com.ubiquity.sprocket.api.dto.containers.VideosDto;
import com.ubiquity.content.domain.VideoContent;
import com.ubiquity.sprocket.service.ServiceFactory;

@Path("/1.0/content")
public class ContentEndpoint {

	private JsonConverter jsonConverter = JsonConverter.getInstance();

	@GET
	@Path("/users/{userId}/providers/{contentNetworkId}/videos")
	@Produces(MediaType.APPLICATION_JSON)
	public Response videos(@PathParam("userId") Long userId, @PathParam("contentNetworkId") Integer contentNetworkId, @HeaderParam("If-Modified-Since") Long ifModifiedSince) {
		
		VideosDto results = new VideosDto();
		
		ContentNetwork contentNetwork = ContentNetwork.getContentNetworkFromId(contentNetworkId);
		CollectionVariant<VideoContent> variant = ServiceFactory.getContentService().findAllVideosByOwnerIdAndContentNetwork(userId, contentNetwork, ifModifiedSince);

		// Throw a 304 if if there is no variant (no change)
		if (variant == null)
			return Response.notModified().build();
		
		for(VideoContent videoContent : variant.getCollection())
			results.getVideos().add(DtoAssembler.assemble(videoContent));

		return Response.ok().header("Last-Modified", variant.getLastModified()).entity(jsonConverter.convertToPayload(results)).build();
	}

}


