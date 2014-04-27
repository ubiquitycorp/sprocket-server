package com.ubiquity.sprocket.api.endpoints;

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
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.service.AuthenticationService;
import com.ubiquity.social.api.Social;
import com.ubiquity.social.api.SocialFactory;
import com.ubiquity.social.domain.SocialIdentity;
import com.ubiquity.social.domain.SocialProviderType;
import com.ubiquity.sprocket.api.dto.model.AccountDto;
import com.ubiquity.sprocket.api.dto.model.IdentityDto;
import com.ubiquity.sprocket.api.validation.ActivationValidation;
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
	
	@POST
	@Path("/{userId}/identities")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response activate(@PathParam("userId") Long userId, InputStream payload) {
		
			
		// convert payload
		IdentityDto identityDto = jsonConverter.convertFromPayload(payload, IdentityDto.class, ActivationValidation.class);
		
		
		ClientPlatform clientPlatform = ClientPlatform.getEnum(identityDto.getClientPlatformId());
		SocialProviderType socialProvider = SocialProviderType.getEnum(identityDto.getIdentityProviderId());

		// create the identity
		User user = ServiceFactory.getUserService().getUserById(userId);
		SocialIdentity identity = new SocialIdentity.Builder()
			.accessToken(identityDto.getAccessToken())
			.secretToken(identityDto.getSecretToken())
			.socialProviderType(socialProvider)
			.user(user).build();
		user.getIdentities().add(identity);
		
		// get the correct provider based on the social network we are activating
		Social social = SocialFactory.createProvider(socialProvider, clientPlatform);
		
		// authenticate the user
		social.authenticateUser(identity);
		
		IdentityDto result = new IdentityDto.Builder().providerIdentifier(identity.getIdentifier()).build();
		return Response.ok()
				.entity(jsonConverter.convertToPayload(result))
				.build();
		
	}
	
}