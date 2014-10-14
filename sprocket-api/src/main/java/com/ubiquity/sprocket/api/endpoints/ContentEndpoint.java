package com.ubiquity.sprocket.api.endpoints;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.niobium.common.serialize.JsonConverter;
import com.niobium.repository.CollectionVariant;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.integration.domain.Category;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.VideoContent;
import com.ubiquity.sprocket.api.DtoAssembler;
import com.ubiquity.sprocket.api.dto.containers.VideosDto;
import com.ubiquity.sprocket.api.dto.model.VideoDto;
import com.ubiquity.sprocket.api.interceptors.Secure;
import com.ubiquity.sprocket.api.validation.EngagementValidation;
import com.ubiquity.sprocket.messaging.MessageConverterFactory;
import com.ubiquity.sprocket.messaging.MessageQueueFactory;
import com.ubiquity.sprocket.messaging.definition.UserEngagedVideo;
import com.ubiquity.sprocket.service.ServiceFactory;

@Path("/1.0/content")
public class ContentEndpoint {

	private JsonConverter jsonConverter = JsonConverter.getInstance();

	@POST
	@Path("/users/{userId}/videos/engaged")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response engaged(@PathParam("userId") Long userId, InputStream payload) throws IOException {

		// convert payload
		VideosDto videosDto = jsonConverter.convertFromPayload(payload, VideosDto.class, EngagementValidation.class);
		for(VideoDto videoDto : videosDto.getVideos()) {
			sendTrackAndSyncMessage(userId, videoDto);
		}
		return Response.ok().build();
	}
	
	@GET
	@Path("/users/{userId}/providers/{externalNetworkId}/videos")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response videos(@PathParam("userId") Long userId, @PathParam("externalNetworkId") Integer externalNetworkId, @HeaderParam("delta") Boolean delta, @HeaderParam("If-Modified-Since") Long ifModifiedSince) {

		VideosDto results = new VideosDto();
		
		ExternalNetwork externalNetwork = ExternalNetwork.getNetworkById(externalNetworkId);
		CollectionVariant<VideoContent> variant = ServiceFactory.getContentService().findAllVideosByOwnerIdAndContentNetwork(userId, externalNetwork, ifModifiedSince,delta);

		// Throw a 304 if if there is no variant (no change)
		if (variant == null)
			return Response.notModified().build();
		
		boolean history = false;
		for(VideoContent videoContent : variant.getCollection())
		{
			VideoDto videoDto = DtoAssembler.assemble(videoContent);
			if(videoDto.getCategory()== Category.MyHistory.getCategoryName())
				history = true;
			results.getVideos().add(videoDto);
		}
		if(delta == null || !delta)
		{
			history =true;
		}
		if(!history && externalNetwork == ExternalNetwork.YouTube)
		{
			ExternalIdentity identity = ServiceFactory.getExternalIdentityService().findExternalIdentity(userId, externalNetwork);
			if (!identity.getEmail().toLowerCase().contains("@gmail"))
				results.setHistoryEmptyMessage("Please note that YouTube doesn't allow retrieving history if you log in with a service account");
		}
		return Response.ok().header("Last-Modified", variant.getLastModified()).entity(jsonConverter.convertToPayload(results)).build();
	}
	
	/**
	 * Drops a message for tracking this event
	 * 
	 * @param userId
	 * @param activityDto
	 * @throws IOException
	 */
	private void sendTrackAndSyncMessage(Long userId, VideoDto videoDto) throws IOException {
		
		VideoContent video = DtoAssembler.assemble(videoDto);
		
		// create message content with strongly typed references to the actual domain entity (for easier de-serialization on the consumer end)
		UserEngagedVideo messageContent = new UserEngagedVideo(userId, video);
		
		// convert to raw bytes and send it off
		String message = MessageConverterFactory.getMessageConverter().serialize(new com.ubiquity.messaging.format.Message(messageContent));
		byte[] bytes = message.getBytes();
		
		// will ensure the domain entity gets saved to the store if it does not exist and indexed for faster search
		MessageQueueFactory.getCacheInvalidationQueueProducer().write(bytes);


	}

}


