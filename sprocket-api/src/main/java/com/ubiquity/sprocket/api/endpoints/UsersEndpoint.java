package com.ubiquity.sprocket.api.endpoints;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
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
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.Identity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.service.AuthenticationService;
import com.ubiquity.social.domain.ContentNetwork;
import com.ubiquity.social.domain.SocialNetwork;
import com.ubiquity.sprocket.api.dto.model.AccountDto;
import com.ubiquity.sprocket.api.dto.model.IdentityDto;
import com.ubiquity.sprocket.api.validation.ActivationValidation;
import com.ubiquity.sprocket.api.validation.AuthenticationValidation;
import com.ubiquity.sprocket.api.validation.RegistrationValidation;
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
				IdentityDto associatedIdentityDto = new IdentityDto.Builder().identifier(socialIdentity.getIdentifier()).identityProviderId(socialIdentity.getIdentityProvider()).build();
				accountDto.getIdentities().add(associatedIdentityDto);
			}
		}

		// Save UserId and APIKey in Redis cache database
		authenticationService.saveAuthkey(user.getUserId(), apiKey);

		log.debug("Authenticated user {}", user);

		// send notification interested consumers
		//String message = MessageConverterFactory.getMessageConverter().serialize(new Message(new UserAuthenticated(user.getUserId())));
		//MessageQueueFactory.getCacheInvalidationQueueProducer().write(message.getBytes());

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

	
		return Response.ok()
				.entity(jsonConverter.convertToPayload(accountDto))
				.build();
	}

	
	@POST
	@Path("/{userId}/identities")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response activate(@PathParam("userId") Long userId, InputStream payload) throws IOException {

		// convert payload
		IdentityDto identityDto = jsonConverter.convertFromPayload(payload, IdentityDto.class, ActivationValidation.class);


		ClientPlatform clientPlatform = ClientPlatform.getEnum(identityDto.getClientPlatformId());		
		SocialNetwork socialNetwork = SocialNetwork.getEnum(identityDto.getSocialIdentityProviderId());
		
		// load user
		User user = ServiceFactory.getUserService().getUserById(userId);
				
		// create the identity if it does not exist; or use the existing one
		ExternalIdentity identity = ServiceFactory.getSocialService().createOrUpdateSocialIdentity(user, identityDto.getAccessToken(), identityDto.getSecretToken(), clientPlatform, socialNetwork);

		// if this is for a content network, sync it
		if(identityDto.getContentIdentityProviderId() != null) {
			ContentNetwork contentNetwork = ContentNetwork.values()[identityDto.getContentIdentityProviderId()];
			ServiceFactory.getContentService().sync(identity, contentNetwork);
		}
		
		// ServiceFactory.getSocialService().sync(identity, socialNetwork)
		
		IdentityDto result = new IdentityDto.Builder().identifier(identity.getIdentifier()).build();
		return Response.ok()
				.entity(jsonConverter.convertToPayload(result))
				.build();

	}

}