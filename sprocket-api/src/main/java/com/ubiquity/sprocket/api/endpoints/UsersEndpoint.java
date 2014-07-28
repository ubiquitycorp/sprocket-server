package com.ubiquity.sprocket.api.endpoints;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.api.exception.HttpException;
import com.ubiquity.content.api.ContentAPI;
import com.ubiquity.content.api.ContentAPIFactory;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.Identity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.service.AuthenticationService;
import com.ubiquity.messaging.format.Message;
import com.ubiquity.social.api.SocialAPI;
import com.ubiquity.social.api.SocialAPIFactory;
import com.ubiquity.social.api.linkedin.ExchangeService;
import com.ubiquity.sprocket.api.dto.model.AccountDto;
import com.ubiquity.sprocket.api.dto.model.ExchangeTokenDto;
import com.ubiquity.sprocket.api.dto.model.IdentityDto;
import com.ubiquity.sprocket.api.validation.ActivationValidation;
import com.ubiquity.sprocket.api.validation.AuthenticationValidation;
import com.ubiquity.sprocket.api.validation.AuthorizationValidation;
import com.ubiquity.sprocket.api.validation.RegistrationValidation;
import com.ubiquity.sprocket.messaging.MessageConverterFactory;
import com.ubiquity.sprocket.messaging.MessageQueueFactory;
//import com.ubiquity.sprocket.messaging.definition.EventTracked;
import com.ubiquity.sprocket.messaging.definition.ExternalIdentityActivated;
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
	 * This method authenticates user's linkedin via native login. Thereafter
	 * users can authenticate
	 * 
	 * @param cookie
	 *            file
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/{userId}/authenticatedlinkedin")
	@Produces(MediaType.APPLICATION_JSON)
	public Response authenticatedlinkedin(@PathParam("userId") Long userId,
			@CookieParam("linkedin_oauth_77fa6kjljumj8x") String cookie)
			throws Exception {

		// load user
		User user = ServiceFactory.getUserService().getUserById(userId);
		if (user == null)
			throw new HttpException("Username / password incorrect", 401);

		String cookieString = java.net.URLDecoder.decode(cookie, "UTF-8");
		ExchangeService exchangservice = new ExchangeService();
		String[] accesstokens = exchangservice.exchangeToken(cookieString);

		if (accesstokens[0] == null || accesstokens[0].equalsIgnoreCase(""))
			throw new HttpException(
					"Autontication Failed no oAuth_token_returned", 401);

		// create the identity if it does not exist; or use the existing one
		ExternalIdentity identity = ServiceFactory.getExternalIdentityService()
				.createOrUpdateExternalIdentity(user, accesstokens[0],
						accesstokens[1], ClientPlatform.WEB,
						ExternalNetwork.LinkedIn);

		return Response.ok().build();

	}

	/***
	 * This method authenticates user via native login. Thereafter users can
	 * authenticate
	 * 
	 * @param accessToken
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/authenticated")
	@Produces(MediaType.APPLICATION_JSON)
	public Response authenticate(InputStream payload) throws IOException {
		IdentityDto identityDto = jsonConverter.convertFromPayload(payload,
				IdentityDto.class, AuthenticationValidation.class);

		AuthenticationService authenticationService = ServiceFactory
				.getAuthenticationService();
		User user = authenticationService.authenticate(
				identityDto.getUsername(), identityDto.getPassword());
		if (user == null)
			throw new HttpException("Username / password incorrect", 401);

		// create api key and pass back associated identities for this user (in
		// case of a login from a different device)
		String apiKey = authenticationService.generateApiKey();
		AccountDto accountDto = new AccountDto.Builder().apiKey(apiKey)
				.userId(user.getUserId()).build();

		for (Identity identity : user.getIdentities()) {
			if (identity instanceof ExternalIdentity) {
				ExternalIdentity socialIdentity = (ExternalIdentity) identity;
				IdentityDto associatedIdentityDto = new IdentityDto.Builder()
						.identifier(socialIdentity.getIdentifier())
						.externalNetworkId(socialIdentity.getExternalNetwork())
						.build();
				accountDto.getIdentities().add(associatedIdentityDto);
			}
		}

		// Save UserId and APIKey in Redis cache database
		authenticationService.saveAuthkey(user.getUserId(), apiKey);

		log.debug("Authenticated user {}", user);

		// send notification interested consumers
		// String message =
		// MessageConverterFactory.getMessageConverter().serialize(new
		// Message(new UserAuthenticated(user.getUserId())));
		// MessageQueueFactory.getCacheInvalidationQueueProducer().write(message.getBytes());

		return Response.ok().entity(jsonConverter.convertToPayload(accountDto))
				.build();
	}

	/***
	 * This method registers user via native login. Thereafter users can
	 * authenticate
	 * 
	 * @param accessToken
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/registered")
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(InputStream payload) throws IOException {
		IdentityDto identityDto = jsonConverter.convertFromPayload(payload,
				IdentityDto.class, RegistrationValidation.class);

		AuthenticationService authenticationService = ServiceFactory
				.getAuthenticationService();

		ClientPlatform clientPlatform = ClientPlatform.getEnum(identityDto
				.getClientPlatformId());
		User user = ServiceFactory.getAuthenticationService().register(
				identityDto.getUsername(), identityDto.getPassword(), "", "",
				identityDto.getDisplayName(), clientPlatform, Boolean.TRUE);

		// user now has a single, native identity
		String apiKey = authenticationService.generateApiKey();

		// set the account DTO with an api key and new user id and send it back
		AccountDto accountDto = new AccountDto.Builder().apiKey(apiKey)
				.userId(user.getUserId()).build();

		// Save UserId and APIKey in Redis cache database
		authenticationService.saveAuthkey(user.getUserId(), apiKey);

		log.debug("Created user {}", user);

		return Response.ok().entity(jsonConverter.convertToPayload(accountDto))
				.build();
	}

	@POST
	@Path("/{userId}/identities")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response activate(@PathParam("userId") Long userId,
			InputStream payload) throws IOException {

		// convert payload
		IdentityDto identityDto = jsonConverter.convertFromPayload(payload,
				IdentityDto.class, ActivationValidation.class);

		ClientPlatform clientPlatform = ClientPlatform.getEnum(identityDto
				.getClientPlatformId());
		ExternalNetwork externalNetwork = ExternalNetwork
				.getNetworkById(identityDto.getExternalNetworkId());

		// load user
		User user = ServiceFactory.getUserService().getUserById(userId);

		// create the identity if it does not exist; or use the existing one
		ExternalIdentity identity = ServiceFactory.getExternalIdentityService()
				.createOrUpdateExternalIdentity(user,
						identityDto.getAccessToken(),
						identityDto.getSecretToken(), clientPlatform,
						externalNetwork);

		// now send the message activated message to cache invalidate
		sendActivatedMessage(user, identity, identityDto);

		// send off to analytics tracker
		//sendEventTrackedMessage(user, identity);

		IdentityDto result = new IdentityDto.Builder().identifier(
				identity.getIdentifier()).build();
		return Response.ok().entity(jsonConverter.convertToPayload(result))
				.build();

	}

	/***
	 * This end point authorizes user in the given content network and retrieves
	 * access token. Then it saves an external identity for this user in given
	 * content network
	 * 
	 * @param userId
	 * @param payload
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/{userId}/authorized")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response authorize(@PathParam("userId") Long userId,
			InputStream payload) throws IOException {

		// convert payload
		IdentityDto identityDto = jsonConverter.convertFromPayload(payload,
				IdentityDto.class, AuthorizationValidation.class);
		ClientPlatform clientPlatform = ClientPlatform.getEnum(identityDto
				.getClientPlatformId());
		ExternalNetwork externalNetwork = ExternalNetwork
				.getNetworkById(identityDto.getExternalNetworkId());

		// load user
		User user = ServiceFactory.getUserService().getUserById(userId);
		ContentAPI contentApi = ContentAPIFactory.createProvider(
				externalNetwork, clientPlatform);
		String accessToken = contentApi.getAccessToken(identityDto.getCode(),
				identityDto.getRedirectUrl());

		// create the identity if it does not exist; or use the existing one
		ExternalIdentity identity = ServiceFactory.getExternalIdentityService()
				.createOrUpdateExternalIdentity(user, accessToken,
						identityDto.getSecretToken(), clientPlatform,
						externalNetwork);
		
		sendActivatedMessage(user, identity, identityDto);

		return Response.ok().build();

	}

	/***
	 * This method exchanges short-lived access token with long-lived one in a given provider.
	 * @param userId
	 * @param externalNetworkId
	 * @param accessToken
	 * @return
	 */
	@POST
	@Path("/{userId}/exchangedToken")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response exchangeAccessToken(@PathParam("userId") Long userId, InputStream payload){
		
		ExchangeTokenDto exchangeTokenDto = jsonConverter.convertFromPayload(payload, ExchangeTokenDto.class);
		ExternalNetwork externalNetwork = ExternalNetwork.getNetworkById(exchangeTokenDto.getExternalNetworkId());
		// load user
		User user = ServiceFactory.getUserService().getUserById(userId);
		SocialAPI socialApi = SocialAPIFactory.createProvider(externalNetwork, ClientPlatform.WEB);
		String LongLivedAccessToken = socialApi.exchangeAccessToken(exchangeTokenDto.getAccessToken());
		
		return Response.ok("{\"accessToken\":\"" + LongLivedAccessToken + "\"}").build();
	}
	
	private void sendActivatedMessage(User user, ExternalIdentity identity,
			IdentityDto identityDto) throws IOException {
		ExternalIdentityActivated content = new ExternalIdentityActivated.Builder()
				.clientPlatformId(identityDto.getClientPlatformId())
				.userId(user.getUserId()).identityId(identity.getIdentityId())
				.build();

		// serialize and send it
		String message = MessageConverterFactory.getMessageConverter()
				.serialize(new Message(content));
		MessageQueueFactory.getCacheInvalidationQueueProducer().write(
				message.getBytes());
	}


}