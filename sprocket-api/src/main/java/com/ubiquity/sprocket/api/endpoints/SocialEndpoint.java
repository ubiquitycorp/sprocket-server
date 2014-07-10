package com.ubiquity.sprocket.api.endpoints;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.common.serialize.JsonConverter;
import com.niobium.repository.CollectionVariant;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.domain.Message;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.sprocket.api.DtoAssembler;
import com.ubiquity.sprocket.api.dto.containers.ActivitiesDto;
import com.ubiquity.sprocket.api.dto.containers.MessagesDto;
import com.ubiquity.sprocket.service.ServiceFactory;

@Path("/1.0/social")
public class SocialEndpoint {

	@SuppressWarnings("unused")
	private Logger log = LoggerFactory.getLogger(getClass());

	private JsonConverter jsonConverter = JsonConverter.getInstance();


	@GET
	@Path("users/{userId}/providers/{socialNetworkId}/activities")
	@Produces(MediaType.APPLICATION_JSON)
	public Response activities(@PathParam("userId") Long userId, @PathParam("socialNetworkId") Integer socialProviderId, @HeaderParam("If-Modified-Since") Long ifModifiedSince) {
		ActivitiesDto results = new ActivitiesDto();

		ExternalNetwork socialNetwork = ExternalNetwork.getNetworkById(socialProviderId);

		CollectionVariant<Activity> variant = ServiceFactory.getSocialService().findActivityByOwnerIdAndSocialNetwork(userId, socialNetwork, ifModifiedSince);

		// Throw a 304 if if there is no variant (no change)
		if (variant == null)
			return Response.notModified().build();

		
		for(Activity activity : variant.getCollection()) {
			results.getActivities().add(DtoAssembler.assemble(activity));
		}

		return Response.ok()
				.header("Last-Modified", variant.getLastModified())
				.entity(jsonConverter.convertToPayload(results))
				.build();
	}


	
	/***
	 * This method returns messages of specific social network
	 * @param userId
	 * @param socialProviderId
	 * @return
	 */
	@GET
	@Path("users/{userId}/providers/{socialNetworkId}/messages")
	@Produces(MediaType.APPLICATION_JSON)
	public Response messages(@PathParam("userId") Long userId, @PathParam("socialNetworkId") Integer socialProviderId, @HeaderParam("If-Modified-Since") Long ifModifiedSince) {

		MessagesDto result = new MessagesDto();

		ExternalNetwork socialNetwork = ExternalNetwork.getNetworkById(socialProviderId);
					 
		CollectionVariant<Message> variant = ServiceFactory.getSocialService().findMessagesByOwnerIdAndSocialNetwork(userId, socialNetwork, ifModifiedSince);
		
		
		// Throw a 304 if if there is no variant (no change)
		if (variant == null)
			return Response.notModified().build();
		
		
		List<Message> messages = new LinkedList<Message>();
		messages.addAll(variant.getCollection());
		
		// Assemble into message dto, constructing conversations if they are inherent in the data
		result.getMessages().addAll(DtoAssembler.assemble(messages));
	
		return Response.ok()
				.header("Last-Modified", variant.getLastModified())
				.entity(jsonConverter.convertToPayload(result))
				.build();
	}

}
