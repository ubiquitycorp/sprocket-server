package com.ubiquity.sprocket.api.endpoints;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.social.domain.SocialProvider;
import com.ubiquity.sprocket.api.dto.containers.ActivitiesDto;
import com.ubiquity.sprocket.api.dto.containers.MessagesDto;
import com.ubiquity.sprocket.api.dto.model.ActivityDto;
import com.ubiquity.sprocket.api.dto.model.ContactDto;
import com.ubiquity.sprocket.api.dto.model.MessageDto;

@Path("/1.0/social")
public class SocialEndpoint {
	
	@SuppressWarnings("unused")
	private Logger log = LoggerFactory.getLogger(getClass());

	private JsonConverter jsonConverter = JsonConverter.getInstance();
	
	
	@GET
	@Path("users/{userId}/activities")
	@Produces(MediaType.APPLICATION_JSON)
	public Response activities(@PathParam("userId") Long userId, InputStream payload) throws IOException {
		ActivitiesDto activities = new ActivitiesDto();
		
		activities.getActivities().add(new ActivityDto.Builder()
			.body("Activity 1 body")
			.date(System.currentTimeMillis())
			.socialProviderId(SocialProvider.Facebook.getValue())
			.title("Activity title 1 lorem ipsum lorem ipsume lorem ipsum lorem ipsum lorem ipsume lorem ipsume lorem ipsum lorem ipsume lorem ipsumelorem ipsum lorem ipsume lorem ipsume")
			.imageUrl(null)
			.postedBy(new ContactDto.Builder().contactId(2l).displayName("Contact 1").firstName("Contact").lastName("One").imageUrl("https://graph.facebook.com/754592628/picture").build())
			.build());
		activities.getActivities().add(new ActivityDto.Builder()
		.body("Activity 2 body lorem ipsum lorem ipsume lorem ipsumelorem ipsum lorem ipsume lorem ipsumelorem ipsum lorem ipsume lorem ipsumelorem ipsum lorem ipsume lorem ipsumelorem ipsum lorem ipsume lorem ipsume")
		.date(System.currentTimeMillis())
		.socialProviderId(SocialProvider.Google.getValue())
		.title("Activity title 2")
		.imageUrl(null)
		.postedBy(new ContactDto.Builder().contactId(2l).displayName("Contact 1").firstName("Contact").lastName("One").imageUrl("https://graph.facebook.com/754592628/picture").build())
		.build());
		
		activities.getActivities().add(new ActivityDto.Builder()
		.body("Activity 3 body lorem ipsum lorem ipsume lorem ipsumelorem ipsum lorem ipsume lorem ipsumelorem ipsum lorem ipsume lorem ipsumelorem ipsum lorem ipsume lorem ipsume")
		.date(System.currentTimeMillis())
		.title("Activity title 3")
		.socialProviderId(SocialProvider.LinkedIn.getValue())
		.imageUrl(null)
		.postedBy(new ContactDto.Builder().contactId(2l).displayName("Contact 1").firstName("Contact").lastName("One").imageUrl("https://graph.facebook.com/754592628/picture").build())
		.build());
		return Response.ok()
				.entity(jsonConverter.convertToPayload(activities))
				.build();
		
	}
	
	
	@GET
	@Path("users/{userId}/messages")
	@Produces(MediaType.APPLICATION_JSON)
	public Response messages(@PathParam("userId") Long userId, InputStream payload) throws IOException {
		
		// convert payload
		MessagesDto messagesDto = new MessagesDto();
		messagesDto.getMessages().add(new MessageDto.Builder()
		.subject("Message subject 1")
		.date(System.currentTimeMillis())
		.socialProviderId(SocialProvider.Facebook.getValue())
		.body("Body of message 1, lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsumlorem ipsum lorem ipsumlorem ipsum lorem ipsumlorem ipsum lorem ipsumlorem ipsum lorem ipsumlorem ipsum lorem ipsum")
		.sender(new ContactDto.Builder().contactId(1l).displayName("Contact 1").firstName("Contact").lastName("One").imageUrl("https://graph.facebook.com/754592629/picture").build())
		.build());
		messagesDto.getMessages().add(new MessageDto.Builder()
		.subject("Message subject 2")
		.socialProviderId(SocialProvider.Google.getValue())
		.date(System.currentTimeMillis())
		.body("Body of message 2, lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsumlorem ipsum lorem ipsumlorem ipsum lorem ipsumlorem ipsum lorem ipsumlorem ipsum lorem ipsumlorem ipsum lorem ipsum")
		.sender(new ContactDto.Builder().contactId(2l).displayName("Contact 1").firstName("Contact").lastName("One").imageUrl("https://graph.facebook.com/754592628/picture").build())
		.build());
		messagesDto.getMessages().add(new MessageDto.Builder()
		.subject("Message subject 3")
		.socialProviderId(SocialProvider.LinkedIn.getValue())
		.date(System.currentTimeMillis())
		.body("Body of message 3, lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsumlorem ipsum lorem ipsumlorem ipsum lorem ipsumlorem ipsum lorem ipsumlorem ipsum lorem ipsumlorem ipsum lorem ipsum")
		.sender(new ContactDto.Builder().contactId(2l).displayName("Contact 2").firstName("Contact").lastName("Two").imageUrl("https://graph.facebook.com/754592628/picture").build())
		.build());
		
		
		return Response.ok()
				.entity(jsonConverter.convertToPayload(messagesDto))
				.build();
	}

}
