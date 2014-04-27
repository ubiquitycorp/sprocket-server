package com.ubiquity.sprocket.api.endpoints;

import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.service.AuthenticationService;
import com.ubiquity.sprocket.api.dto.model.AccountDto;
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
		AccountDto accountDto = jsonConverter.convertFromPayload(payload, AccountDto.class);
		
		AuthenticationService authenticationService = ServiceFactory.getAuthenticationService();
		
		ClientPlatform clientPlatform = ClientPlatform.getEnum(accountDto.getClientPlatformId());
		User user = ServiceFactory.getAuthenticationService().register(accountDto.getUsername(), 
				accountDto.getPassword(), accountDto.getDisplayName(), clientPlatform);
		
		String apiKey = authenticationService.generateApiKey();
		
		// set the passed-in DTO with an api key and new user id and send it back
		accountDto = new AccountDto();
		accountDto.setUserId(user.getUserId());
		accountDto.setApiKey(apiKey);

		// Save UserId and APIKey in Redis cache database
		authenticationService.saveAuthkey(user.getUserId(), apiKey);
		
		log.debug("Created user {}", user);
		
		return Response.ok()
				.entity(jsonConverter.convertToPayload(accountDto))
				.build();
		
		
	}
	
}