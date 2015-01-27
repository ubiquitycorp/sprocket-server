package com.ubiquity.sprocket.api.endpoints;

import java.io.IOException;
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
import com.ubiquity.sprocket.api.dto.model.developer.DeveloperDto;
import com.ubiquity.sprocket.api.validation.RegistrationValidation;

/***
 * 
 * @author peter.tadros
 * 
 */
@Path("/1.0/developers")
public class DeveloperEndPoint {

	private JsonConverter jsonConverter = JsonConverter.getInstance();

	private Logger log = LoggerFactory.getLogger(getClass());

	@GET
	@Path("/ping")
	@Produces(MediaType.APPLICATION_JSON)
	public Response ping() {
		return Response.ok().entity("{\"message\":\"pong\"}").build();
	}
	
	/***
	 * This method registers a developer to the system
	 * 
	 * @param payload
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/registered")
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(InputStream payload) throws IOException {
		DeveloperDto developerDto = jsonConverter.convertFromPayload(payload,
				DeveloperDto.class, RegistrationValidation.class);

		/*
		 * AuthenticationService authenticationService = ServiceFactory
		 * .getAuthenticationService();
		 * 
		 * 
		 * User user = ServiceFactory.getAuthenticationService().register(
		 * identityDto.getUsername(), identityDto.getPassword(), "", "",
		 * identityDto.getDisplayName(), identityDto.getEmail(), clientPlatform,
		 * Boolean.TRUE);
		 * 
		 * // user now has a single, native identity String apiKey =
		 * AuthenticationService.generateAPIKey();
		 * 
		 * // set the account DTO with an api key and new user id and send it
		 * back AccountDto accountDto = new AccountDto.Builder().apiKey(apiKey)
		 * .userId(user.getUserId()).build();
		 * 
		 * // Save UserId and APIKey in Redis cache database
		 * authenticationService.saveAuthkey(user.getUserId(), apiKey);
		 * 
		 * log.debug("Created user {}", user);
		 */

		return Response.ok().build();
	}

	/***
	 * This method authenticates developer via native login. Thereafter users
	 * can authenticate
	 * 
	 * @param accessToken
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/authenticated")
	@Produces(MediaType.APPLICATION_JSON)
	public Response authenticate(InputStream payload) throws IOException {

		return Response.ok().build();
	}

}
