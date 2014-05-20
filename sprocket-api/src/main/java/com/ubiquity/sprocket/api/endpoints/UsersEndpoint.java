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
import com.ubiquity.identity.domain.Identity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.service.AuthenticationService;
import com.ubiquity.identity.service.UserService;
import com.ubiquity.messaging.format.Message;
import com.ubiquity.social.api.SocialAPI;
import com.ubiquity.social.api.SocialAPIFactory;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.ContentProvider;
import com.ubiquity.social.domain.ExternalIdentity;
import com.ubiquity.social.domain.SocialProvider;
import com.ubiquity.sprocket.api.DtoAssembler;
import com.ubiquity.sprocket.api.dto.containers.ContactsDto;
import com.ubiquity.sprocket.api.dto.model.AccountDto;
import com.ubiquity.sprocket.api.dto.model.ContactDto;
import com.ubiquity.sprocket.api.dto.model.IdentityDto;
import com.ubiquity.sprocket.api.validation.ActivationValidation;
import com.ubiquity.sprocket.api.validation.AuthenticationValidation;
import com.ubiquity.sprocket.api.validation.RegistrationValidation;
import com.ubiquity.sprocket.messaging.MessageConverterFactory;
import com.ubiquity.sprocket.messaging.MessageQueueFactory;
import com.ubiquity.sprocket.messaging.definition.ExternalIdentityActivated;
import com.ubiquity.sprocket.messaging.definition.UserAuthenticated;
import com.ubiquity.sprocket.messaging.definition.UserRegistered;
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
	 * @throws IOException 
	 */
	@POST
	@Path("/authenticated")
	@Produces(MediaType.APPLICATION_JSON)
	public Response authenticate(InputStream payload) throws IOException {
		IdentityDto identityDto = jsonConverter.convertFromPayload(payload, IdentityDto.class, AuthenticationValidation.class);

		AuthenticationService authenticationService = ServiceFactory.getAuthenticationService();
		User user = authenticationService.authenticate(identityDto.getUsername(), identityDto.getPassword());
		if(user == null)
			throw new HttpException("Username / password incorrect", 401);
		
		// create api key and pass back associated identities for this user (in case of a login from a different device)
		String apiKey = authenticationService.generateApiKey();
		AccountDto accountDto = new AccountDto.Builder()
		.apiKey(apiKey)
		.userId(user.getUserId())
		.build();

		for(Identity identity : user.getIdentities()) {
			if(identity instanceof ExternalIdentity) {
				ExternalIdentity socialIdentity = (ExternalIdentity)identity;
				IdentityDto associatedIdentityDto = new IdentityDto.Builder().identifier(socialIdentity.getIdentifier()).identityProviderId(socialIdentity.getSocialProvider().getValue()).build();
				accountDto.getIdentities().add(associatedIdentityDto);
			}
		}

		// Save UserId and APIKey in Redis cache database
		authenticationService.saveAuthkey(user.getUserId(), apiKey);

		log.debug("Authenticated user {}", user);

		// send notification interested consumers
		String message = MessageConverterFactory.getMessageConverter().serialize(new Message(new UserAuthenticated(user.getUserId())));
		MessageQueueFactory.getCacheInvalidationQueueProducer().write(message.getBytes());

		return Response.ok()
				.entity(jsonConverter.convertToPayload(accountDto))
				.build();
	}


	/***
	 * This method registers user via native login. Thereafter users can authenticate
	 * 
	 * @param accessToken
	 * @return
	 * @throws IOException 
	 */
	@POST
	@Path("/registered")
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(InputStream payload) throws IOException {
		IdentityDto identityDto = jsonConverter.convertFromPayload(payload, IdentityDto.class, RegistrationValidation.class);

		AuthenticationService authenticationService = ServiceFactory.getAuthenticationService();

		ClientPlatform clientPlatform = ClientPlatform.getEnum(identityDto.getClientPlatformId());
		User user = ServiceFactory.getAuthenticationService().register(identityDto.getUsername(), 
				identityDto.getPassword(), identityDto.getDisplayName(), clientPlatform);

		// user now has a single, native identity
		String apiKey = authenticationService.generateApiKey();

		// set the account DTO with an api key and new user id and send it back
		AccountDto accountDto = new AccountDto.Builder()
		.apiKey(apiKey)
		.userId(user.getUserId())
		.build();

		// Save UserId and APIKey in Redis cache database
		authenticationService.saveAuthkey(user.getUserId(), apiKey);

		log.debug("Created user {}", user);

		// send notification interested consumers
		String message = MessageConverterFactory.getMessageConverter().serialize(new Message(new UserRegistered(user.getUserId())));
		MessageQueueFactory.getCacheInvalidationQueueProducer().write(message.getBytes());

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
			ContactDto contactDto = DtoAssembler.assemble(contact);
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
		Integer socialProviderTypeId = identityDto.getSocialIdentityProviderId();
		Integer contentProviderTypeId = identityDto.getContentIdentityProviderId();
		
		SocialProvider socialProvider = null;
		ContentProvider contentProvider = null;
		if(socialProviderTypeId != null)
			socialProvider = SocialProvider.getEnum(identityDto.getSocialIdentityProviderId());
		if(contentProviderTypeId != null)
			contentProvider = ContentProvider.values()[contentProviderTypeId];
		
		// create the identity
		UserService userService = ServiceFactory.getUserService();
		User user = userService.getUserById(userId);
		ExternalIdentity identity = new ExternalIdentity.Builder()
			.accessToken(identityDto.getAccessToken())
			.secretToken(identityDto.getSecretToken())
			.socialProvider(socialProvider)
			.contentProvider(contentProvider)
			.user(user)
			.build();
		user.getIdentities().add(identity);

		// get the correct provider based on the social network we are activating; if we have content provider
		// and a social provider, we use the social to auth (int the case of Google and YouTube); If we have 
		// a single content provider we use that...
		if(socialProvider != null) {
			SocialAPI social = SocialAPIFactory.createProvider(socialProvider, clientPlatform);

			// authenticate the user; this will give the user a contact record specific for to this network
			Contact contact;
			try {
				contact = social.authenticateUser(identity);
			} catch (Exception e) {
				throw new HttpException("Could not authenticate with provider: " + e.getMessage(), 401);
			}

			ServiceFactory.getContactService().create(contact);
		}

		// now update the user's identity
		userService.update(user);

		// send notification to the data sync that some stuff needs to be loaded for this user now....
		ExternalIdentityActivated content = new ExternalIdentityActivated.Builder()
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