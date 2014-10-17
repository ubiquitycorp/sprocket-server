package com.ubiquity.sprocket.api.endpoints;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.api.exception.HttpException;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.Identity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.service.AuthenticationService;
import com.ubiquity.integration.api.ContentAPI;
import com.ubiquity.integration.api.ContentAPIFactory;
import com.ubiquity.integration.api.SocialAPI;
import com.ubiquity.integration.api.SocialAPIFactory;
import com.ubiquity.integration.api.linkedin.ExchangeService;
import com.ubiquity.integration.api.twitter.TwitterAPI;
import com.ubiquity.integration.domain.Contact;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.Network;
import com.ubiquity.integration.domain.SocialToken;
import com.ubiquity.messaging.format.Message;
import com.ubiquity.sprocket.api.DtoAssembler;
import com.ubiquity.sprocket.api.dto.containers.IdentitiesDto;
import com.ubiquity.sprocket.api.dto.model.AccountDto;
import com.ubiquity.sprocket.api.dto.model.ContactDto;
import com.ubiquity.sprocket.api.dto.model.ExchangeTokenDto;
import com.ubiquity.sprocket.api.dto.model.IdentityDto;
import com.ubiquity.sprocket.api.dto.model.LocationDto;
import com.ubiquity.sprocket.api.dto.model.ResetPasswordDto;
import com.ubiquity.sprocket.api.interceptors.Secure;
import com.ubiquity.sprocket.api.validation.ActivationValidation;
import com.ubiquity.sprocket.api.validation.AuthenticationValidation;
import com.ubiquity.sprocket.api.validation.AuthorizationValidation;
import com.ubiquity.sprocket.api.validation.RegistrationValidation;
import com.ubiquity.sprocket.api.validation.ResetValidation;
import com.ubiquity.sprocket.api.validation.UserLocationUpdateValidation;
import com.ubiquity.sprocket.messaging.MessageConverterFactory;
import com.ubiquity.sprocket.messaging.MessageQueueFactory;
//import com.ubiquity.sprocket.messaging.definition.EventTracked;
import com.ubiquity.sprocket.messaging.definition.ExternalIdentityActivated;
import com.ubiquity.sprocket.messaging.definition.LocationUpdated;
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
	@Secure
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
		List<ExternalIdentity> identities = ServiceFactory.getExternalIdentityService()
				.createOrUpdateExternalIdentity(user, accesstokens[0],
						accesstokens[1], null, ClientPlatform.WEB,
						ExternalNetwork.LinkedIn, null);
		
		ExternalIdentity identity = identities.get(0);
		IdentityDto result = new IdentityDto.Builder().identifier(
				identity.getIdentifier()).build();
		// now send the message activated message to cache invalidate
		sendActivatedMessage(user, identities, result);

		try {

			Contact contact = ServiceFactory.getContactService()
					.getBySocialIdentityId(identity.getIdentityId());
			ContactDto contactDto = DtoAssembler.assemble(contact);
			return Response.ok()
					.entity(jsonConverter.convertToPayload(contactDto)).build();
		} catch (NoResultException ex) {

			return Response.ok().entity(jsonConverter.convertToPayload(result))
					.build();
		}

	}

	/***
	 * This method used to request Token
	 * 
	 * @param cookie
	 *            InputStream
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/{userId}/requesttoken")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response requesttoken(@PathParam("userId") Long userId,
			InputStream payload) throws Exception {

		// load user
		ServiceFactory.getUserService().getUserById(userId);
		IdentityDto identityDto = jsonConverter.convertFromPayload(payload,
				IdentityDto.class, AuthorizationValidation.class);
		// ClientPlatform clientPlatform = ClientPlatform.getEnum(identityDto
		// .getClientPlatformId());
		ExternalNetwork externalNetwork = ExternalNetwork
				.getNetworkById(identityDto.getExternalNetworkId());
		if (externalNetwork == ExternalNetwork.Twitter) {
			SocialAPI socialApi = SocialAPIFactory
					.createTwitterProvider(identityDto.getRedirectUrl());
			TwitterAPI twitterApi = (TwitterAPI) socialApi;
			SocialToken requestToken = twitterApi.requesttoken();
			if (requestToken == null
					|| requestToken.getAccessToken().equalsIgnoreCase(""))
				throw new HttpException(
						"Autontication Failed no oAuth_token_returned", 401);
			else
				return Response
						.ok()
						.entity("{\"oauthToken\":\""
								+ requestToken.getAccessToken()
								+ "\",\"oauthTokenSecret\":\""
								+ requestToken.getSecretToken() + "\"}")
						// .entity(jsonConverter.convertToPayload(requestToken))
						.build();
		}
		throw new NotImplementedException("ExternalNetwork is not supported");

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
		
		// update user last login 
		user.setLastLogin(System.currentTimeMillis());
		ServiceFactory.getUserService().update(user);
		// create api key and pass back associated identities for this user (in
		// case of a login from a different device)
		
		String apiKey = authenticationService.generateAPIKeyIfNotExsits(user.getUserId());
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
				identityDto.getDisplayName(), identityDto.getEmail(),
				clientPlatform, Boolean.TRUE);

		// user now has a single, native identity
		String apiKey = AuthenticationService.generateAPIKey();

		// set the account DTO with an api key and new user id and send it back
		AccountDto accountDto = new AccountDto.Builder().apiKey(apiKey)
				.userId(user.getUserId()).build();

		// Save UserId and APIKey in Redis cache database
		authenticationService.saveAuthkey(user.getUserId(), apiKey);

		log.debug("Created user {}", user);

		return Response.ok().entity(jsonConverter.convertToPayload(accountDto))
				.build();
	}
	@GET
	@Path("/{userId}/identities")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response getIdentities(@PathParam("userId") Long userId) throws IOException {

		// load user
		User user = ServiceFactory.getUserService().getUserById(userId);
		Set<Identity> identities = user.getIdentities();
		IdentitiesDto identitiesDto = new IdentitiesDto();
		for (Identity identity : identities) {
			if(identity instanceof ExternalIdentity) {
				identitiesDto.getIdentities().add(DtoAssembler.assemble((ExternalIdentity)identity));
			}
		}
		return Response.ok()
				.entity(jsonConverter.convertToPayload(identitiesDto)).build();
	}
	@POST
	@Path("/{userId}/identities")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
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
		List<ExternalIdentity> identity = ServiceFactory.getExternalIdentityService()
				.createOrUpdateExternalIdentity(user,
						identityDto.getAccessToken(),
						identityDto.getSecretToken(),
						identityDto.getRefreshToken(), clientPlatform,
						externalNetwork, identityDto.getExpiresIn());

		// now send the message activated message to cache invalidate
		sendActivatedMessage(user, identity, identityDto);

		// send off to analytics tracker
		// sendEventTrackedMessage(user, identity);
		try {

			Contact contact = ServiceFactory.getContactService()
					.getBySocialIdentityId(identity.get(0).getIdentityId());
			ContactDto contactDto = DtoAssembler.assemble(contact);
			return Response.ok()
					.entity(jsonConverter.convertToPayload(contactDto)).build();
		} catch (NoResultException ex) {
			IdentityDto result = new IdentityDto.Builder().identifier(
					identity.get(0).getIdentifier()).build();
			return Response.ok().entity(jsonConverter.convertToPayload(result))
					.build();
		}
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
	@Secure
	public Response authorize(@PathParam("userId") Long userId,
			InputStream payload) throws IOException {

		// convert payload
		IdentityDto identityDto = jsonConverter.convertFromPayload(payload,
				IdentityDto.class, AuthorizationValidation.class);
		ClientPlatform clientPlatform = ClientPlatform.getEnum(identityDto
				.getClientPlatformId());
		ExternalNetwork externalNetwork = ExternalNetwork
				.getNetworkById(identityDto.getExternalNetworkId());
		List<ExternalIdentity> identiies = null;
		// load user
		User user = ServiceFactory.getUserService().getUserById(userId);
		if (externalNetwork.network == Network.Content) {

			ContentAPI contentApi = ContentAPIFactory.createProvider(
					externalNetwork, clientPlatform);
			String accessToken = contentApi.getAccessToken(
					identityDto.getCode(), identityDto.getRedirectUrl());

			// create the identity if it does not exist; or use the existing one
			identiies = ServiceFactory.getExternalIdentityService()
					.createOrUpdateExternalIdentity(user, accessToken,
							identityDto.getSecretToken(),
							identityDto.getRefreshToken(), clientPlatform,
							externalNetwork, null);
		} else if (externalNetwork.network == Network.Social) {
			SocialAPI socialApi = SocialAPIFactory.createProvider(
					externalNetwork, clientPlatform);
			String redirectUri = null;
			if ((externalNetwork.equals(ExternalNetwork.Google) || externalNetwork
					.equals(ExternalNetwork.YouTube))
					&& clientPlatform.equals(ClientPlatform.WEB)) {
				redirectUri = "postmessage";
			}
			// the expiredAt value in externalIdentity object returned from
			// getAccessToken() is equal to expiresIn value
			ExternalIdentity externalidentity = socialApi.getAccessToken(
					identityDto.getCode(), identityDto.getOauthToken(),
					identityDto.getOauthTokenSecret(), redirectUri);

			identiies = ServiceFactory.getExternalIdentityService()
					.createOrUpdateExternalIdentity(user,
							externalidentity.getAccessToken(),
							externalidentity.getSecretToken(),
							externalidentity.getRefreshToken(), clientPlatform,
							externalNetwork, externalidentity.getExpiredAt());

		}

		// now send the message activated message to cache invalidate
		sendActivatedMessage(user, identiies, identityDto);

		// send off to analytics tracker
		// sendEventTrackedMessage(user, identity);

		try {

			Contact contact = ServiceFactory.getContactService()
					.getBySocialIdentityId(identiies.get(0).getIdentityId());
			ContactDto contactDto = DtoAssembler.assemble(contact);
			return Response.ok()
					.entity(jsonConverter.convertToPayload(contactDto)).build();
		} catch (NoResultException ex) {
			IdentityDto result = new IdentityDto.Builder().identifier(
					identiies.get(0).getIdentifier()).build();
			return Response.ok().entity(jsonConverter.convertToPayload(result))
					.build();
		}

	}

	/***
	 * This method exchanges short-lived access token with long-lived one in a
	 * given provider.
	 * 
	 * @param userId
	 * @param externalNetworkId
	 * @param accessToken
	 * @return
	 */
	@POST
	@Path("/{userId}/exchangedToken")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response exchangeAccessToken(@PathParam("userId") Long userId,
			InputStream payload) {

		ExchangeTokenDto exchangeTokenDto = jsonConverter.convertFromPayload(
				payload, ExchangeTokenDto.class);
		ExternalNetwork externalNetwork = ExternalNetwork
				.getNetworkById(exchangeTokenDto.getExternalNetworkId());
		// load user
		// User user = ServiceFactory.getUserService().getUserById(userId);
		SocialAPI socialApi = SocialAPIFactory.createProvider(externalNetwork,
				ClientPlatform.WEB);
		SocialToken token = socialApi.exchangeAccessToken(exchangeTokenDto
				.getAccessToken());

		return Response.ok()
				.entity("{\"accessToken\":\"" + token.getAccessToken() + "\"}")
				.build();
	}

	/***
	 * 
	 * @param email
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/authenticated/reset/requests")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	// @ValidateRequest
	public Response sendResetEmail(InputStream payload) throws IOException {
		IdentityDto identityDto = jsonConverter.convertFromPayload(payload,
				IdentityDto.class, ResetValidation.class);
		ServiceFactory.getUserService().sendResetPasswordEmail(
				identityDto.getUsername());
		return Response.ok().build();
	}

	/***
	 * 
	 * @param payload
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/authenticated/reset/responses")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response resetPassword(InputStream payload) throws IOException {
		ResetPasswordDto resetPasswordDto = jsonConverter.convertFromPayload(
				payload, ResetPasswordDto.class);
		ServiceFactory.getUserService().resetPassword(
				resetPasswordDto.getToken(), resetPasswordDto.getPassword());
		return Response.ok().build();
	}

	/***
	 * This method receives user's location and saves it into database
	 * 
	 * @param payload
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/{userId}/location")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response setLocation(@PathParam("userId") Long userId,
			InputStream payload) throws IOException {
		
		LocationDto locationDto = jsonConverter.convertFromPayload(payload,LocationDto.class,UserLocationUpdateValidation.class);
		
		sendLocationMessage(userId, locationDto);
		
		return Response.ok().build();
	}

	private void sendActivatedMessage(User user, List<ExternalIdentity> identities,
			IdentityDto identityDto) throws IOException {
		for(ExternalIdentity identity : identities)
		{
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
	
	private void sendLocationMessage(Long userId, LocationDto locationDto) throws IOException {
		LocationUpdated content = new LocationUpdated.Builder()
			.userId(userId)
			.horizontalAccuracy(locationDto.getHorizontalAccuracy())
			.verticalAccuracy(locationDto.getVerticalAccuracy())
			.timestamp(locationDto.getTimestamp())
			.latitude(locationDto.getLatitude())
			.longitude(locationDto.getLongitude())
			.altitude(locationDto.getAltitude())
		.build();
		
		// serialize and send it
		String message = MessageConverterFactory.getMessageConverter()
				.serialize(new Message(content));
		MessageQueueFactory.getLocationQueueProducer().write(
				message.getBytes());
	}
	

}