package com.ubiquity.sprocket.api.endpoints;

import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.common.serialize.JsonConverter;
import com.niobium.repository.CollectionVariant;
import com.ubiquity.content.domain.VideoContent;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.sprocket.api.DtoAssembler;
import com.ubiquity.sprocket.api.dto.containers.VideosDto;
import com.ubiquity.sprocket.service.ServiceFactory;

@Path("/1.0/content")
public class ContentEndpoint {

	private JsonConverter jsonConverter = JsonConverter.getInstance();

	private Logger log = LoggerFactory.getLogger(getClass());

	@POST
	@Path("/users/{userId}/videos/engaged")
	@Produces(MediaType.APPLICATION_JSON)
	public Response engaged(@PathParam("userId") Long userId, @PathParam("externalNetworkId") Integer externalNetworkId, InputStream payload) {

		// convert payload
		VideosDto videosDto = jsonConverter.convertFromPayload(payload, VideosDto.class);
		log.debug("videos engaged {}", videosDto);
		
		return Response.ok().build();
	}
	
	@GET
	@Path("/users/{userId}/providers/{externalNetworkId}/videos")
	@Produces(MediaType.APPLICATION_JSON)
	public Response videos(@PathParam("userId") Long userId, @PathParam("externalNetworkId") Integer externalNetworkId, @HeaderParam("If-Modified-Since") Long ifModifiedSince) {

		VideosDto results = new VideosDto();

		ExternalNetwork externalNetwork = ExternalNetwork.getNetworkById(externalNetworkId);
		CollectionVariant<VideoContent> variant = ServiceFactory.getContentService().findAllVideosByOwnerIdAndContentNetwork(userId, externalNetwork, ifModifiedSince);

		// Throw a 304 if if there is no variant (no change)
		if (variant == null)
			return Response.notModified().build();

		for(VideoContent videoContent : variant.getCollection())
			results.getVideos().add(DtoAssembler.assemble(videoContent));

		return Response.ok().header("Last-Modified", variant.getLastModified()).entity(jsonConverter.convertToPayload(results)).build();
	}

}


