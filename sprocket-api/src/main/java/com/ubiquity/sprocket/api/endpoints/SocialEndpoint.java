package com.ubiquity.sprocket.api.endpoints;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.service.UserService;
import com.ubiquity.social.api.SocialAPI;
import com.ubiquity.social.api.SocialAPIFactory;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.domain.Message;
import com.ubiquity.social.domain.SocialNetwork;
import com.ubiquity.social.service.SocialService;
import com.ubiquity.sprocket.api.DtoAssembler;
import com.ubiquity.sprocket.api.dto.containers.ActivitiesDto;
import com.ubiquity.sprocket.api.dto.containers.MessagesDto;
import com.ubiquity.sprocket.api.dto.model.ActivityDto;
import com.ubiquity.sprocket.service.ServiceFactory;

@Path("/1.0/social")
public class SocialEndpoint {

	@SuppressWarnings("unused")
	private Logger log = LoggerFactory.getLogger(getClass());

	private JsonConverter jsonConverter = JsonConverter.getInstance();


	@GET
	@Path("users/{userId}/providers/{socialProviderId}/activities")
	@Produces(MediaType.APPLICATION_JSON)
	public Response activities(@PathParam("userId") Long userId, @PathParam("socialProviderId") Integer socialProviderId) {
		ActivitiesDto results = new ActivitiesDto();

		SocialNetwork socialProvider = SocialNetwork.getEnum(socialProviderId);

		UserService userService = ServiceFactory.getUserService();
		User user = userService.getUserById(userId);

		SocialAPI socialApi = SocialAPIFactory.createProvider(socialProvider, user.getClientPlatform());
		ExternalIdentity identity = SocialService.getAssociatedSocialIdentity(user, socialProvider);
		
		
		List<Activity> activities = socialApi.listActivities(identity);
					for(Activity activity : activities) {
						results.getActivities().add(DtoAssembler.assemble(activity, socialProvider));
					}
			
	ServiceFactory.getSearchService().indexActivities(activities);


		return Response.ok()
				.entity(jsonConverter.convertToPayload(results))
				.build();
	}

	@GET
	@Path("users/{userId}/providers/{socialProviderId}/messages")
	@Produces(MediaType.APPLICATION_JSON)
	public Response messages(@PathParam("userId") Long userId, @PathParam("socialProviderId") Integer socialProviderId) {

		MessagesDto result = new MessagesDto();

		UserService userService = ServiceFactory.getUserService();
		User user = userService.getUserById(userId);

		SocialNetwork socialProvider = SocialNetwork.getEnum(socialProviderId);
		ExternalIdentity identity = SocialService.getAssociatedSocialIdentity(user, socialProvider);
		
		SocialAPI socialApi = SocialAPIFactory.createProvider(socialProvider, user.getClientPlatform());
		
		List<Message> messages = socialApi.listMessages(identity);
		// prune this before sending to search index
		for(Message message : messages) {
			
			// note that a message can be null here...because Facebook allows conversations without "comments"
			if(message == null) {
				continue;
			}
			
			
			result.getMessages().add(DtoAssembler.assemble(message));
		}	
		
	
		
		// Add to search index
		ServiceFactory.getSearchService().indexMessages(messages);
		
		return Response.ok()
				.entity(jsonConverter.convertToPayload(result))
				.build();
	}

}
