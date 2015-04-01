package com.ubiquity.sprocket.api.endpoints;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.http.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.common.serialize.JsonConverter;
import com.niobium.repository.CollectionVariant;
import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.api.annotations.Active;
import com.ubiquity.api.annotations.Secure;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.ExternalNetworkApplication;
import com.ubiquity.identity.service.UserService;
import com.ubiquity.integration.domain.Activity;
import com.ubiquity.integration.domain.Captcha;
import com.ubiquity.integration.domain.Contact;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.Message;
import com.ubiquity.integration.domain.PostActivity;
import com.ubiquity.integration.domain.PostComment;
import com.ubiquity.integration.domain.PostVote;
import com.ubiquity.integration.domain.UserContact;
import com.ubiquity.integration.service.ApplicationService;
import com.ubiquity.integration.service.ContactService;
import com.ubiquity.integration.service.ExternalIdentityService;
import com.ubiquity.integration.service.SocialService;
import com.ubiquity.location.domain.UserLocation;
import com.ubiquity.sprocket.api.DtoAssembler;
import com.ubiquity.sprocket.api.dto.containers.ActivitiesDto;
import com.ubiquity.sprocket.api.dto.containers.ContactsDto;
import com.ubiquity.sprocket.api.dto.containers.DataSyncedDto;
import com.ubiquity.sprocket.api.dto.containers.MessagesDto;
import com.ubiquity.sprocket.api.dto.model.social.ActivityDto;
import com.ubiquity.sprocket.api.dto.model.social.ContactDto;
import com.ubiquity.sprocket.api.dto.model.social.MessageDto;
import com.ubiquity.sprocket.api.dto.model.social.PostActivityDto;
import com.ubiquity.sprocket.api.dto.model.social.PostCommentDto;
import com.ubiquity.sprocket.api.dto.model.social.PostVoteDto;
import com.ubiquity.sprocket.api.dto.model.social.SendMessageDto;
import com.ubiquity.sprocket.api.validation.EngagementValidation;
import com.ubiquity.sprocket.messaging.MessageConverterFactory;
import com.ubiquity.sprocket.messaging.MessageQueueFactory;
import com.ubiquity.sprocket.messaging.definition.UserEngagedActivity;
import com.ubiquity.sprocket.service.LocationService;
import com.ubiquity.sprocket.service.ServiceFactory;

@Path("/1.0/social")
public class SocialEndpoint {

	private Logger log = LoggerFactory.getLogger(getClass());

	private JsonConverter jsonConverter = JsonConverter.getInstance();
	
	private SocialService socialService = ServiceFactory.getSocialService();
	private ContactService contactService = ServiceFactory.getContactService();
	private LocationService locationService = ServiceFactory.getLocationService();
	private UserService userService = ServiceFactory.getUserService();
	private ExternalIdentityService externalIdentityService = ServiceFactory.getExternalIdentityService();
	private ApplicationService applicationService = ServiceFactory.getApplicationService();

	@POST
	@Path("/users/{userId}/activities/engaged")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	@Active
	public Response engaged(@PathParam("userId") Long userId,
			InputStream payload) throws IOException {

		// convert payload
		ActivitiesDto activitiesDto = jsonConverter.convertFromPayload(payload,
				ActivitiesDto.class, EngagementValidation.class);

		for (ActivityDto activityDto : activitiesDto.getActivities()) {
			log.debug("tracking activity {}", activityDto);
			sendTrackAndSyncMessage(userId, activityDto);
		}
		return Response.ok().build();
	}

	@GET
	@Path("users/{userId}/providers/{externalNetworkId}/activities")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	@Active
	public Response activities(@PathParam("userId") Long userId,
			@PathParam("externalNetworkId") Integer socialProviderId,
			@HeaderParam("If-Modified-Since") Long ifModifiedSince) {
		ActivitiesDto results = new ActivitiesDto();

		ExternalNetwork socialNetwork = ExternalNetwork
				.getNetworkById(socialProviderId);

		CollectionVariant<Activity> variant = socialService
				.findActivityByOwnerIdAndSocialNetwork(userId, socialNetwork,
						ifModifiedSince);

		// Throw a 304 if if there is no variant (no change)
		if (variant == null)
			return Response.notModified().build();

		for (Activity activity : variant.getCollection()) {
			results.getActivities().add(DtoAssembler.assemble(activity));
		}

		return Response.ok().header("Last-Modified", variant.getLastModified())
				.entity(jsonConverter.convertToPayload(results)).build();
	}

	@GET
	@Path("users/{userId}/providers/{externalNetworkId}/activities/synced")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	@Active
	public Response getModifiedActivities(@PathParam("userId") Long userId,
			@PathParam("externalNetworkId") Integer socialProviderId,
			@HeaderParam("If-Modified-Since") Long ifModifiedSince) {

		ExternalNetwork socialNetwork = ExternalNetwork
				.getNetworkById(socialProviderId);

		CollectionVariant<Activity> variant = socialService
				.findActivityByOwnerIdAndSocialNetworkAndModifiedSince(userId, socialNetwork,
						ifModifiedSince);

		// Throw a 304 if if there is no variant (no change)
		if (variant == null)
			return Response.notModified().build();

		// Convert entire list to DTO
		DataSyncedDto<ActivityDto> result = new DataSyncedDto<ActivityDto>();
		for (Activity activity : variant.getCollection()) {

			if (activity.isDeleted())
				result.getDeleted().add(activity.getActivityId());
			else {
				ActivityDto activityDto = DtoAssembler.assemble(activity);
				if (activity.getCreatedAt() > ifModifiedSince) {
					result.getAdded().add(activityDto);
				} else {
					result.getUpdated().add(activityDto);
				}
			}
		}

		return Response.ok().header("Last-Modified", variant.getLastModified())
				.entity(jsonConverter.convertToPayload(result)).build();
	}

	@GET
	@Path("users/{userId}/contacts")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	@Active
	public Response contacts(@PathParam("userId") Long userId) {

		// Manager will return contacts if they have been modified, else it will
		// be empty
		List<Contact> variant = contactService
				.findContactsForActiveNetworksByOwnerId(userId);

		// Convert entire list to DTO
		ContactsDto result = new ContactsDto();
		for (Contact contact : variant) {
			ContactDto contactDto = DtoAssembler.assemble(contact);
			result.getContacts().add(contactDto);
		}

		return Response.ok().entity(jsonConverter.convertToPayload(result))
				.build();
	}

	/***
	 * 
	 * @param userId
	 * @param delta
	 * @param ifModifiedSince
	 * @return
	 */
	@GET
	@Path("users/{userId}/contacts/synced")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	@Active
	public Response getModifiedContacts(@PathParam("userId") Long userId,
			@HeaderParam("If-Modified-Since") Long ifModifiedSince) {
		log.debug("Listing contacts modified since: {}", ifModifiedSince);

		// Manager will return contacts if they have been modified, else it will
		// be empty

		CollectionVariant<UserContact> variant = contactService
				.findModifiedContactsForActiveNetworksByOwnerId(userId,
						ifModifiedSince);

		// Throw a 304 if there is no variant (no change)
		if (variant == null) {
			return Response.notModified().build();
		}
		// Convert entire list to DTO
		DataSyncedDto<ContactDto> result = new DataSyncedDto<ContactDto>();
		for (UserContact contact : variant.getCollection()) {

			if (contact.IsDeleted())
				result.getDeleted().add(contact.getContact().getContactId());
			else {
				ContactDto contactDto = DtoAssembler.assemble(contact
						.getContact());
				if (contact.getCreatedAt() > ifModifiedSince) {
					result.getAdded().add(contactDto);
				} else {
					result.getUpdated().add(contactDto);
				}
			}
		}

		return Response.ok().header("Last-Modified", variant.getLastModified())
				.entity(jsonConverter.convertToPayload(result)).build();
	}

	/***
	 * Returns list of activities which represents local news feed in specific
	 * provider based on last updated user's location in the system
	 * 
	 * @param userId
	 * @param socialProviderId
	 * @param ifModifiedSince
	 * @return
	 */
	@GET
	@Path("users/{userId}/providers/{externalNetworkId}/localfeed")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	@Active
	public Response getLocalFeed(@PathParam("userId") Long userId,
			@PathParam("externalNetworkId") Integer socialProviderId,
			@HeaderParam("If-Modified-Since") Long ifModifiedSince,
			@HeaderParam("delta") Boolean delta) {
		ActivitiesDto results = new ActivitiesDto();

		ExternalNetwork socialNetwork = ExternalNetwork
				.getNetworkById(socialProviderId);

		UserLocation userLocation = locationService
				.getLocation(userId);
		// returns empty list if user has not set his location yet
		if (userLocation == null)
			return Response.ok()
					.entity(jsonConverter.convertToPayload(results)).build();

		CollectionVariant<Activity> variant = socialService
				.findActivityByPlaceIdAndSocialNetwork(
						userLocation.getNearestPlace().getPlaceId(),
						socialNetwork, ifModifiedSince, delta);

		// Throw a 304 if if there is no variant (no change)
		if (variant == null)
			return Response.notModified().build();

		for (Activity activity : variant.getCollection()) {
			results.getActivities().add(DtoAssembler.assemble(activity));
		}

		return Response.ok().header("Last-Modified", variant.getLastModified())
				.entity(jsonConverter.convertToPayload(results)).build();
	}

	/***
	 * This method returns messages of specific social network
	 * 
	 * @param userId
	 * @param socialProviderId
	 * @return
	 */
	@GET
	@Path("users/{userId}/providers/{externalNetworkId}/messages")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	@Active
	public Response messages(@PathParam("userId") Long userId,
			@PathParam("externalNetworkId") Integer socialProviderId,
			@HeaderParam("delta") Boolean delta,
			@HeaderParam("If-Modified-Since") Long ifModifiedSince) {
		try {
			MessagesDto result = new MessagesDto();

			ExternalNetwork socialNetwork = ExternalNetwork
					.getNetworkById(socialProviderId);

			CollectionVariant<Message> variant = socialService.findMessagesByOwnerIdAndSocialNetwork(
							userId, socialNetwork, ifModifiedSince, delta);

			// Throw a 304 if if there is no variant (no change)
			if (variant == null)
				return Response.notModified().build();

			List<Message> messages = new LinkedList<Message>();
			messages.addAll(variant.getCollection());

			// Assemble into message dto, constructing conversations if they are
			// inherent in the data
			List<MessageDto> conversations = DtoAssembler.assemble(messages);
			Collections.sort(conversations, Collections.reverseOrder());
			result.getMessages().addAll(conversations);

			return Response.ok()
					.header("Last-Modified", variant.getLastModified())
					.entity(jsonConverter.convertToPayload(result)).build();
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}

	/***
	 * This method send message to specific user in social network
	 * 
	 * @param userId
	 * @param externalNetworkId
	 * @return
	 * @throws org.apache.http.HttpException
	 */
	@POST
	@Path("users/{userId}/providers/{externalNetworkId}/messages")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	@Active
	public Response sendmessage(@PathParam("userId") Long userId,
			@PathParam("externalNetworkId") Integer externalNetworkId,
			InputStream payload) throws org.apache.http.HttpException {

		// Cast the input into SendMessageObject
		SendMessageDto sendMessageDto = jsonConverter.convertFromPayload(
				payload, SendMessageDto.class);

		// load user
		userService.getUserById(userId);
		// get social network
		ExternalNetwork socialNetwork = ExternalNetwork
				.getNetworkById(externalNetworkId);
		// get the identity from DB
		ExternalIdentity identity = externalIdentityService
				.findExternalIdentity(userId, socialNetwork);

		Contact contact = contactService.getByContactId(
				sendMessageDto.getReceiverId());
		// get External network and check the validity of identity
		ExternalNetworkApplication externalNetworkApplication = checkValidityOfExternalIdentity(
				userId, identity);

		socialService.sendMessage(identity, socialNetwork,
				contact, sendMessageDto.getReceiverName(),
				sendMessageDto.getText(), sendMessageDto.getSubject(),
				externalNetworkApplication);

		return Response.ok().build();

	}

	/***
	 * This method send message to specific user in social network
	 * 
	 * @param userId
	 * @param externalNetworkId
	 * @return
	 */
	@POST
	@Path("users/{userId}/providers/{externalNetworkId}/activities")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	@Active
	public Response postActivity(@PathParam("userId") Long userId,
			@PathParam("externalNetworkId") Integer externalNetworkId,
			InputStream payload) throws HttpException {

		PostActivityDto postActivityDto = jsonConverter.convertFromPayload(
				payload, PostActivityDto.class);

		// Validate request parameters dependent on activity type
		postActivityDto.validate();

		// get social network
		ExternalNetwork socialNetwork = ExternalNetwork
				.getNetworkById(externalNetworkId);
		// get the identity from DB
		ExternalIdentity identity = externalIdentityService
				.findExternalIdentity(userId, socialNetwork);

		if (identity == null)
			throw new IllegalArgumentException(
					"User has no identity for this network");

		// get External network and check the validity of identity
		ExternalNetworkApplication externalNetworkApplication = checkValidityOfExternalIdentity(
				userId, identity);

		// Prepare post activity object
		PostActivity postActivity = new PostActivity.Builder()
				.activityTypeId(postActivityDto.getActivityTypeId())
				.title(postActivityDto.getTitle())
				.body(postActivityDto.getBody())
				.link(postActivityDto.getLink())
				.embed(postActivityDto.getEmbed())
				.pageId(postActivityDto.getPageId())
				.captcha(postActivityDto.getCaptcha())
				.captchaIden(postActivityDto.getCaptchaIden()).build();

		socialService.postActivity(identity, socialNetwork,
				postActivity, externalNetworkApplication);

		return Response.ok().build();

	}

	/***
	 * This method post comment to specific user in social network
	 * 
	 * @param userId
	 * @param socialProviderId
	 * @return
	 * @throws org.jets3t.service.impl.rest.HttpException
	 */
	@POST
	@Path("users/{userId}/providers/{externalNetworkId}/comment")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	@Active
	public Response postComment(@PathParam("userId") Long userId,
			@PathParam("externalNetworkId") Integer socialProviderId,
			InputStream payload)
			throws org.jets3t.service.impl.rest.HttpException {

		// Cast the input into SendMessageObject
		PostCommentDto postCommentDto = jsonConverter.convertFromPayload(
				payload, PostCommentDto.class);

		// load user
		userService.getUserById(userId);
		// get social network
		ExternalNetwork socialNetwork = ExternalNetwork
				.getNetworkById(socialProviderId);
		// get the identity from DB
		ExternalIdentity identity = externalIdentityService
				.findExternalIdentity(userId, socialNetwork);

		// get External network and check the validity of identity
		ExternalNetworkApplication externalNetworkApplication = checkValidityOfExternalIdentity(
				userId, identity);

		PostComment postComment = DtoAssembler.assemble(postCommentDto);
		socialService.postComment(identity, socialNetwork,
				postComment, externalNetworkApplication);

		return Response.ok().build();

	}

	@POST
	@Path("users/{userId}/providers/{externalNetworkId}/vote")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	@Active
	public Response postVote(@PathParam("userId") Long userId,
			@PathParam("externalNetworkId") Integer socialProviderId,
			InputStream payload)
			throws org.jets3t.service.impl.rest.HttpException {

		// Cast the input into SendMessageObject
		PostVoteDto postVoteDto = jsonConverter.convertFromPayload(payload,
				PostVoteDto.class);

		// load user
		userService.getUserById(userId);
		// get social network
		ExternalNetwork socialNetwork = ExternalNetwork
				.getNetworkById(socialProviderId);
		// get the identity from DB
		ExternalIdentity identity = externalIdentityService
				.findExternalIdentity(userId, socialNetwork);

		// get External network and check the validity of identity
		ExternalNetworkApplication externalNetworkApplication = checkValidityOfExternalIdentity(
				userId, identity);

		PostVote postComment = DtoAssembler.assemble(postVoteDto);

		socialService.postVote(identity, socialNetwork,
				postComment, externalNetworkApplication);

		return Response.ok().build();

	}

	/***
	 * This end point forces synchronization for specific external network
	 * 
	 * @param userId
	 * @param payload
	 * @return
	 * @throws IOException
	 */
	@GET
	@Path("users/{userId}/providers/{externalNetworkId}/captcha")
	@Consumes(MediaType.APPLICATION_JSON)
	// /@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Secure
	public Response requestCaptcha(@PathParam("userId") Long userId,
			@PathParam("externalNetworkId") Integer socialProviderId)
			throws IOException {

		ExternalNetwork externalNetwork = ExternalNetwork
				.getNetworkById(socialProviderId);

		ExternalIdentity identity = externalIdentityService
				.findExternalIdentity(userId, externalNetwork);

		// get External network and check the validity of identity
		ExternalNetworkApplication externalNetworkApplication = checkValidityOfExternalIdentity(
				userId, identity);

		Captcha captcha = socialService.requestCaptcha(
				identity, externalNetwork, externalNetworkApplication);

		final byte[] image = captcha.getImage();

		if (image != null) {
			StreamingOutput stream = new StreamingOutput() {

				public void write(OutputStream output) throws IOException,
						WebApplicationException {
					try {

						output.write(image);
					} catch (Exception e) {
						throw new WebApplicationException(e);
					}
				}

			};
			return Response
					.ok(stream, MediaType.APPLICATION_OCTET_STREAM)
					.header("content-disposition",
							"attachment; filename=\"captcha."
									+ captcha.getImageType() + "\"")
					.header("captcha-identifier", captcha.getIdentifier())
					.build();
		}

		return Response.ok().build();

	}

	/***
	 * 
	 * @param userId
	 * @param identity
	 * @return
	 */
	private ExternalNetworkApplication checkValidityOfExternalIdentity(
			Long userId, ExternalIdentity identity) {

		// Get app_i from Redis
		Long appId = userService.retrieveApplicationId(
				userId);

		// Load external network application
		ExternalNetworkApplication externalNetworkApp = applicationService
				.getExAppByAppIdAndExternalNetworkAndClientPlatform(appId,
						identity.getExternalNetwork(),
						identity.getClientPlatform());
		socialService.checkValidityOfExternalIdentity(
				identity, externalNetworkApp);
		return externalNetworkApp;
	}

	/**
	 * Drops a message for tracking this event
	 * 
	 * @param userId
	 * @param activityDto
	 * @throws IOException
	 */
	private void sendTrackAndSyncMessage(Long userId, ActivityDto activityDto)
			throws IOException {

		Activity activity = DtoAssembler.assemble(activityDto);

		UserEngagedActivity messageContent = new UserEngagedActivity(userId,
				activity);
		String message = MessageConverterFactory.getMessageConverter()
				.serialize(
						new com.ubiquity.messaging.format.Message(
								messageContent));
		byte[] bytes = message.getBytes();
		MessageQueueFactory.getTrackQueueProducer().write(bytes);
	}
}
