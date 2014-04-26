package com.ubiquity.giftsender.api.endpoints;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.giftsender.api.dto.DtoAssembler;
import com.ubiquity.giftsender.api.dto.containers.ClientConfigurationDto;
import com.ubiquity.giftsender.api.dto.model.AccountDto;
import com.ubiquity.giftsender.api.exception.HttpException;
import com.ubiquity.giftsender.service.ClientConfigurationService;
import com.ubiquity.giftsender.service.ServiceFactory;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.service.AuthenticationService;
import com.ubiquity.identity.service.UserService;
import com.ubiquity.social.api.Social;
import com.ubiquity.social.api.SocialFactory;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.SocialIdentity;
import com.ubiquity.social.domain.SocialProviderType;

/***
 * Endpoints for application
 * 
 * @author chris
 * 
 */
@Path("/1.0/mobile/device")
public class DeviceEndpoint {

	private Logger log = LoggerFactory.getLogger(getClass());

	private JsonConverter jsonConverter = JsonConverter.getInstance();

	@GET
	@Path("/ping")
	@Produces(MediaType.APPLICATION_JSON)
	public Response ping() {
		return Response.ok().entity("{\"message\":\"peter\"}").build();
	}

	@GET
	@Path("/authorize")
	@Produces(MediaType.APPLICATION_JSON)
	public Response authorize(@QueryParam("oauth_token") String oauth_token,
			@QueryParam("oauth_verifier") String oauth_verifier,
			@Context UriInfo uriInfo) throws URISyntaxException {
		log.debug("oauth_token = " + oauth_token
				+ " , oauth_verifier = " + oauth_verifier);
		String query = uriInfo.getRequestUri().getQuery();
		return Response
				.created(
						new URI("com-ubiquity-myapp://oauth-response?" + query))
						.location(
								new URI("com-ubiquity-myapp://oauth-response?" + query))
								.build();
	}

	/***
	 * This method authenticates user via social network (Facebook, Yahoo,
	 * LinkedIn and Google+) Then it return an APIKey to use in subsequent
	 * requests.It retrieves user contacts and saves them to local database For
	 * now, it creates a new user in each call
	 * 
	 * @param accessToken
	 * @return
	 */
	@POST
	@Path("/authenticate")
	@Produces(MediaType.APPLICATION_JSON)
	public Response authenticate(InputStream payload) {
		return processAuthenticate(payload);
	}

	/***
	 * This method is used to activate another social network for a user already
	 * have one It returns the APIKey and UserId which are sent in the request
	 * payload
	 * 
	 * @param payload
	 * @return
	 */
	@POST
	@Path("/activate")
	@Produces(MediaType.APPLICATION_JSON)
	public Response activate(InputStream payload) {
		return processAuthenticate(payload);
	}

	/***
	 * This method is used to deactivate another social network for a user
	 * already have one It returns the APIKey and UserId which are sent in the
	 * request payload
	 * 
	 * @param payload
	 * @return
	 */
	@DELETE
	@Path("/activate/{userId}/{providerId}")
	@Produces(MediaType.APPLICATION_JSON)
	//@Secure
	public Response deAtivate(@HeaderParam("ApiKey") String apiKey,
			@PathParam("userId") Long userId,
			@PathParam("providerId") Integer providerId) {

		// Social is an Provider interface
		SocialProviderType providerType = SocialProviderType.getEnum(providerId);

		UserService userService = ServiceFactory.getUserService();
		//User user = userService.searchUserByIdentityId(identityId)
		SocialIdentity identity = ServiceFactory.getSocialService().findSocialIdentity(userId, providerType);
		if(identity == null) // new identity
			throw new IllegalArgumentException(
					"There is no provider associated with this user using Provider id");
		
		// security check to make sure one user can't hijack another
		if(!identity.getUser().getUserId().equals(userId))
			throw new HttpException("This account is associated with another user", 401);
		// deactivate
		identity.setIsActive(Boolean.FALSE);
		// set changes in db
		userService.update(identity);
		
		// update Redis cache time so contacts are synchronized in the next call
		ServiceFactory.getContactService().updateCacheTime(userId); 
		
		
		return Response.ok().build();
		
	}

	/***
	 * 
	 * Clients call this method on startup
	 * 
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/initialized")
	@Produces(MediaType.APPLICATION_JSON)
	public Response boostrap() throws Exception {
		log.debug("init called");

		// Get configuration manager and set rules
		ClientConfigurationService clientConfigurationManager = ServiceFactory
				.getClientConfigurationService();

		// Build response DTO
		ClientConfigurationDto initResult = new ClientConfigurationDto();
		initResult.getRules().putAll(clientConfigurationManager.getRules());

		// Assemble and return
		return Response.ok().entity(jsonConverter.convertToPayload(initResult))
				.build();
	}
	
	private Response processAuthenticate(InputStream payload) {
		AccountDto accountDto = jsonConverter.convertFromPayload(payload, AccountDto.class);
		// need to validate clientPlatform and providerId
		ClientPlatform clientPlatform = ClientPlatform.getEnum(accountDto.getClientPlatformId());

		// Create a new identity based on account input
		SocialIdentity identity = DtoAssembler.assembleSocialIdentity(accountDto, Boolean.TRUE);

		// Get the correct social provider API by the passed-in type (i.e., Facebook LinkedIN)
		Social provider = SocialFactory.createProvider(identity.getSocialProviderType(), clientPlatform);

		// Authenticate this user as a contact; fail right away if we can't validate
		Contact authenticatedContact = provider.authenticateUser(identity);
		if(authenticatedContact == null)
			throw new HttpException("Could not validate with social network", 401);

		UserService userService = ServiceFactory.getUserService();
		User user = userService.searchUserByIdentityId(identity.getIdentityId());
		// new user
		if (user == null) { 

			// If this is a first time user, populate it with the contact info from the first network
			user = new User.Builder()
			.displayName(authenticatedContact.getDisplayName())
			.firstName(authenticatedContact.getFirstName())
			.lastName(authenticatedContact.getLastName())
			.email(authenticatedContact.getEmail())
			.lastUpdated(System.currentTimeMillis())
			.clientPlatform(clientPlatform)
			.image(authenticatedContact.getImage())
			.build();

			// append to this identity
			identity.setUser(user);
			identity.setEmail(authenticatedContact.getEmail());
			user.getIdentities().add(identity);

			// save it to the data store and cache(s)
			userService.create(user);

			// touch the cache so the user will get received orders
			ServiceFactory.getCommerceService().activateReceivedOrders(user.getUserId());
		} else {
			// retrieve existing user information
			user = userService.getUserById(identity.getUser().getUserId());
			// update this identity with a new token sent from client
			userService.update(identity);
		}

		// now start the auth service and store a key for this user
		AuthenticationService authenticationService = ServiceFactory.getAuthenticationService();

		String apiKey = authenticationService.generateAPIKey(identity.getAccessToken());
		
		// set the passed-in DTO with an api key and new user id and send it back
		accountDto = new AccountDto();
		accountDto.setUserId(user.getUserId());
		accountDto.setApiKey(apiKey);

		// Save UserId and APIKey in Redis cache database
		ServiceFactory.getAuthenticationService().saveAuthkey(user.getUserId(), apiKey);

		log.info("User "
				+ user.getUserId()
				+ ": APIKey from cache"
				+ ServiceFactory.getAuthenticationService()
				.retrieveAuthkey(user.getUserId()));
		
		return Response.ok()
				.entity(jsonConverter.convertToPayload(accountDto))
				.build();
	}

}
