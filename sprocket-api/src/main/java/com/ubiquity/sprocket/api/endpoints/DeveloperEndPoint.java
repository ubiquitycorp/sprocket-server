package com.ubiquity.sprocket.api.endpoints;

import java.io.IOException;
import java.io.InputStream;

import javax.security.sasl.AuthenticationException;
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
import com.ubiquity.identity.domain.Developer;
import com.ubiquity.identity.service.AuthenticationService;
import com.ubiquity.identity.service.DeveloperAuthenticationService;
import com.ubiquity.sprocket.api.dto.model.developer.DeveloperDto;
import com.ubiquity.sprocket.api.dto.model.user.AccountDto;
import com.ubiquity.sprocket.api.dto.model.user.IdentityDto;
import com.ubiquity.sprocket.api.validation.AuthenticationValidation;
import com.ubiquity.sprocket.api.validation.RegistrationValidation;
import com.ubiquity.sprocket.service.ServiceFactory;

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

		DeveloperAuthenticationService authenticationService = ServiceFactory
				.getDeveloperAuthenticationService();
		Developer developer = authenticationService.register(null, null,
				developerDto.getDisplayName(), developerDto.getEmail(),
				developerDto.getUsername(), developerDto.getPassword());

		String apiKey = AuthenticationService.generateAPIKey();

		DeveloperDto account = new DeveloperDto.Builder()
				.developerId(developer.getDeveloperId()).apiKey(apiKey).build();

		// Save developerId and APIKey in Redis cache database
		authenticationService.saveAuthkey(developer.getDeveloperId(), apiKey);

		log.debug("Created Developer {}", developer);

		return Response.ok().entity(jsonConverter.convertToPayload(account))
				.build();
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

		DeveloperDto developerDto = jsonConverter.convertFromPayload(payload,
				DeveloperDto.class, AuthenticationValidation.class);
		DeveloperAuthenticationService developerAuthenticationService = ServiceFactory
				.getDeveloperAuthenticationService();
		Developer developer = developerAuthenticationService.authenticate(
				developerDto.getUsername(), developerDto.getPassword());
		if (developer == null)
			throw new AuthenticationException("Username / password incorrect",
					null);

		String apiKey = developerAuthenticationService
				.generateAPIKeyIfNotExsits(developer.getDeveloperId());

		DeveloperDto account = new DeveloperDto.Builder().apiKey(apiKey)
				.developerId(developer.getDeveloperId()).build();

		developerAuthenticationService.saveAuthkey(developer.getDeveloperId(),
				apiKey);

		log.debug("Authenticated developer {}", developer);

		return Response.ok().entity(jsonConverter.convertToPayload(account))
				.build();
	}

	/***
	 * This end point creates an sprocket application for the developer who
	 * invoked the request
	 * 
	 * @param payload
	 * @return
	 */
	@POST
	@Path("/{developerId}/applications/created")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createApplication(InputStream payload, @PathParam("developerId") Long developerId) {

		return Response.ok().build();
	}

}
