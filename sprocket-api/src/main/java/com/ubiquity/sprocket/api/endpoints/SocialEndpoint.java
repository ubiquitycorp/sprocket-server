package com.ubiquity.sprocket.api.endpoints;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.location.domain.UserLocation;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.Message;
import com.ubiquity.social.domain.PostActivity;
import com.ubiquity.sprocket.api.DtoAssembler;
import com.ubiquity.sprocket.api.dto.containers.ActivitiesDto;
import com.ubiquity.sprocket.api.dto.containers.MessagesDto;
import com.ubiquity.sprocket.api.dto.model.ActivityDto;
import com.ubiquity.sprocket.api.dto.model.MessageDto;
import com.ubiquity.sprocket.api.dto.model.SendMessageDto;
import com.ubiquity.sprocket.api.interceptors.Secure;
import com.ubiquity.sprocket.api.validation.EngagementValidation;
import com.ubiquity.sprocket.messaging.MessageConverterFactory;
import com.ubiquity.sprocket.messaging.MessageQueueFactory;
import com.ubiquity.sprocket.messaging.definition.UserEngagedActivity;
import com.ubiquity.sprocket.service.ServiceFactory;

@Path("/1.0/social")
public class SocialEndpoint {

	private Logger log = LoggerFactory.getLogger(getClass());

	private JsonConverter jsonConverter = JsonConverter.getInstance();
	
	@POST
	@Path("/users/{userId}/activities/engaged")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response engaged(@PathParam("userId") Long userId, InputStream payload) throws IOException {

		// convert payload
		ActivitiesDto activitiesDto = jsonConverter.convertFromPayload(payload, ActivitiesDto.class, EngagementValidation.class);
		
		for(ActivityDto activityDto : activitiesDto.getActivities()) {
			log.debug("tracking activity {}", activityDto);
			sendTrackAndSyncMessage(userId, activityDto);			
		}
		return Response.ok().build();
	}
	
	@GET
	@Path("users/{userId}/providers/{socialNetworkId}/activities")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
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
				.header("Last-Modified", variant.lastModified)
				.entity(jsonConverter.convertToPayload(results))
				.build();
	}
	
	@GET
	@Path("users/{userId}/providers/{socialNetworkId}/localfeed")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response getLocalFeed(@PathParam("userId") Long userId, @PathParam("socialNetworkId") Integer socialProviderId, @HeaderParam("If-Modified-Since") Long ifModifiedSince) {
		ActivitiesDto results = new ActivitiesDto();

		ExternalNetwork socialNetwork = ExternalNetwork.getNetworkById(socialProviderId);

		UserLocation userLocation = ServiceFactory.getLocationService().getLocation(userId);
		CollectionVariant<Activity> variant = ServiceFactory.getSocialService().findActivityByPlaceIdAndSocialNetwork(userLocation.getNearestPlace().getPlaceId(), socialNetwork, ifModifiedSince);

		// Throw a 304 if if there is no variant (no change)
		if (variant == null)
			return Response.notModified().build();
		
		for(Activity activity : variant.getCollection()) {
			results.getActivities().add(DtoAssembler.assemble(activity));
		}
		
		return Response.ok()
				.header("Last-Modified", variant.lastModified)
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
	@Secure
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
		List<MessageDto> conversations = DtoAssembler.assemble(messages);
		Collections.sort(conversations, Collections.reverseOrder());
		result.getMessages().addAll(conversations);
	
		return Response.ok()
				.header("Last-Modified", variant.getLastModified())
				.entity(jsonConverter.convertToPayload(result))
				.build();
	}
	/***
	 * This method send message to specific user in social network
	 * @param userId
	 * @param externalNetworkId
	 * @return
	 * @throws org.jets3t.service.impl.rest.HttpException 
	 */
	@POST
	@Path("users/{userId}/providers/{externalNetworkId}/messages")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response sendmessage(@PathParam("userId") Long userId, @PathParam("externalNetworkId") Integer externalNetworkId,InputStream payload) throws org.jets3t.service.impl.rest.HttpException {

		//Cast the input into SendMessageObject
		SendMessageDto sendMessageDto = jsonConverter.convertFromPayload(payload, SendMessageDto.class);
			
		// load user
		ServiceFactory.getUserService().getUserById(userId);
		// get social network 
		ExternalNetwork socialNetwork = ExternalNetwork.getNetworkById(externalNetworkId);
		// get the identity from DB
		ExternalIdentity identity = ServiceFactory.getExternalIdentityService().findExternalIdentity(userId, socialNetwork);
		
		Contact contact = ServiceFactory.getContactService().getByContactId(sendMessageDto.getReceiverId());
		
		ServiceFactory.getSocialService().checkValidityOfExternalIdentity(identity);
		
		ServiceFactory.getSocialService().sendMessage(identity,socialNetwork, contact, sendMessageDto.getReceiverName(), sendMessageDto.getText(), sendMessageDto.getSubject());
	
		return Response.ok().build();
			
	}
	
	/***
	 * This method send message to specific user in social network
	 * @param userId
	 * @param socialProviderId
	 * @return
	 * @throws org.jets3t.service.impl.rest.HttpException 
	 */
	@POST
	@Path("users/{userId}/providers/{socialNetworkId}/activities")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response postactivity(@PathParam("userId") Long userId, @PathParam("socialNetworkId") Integer socialProviderId,InputStream payload) throws org.jets3t.service.impl.rest.HttpException {

		//Cast the input into SendMessageObject
		PostActivity postActivity = jsonConverter.convertFromPayload(payload, PostActivity.class);
				
		// load user
		ServiceFactory.getUserService().getUserById(userId);
		// get social network 
		ExternalNetwork socialNetwork = ExternalNetwork.getNetworkById(socialProviderId);
		// get the identity from DB
		ExternalIdentity identity = ServiceFactory.getExternalIdentityService().findExternalIdentity(userId, socialNetwork);
		
		ServiceFactory.getSocialService().checkValidityOfExternalIdentity(identity);
		
		ServiceFactory.getSocialService().PostActivity(identity, socialNetwork, postActivity);

		return Response.ok().build();
			
	}
	
	/**
	 * Drops a message for tracking this event
	 * 
	 * @param userId
	 * @param activityDto
	 * @throws IOException
	 */
	private void sendTrackAndSyncMessage(Long userId, ActivityDto activityDto) throws IOException {
		
		Activity activity = DtoAssembler.assemble(activityDto);
				
		UserEngagedActivity messageContent = new UserEngagedActivity(userId, activity);
		String message = MessageConverterFactory.getMessageConverter().serialize(new com.ubiquity.messaging.format.Message(messageContent));
		byte[] bytes = message.getBytes();
		MessageQueueFactory.getCacheInvalidationQueueProducer().write(bytes);
	}

}
