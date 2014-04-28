package com.ubiquity.sprocket.api.endpoints;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
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
import com.ubiquity.api.exception.HttpException;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.service.AuthenticationService;
import com.ubiquity.identity.service.UserService;
import com.ubiquity.messaging.format.Message;
import com.ubiquity.social.api.Social;
import com.ubiquity.social.api.SocialFactory;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.SocialIdentity;
import com.ubiquity.social.domain.SocialProviderType;
import com.ubiquity.sprocket.api.DtoAssembler;
import com.ubiquity.sprocket.api.dto.containers.ContactsDto;
import com.ubiquity.sprocket.api.dto.model.AccountDto;
import com.ubiquity.sprocket.api.dto.model.ContactDto;
import com.ubiquity.sprocket.api.dto.model.IdentityDto;
import com.ubiquity.sprocket.api.validation.ActivationValidation;
import com.ubiquity.sprocket.api.validation.RegistrationValidation;
import com.ubiquity.sprocket.messaging.MessageConverterFactory;
import com.ubiquity.sprocket.messaging.MessageQueueFactory;
import com.ubiquity.sprocket.messaging.definition.SocialIdentityActivated;
import com.ubiquity.sprocket.service.ServiceFactory;


@Path("/1.0/users")
public class UsersEndpoint {

	private Logger log = LoggerFactory.getLogger(getClass());

	private JsonConverter jsonConverter = JsonConverter.getInstance();

	@GET
	@Path("/ping")
	@Produces(MediaType.APPLICATION_JSON)
	public Response ping() {
		return Response.ok().entity("{\"message\":\"pong\"}").build();
	}
	
	/***
	 * This method authenticates user via native login. Thereafter users can authenticate
	 * 
	 * @param accessToken
	 * @return
	 */
	@POST
	@Path("/registered")
	@Produces(MediaType.APPLICATION_JSON)
	public Response authenticate(InputStream payload) {
		IdentityDto identityDto = jsonConverter.convertFromPayload(payload, IdentityDto.class, RegistrationValidation.class);
		
		AuthenticationService authenticationService = ServiceFactory.getAuthenticationService();
		
		ClientPlatform clientPlatform = ClientPlatform.getEnum(identityDto.getClientPlatformId());
		User user = ServiceFactory.getAuthenticationService().register(identityDto.getUsername(), 
				identityDto.getPassword(), identityDto.getDisplayName(), clientPlatform);
		
		// user now has a single, native identity
		String apiKey = authenticationService.generateApiKey();
		
		// set the passed-in DTO with an api key and new user id and send it back
		AccountDto accountDto = new AccountDto.Builder()
			.apiKey(apiKey)
			.userId(user.getUserId())
			.build();
		
		// Save UserId and APIKey in Redis cache database
		authenticationService.saveAuthkey(user.getUserId(), apiKey);
		
		log.debug("Created user {}", user);
		
		return Response.ok()
				.entity(jsonConverter.convertToPayload(accountDto))
				.build();
	}
	
	@GET
	@Path("/{userId}/contacts")
	public Response contacts(@PathParam("userId") Long userId, @HeaderParam("If-Modified-Since") Long ifModifiedSince) {
		
		CollectionVariant<Contact> variant = ServiceFactory.getContactService().findAllContactsByOwnerId(userId, ifModifiedSince);

		// Throw a 304 if there is no variant (no change)
		if (variant == null)
			return Response.notModified().build();
		
		// Throw a 204 if the cache has been reset to indicate a long, background load
		if(variant.getLastModified() == 1l)
			return Response.noContent().build();
		
		// Convert entire list to DTO
		ContactsDto result = new ContactsDto();
		for (Contact contact : variant.getCollection()) {
			ContactDto contactDto = DtoAssembler.assembleContactDto(contact);
			result.getContacts().add(contactDto);
		}

		return Response.ok().header("Last-Modified", variant.getLastModified())
				.entity(jsonConverter.convertToPayload(result)).build();
		
	}
	@POST
	@Path("/{userId}/identities")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response activate(@PathParam("userId") Long userId, InputStream payload) throws IOException {
		
		
		// convert payload
		IdentityDto identityDto = jsonConverter.convertFromPayload(payload, IdentityDto.class, ActivationValidation.class);
		
		
		ClientPlatform clientPlatform = ClientPlatform.getEnum(identityDto.getClientPlatformId());
		SocialProviderType socialProvider = SocialProviderType.getEnum(identityDto.getIdentityProviderId());

		// create the identity
		UserService userService = ServiceFactory.getUserService();
		User user = userService.getUserById(userId);
		SocialIdentity identity = new SocialIdentity.Builder()
			.accessToken(identityDto.getAccessToken())
			.secretToken(identityDto.getSecretToken())
			.socialProviderType(socialProvider)
			.user(user)
			.build();
		user.getIdentities().add(identity);
		
		// get the correct provider based on the social network we are activating
		Social social = SocialFactory.createProvider(socialProvider, clientPlatform);
		
		// authenticate the user; this will give the user a contact record specific for to this network
		Contact contact;
		try {
			contact = social.authenticateUser(identity);
		} catch (Exception e) {
			throw new HttpException("Could not authenticate with provider", 401);
		}
		
		ServiceFactory.getContactService().create(contact);
		
		// now update the user's identity
		userService.update(user);
		
		// send notification to the data sync that some stuff needs to be loaded for this user now....
		SocialIdentityActivated content = new SocialIdentityActivated.Builder()
			.clientPlatformId(identityDto.getClientPlatformId())
			.userId(userId)
			.identityId(identity.getIdentityId())
			.build();
		
		// serialize it
		String message = MessageConverterFactory.getMessageConverter().serialize(new Message(content));
		// send it
		MessageQueueFactory.getCacheInvalidationQueueProducer().write(message.getBytes());
		
		IdentityDto result = new IdentityDto.Builder().identifier(identity.getIdentifier()).build();
		return Response.ok()
				.entity(jsonConverter.convertToPayload(result))
				.build();
		
	}
	
}