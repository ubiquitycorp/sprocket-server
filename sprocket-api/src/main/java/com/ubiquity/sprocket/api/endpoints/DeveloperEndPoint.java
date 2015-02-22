package com.ubiquity.sprocket.api.endpoints;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

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
import com.ubiquity.identity.domain.Application;
import com.ubiquity.identity.domain.Developer;
import com.ubiquity.identity.domain.ExternalNetworkApplication;
import com.ubiquity.identity.service.AuthenticationService;
import com.ubiquity.identity.service.DeveloperService;
import com.ubiquity.integration.api.exception.AuthorizationException;
import com.ubiquity.sprocket.api.DtoAssembler;
import com.ubiquity.sprocket.api.dto.containers.ApplicationsDto;
import com.ubiquity.sprocket.api.dto.containers.ExternalApplicationsDto;
import com.ubiquity.sprocket.api.dto.model.developer.ApplicationDto;
import com.ubiquity.sprocket.api.dto.model.developer.DeveloperDto;
import com.ubiquity.sprocket.api.dto.model.developer.ExternalApplicationDto;
import com.ubiquity.sprocket.api.interceptors.DeveloperSecure;
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

		AuthenticationService<Developer> authenticationService = ServiceFactory
				.getDevloperAuthService();
		Developer developer = authenticationService.register(
				developerDto.getUsername(), developerDto.getPassword(), null,
				null, developerDto.getDisplayName(), developerDto.getEmail(),
				null, true);

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
	 * This method authenticates developer via native login. Thereafter
	 * developers can authenticate
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
		AuthenticationService<Developer> developerAuthService = ServiceFactory
				.getDevloperAuthService();
		Developer developer = developerAuthService.authenticate(
				developerDto.getUsername(), developerDto.getPassword());
		if (developer == null)
			throw new AuthorizationException("Username / password incorrect",
					null);

		String apiKey = developerAuthService
				.generateAPIKeyIfNotExsits(developer.getDeveloperId());

		DeveloperDto account = new DeveloperDto.Builder().apiKey(apiKey)
				.developerId(developer.getDeveloperId()).build();

		developerAuthService.saveAuthkey(developer.getDeveloperId(), apiKey);

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
	@DeveloperSecure
	@Produces(MediaType.APPLICATION_JSON)
	public Response createApplication(InputStream payload,
			@PathParam("developerId") Long developerId) throws IOException {
		ApplicationDto applicationDto = jsonConverter.convertFromPayload(
				payload, ApplicationDto.class);
		DeveloperService developerService = ServiceFactory
				.getDeveloperService();

		Developer developer = developerService.getDeveloperById(developerId);
		Application application = developerService.createApp(developer,
				applicationDto.getName(), applicationDto.getDescription());

		ApplicationDto developerApplication = new ApplicationDto.Builder()
				.appId(application.getAppId()).appKey(application.getAppKey())
				.appSecret(application.getAppSecret()).build();

		log.debug("Created Application {}", application);

		return Response.ok()
				.entity(jsonConverter.convertToPayload(developerApplication))
				.build();
	}

	/***
	 * 
	 * @param developerId
	 * @return
	 * @throws IOException
	 */
	@GET
	@Path("/{developerId}/applications")
	public Response getApplications(@PathParam("developerId") Long developerId)
			throws IOException {

		DeveloperService developerService = ServiceFactory
				.getDeveloperService();

		List<Application> applications = new LinkedList<Application>();
		applications = developerService
				.getApplicationsByDeveloperID(developerId);
		// convert each application to applicationDto :
		ApplicationsDto applicationsDto = new ApplicationsDto();

		for (Application application : applications) {
			applicationsDto.getApplications().add(
					DtoAssembler.assemble(application));
		}

		return Response.ok()
				.entity(jsonConverter.convertToPayload(applicationsDto))
				.build();
	}

	/***
	 * 
	 * @param developerId
	 * @param applicationId
	 * @return
	 * @throws IOException
	 */
	@GET
	@Path("/{developerId}/applications/{applicationId}")
	public Response getApplications(@PathParam("developerId") Long developerId,
			@PathParam("applicationId") Long applicationId) throws IOException {

		DeveloperService developerService = ServiceFactory
				.getDeveloperService();
		Application application;
		application = developerService.getApplicationByApplicationId(
				developerId, applicationId);

		ApplicationsDto applicationsDto = new ApplicationsDto();
		applicationsDto.getApplications().add(
				DtoAssembler.assemble(application));

		return Response.ok()
				.entity(jsonConverter.convertToPayload(applicationsDto))
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
	@Path("/{developerId}/applications/{applicationId}/external_apps/created")
	@DeveloperSecure
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createExternalApplication(InputStream payload,
			@PathParam("developerId") Long developerId,
			@PathParam("applicationId") Long applicationId) throws IOException {
		ExternalApplicationDto externalAppDto = jsonConverter
				.convertFromPayload(payload, ExternalApplicationDto.class);
		DeveloperService developerService = ServiceFactory
				.getDeveloperService();

		Application application = developerService
				.getApplicationByApplicationId(developerId, applicationId);

		boolean exists = developerService.isExist(
				externalAppDto.getConsumerKey(),
				externalAppDto.getConsumerSecret(),
				externalAppDto.getExternalNetworkId());
		if (exists)
			throw new IllegalArgumentException(
					"This application is already used");
		
		developerService.createExternalApplication(
				externalAppDto.getConsumerKey(),
				externalAppDto.getConsumerSecret(), externalAppDto.getApiKey(),
				externalAppDto.getToken(), externalAppDto.getTokenSecret(),
				externalAppDto.getUserAgent(), externalAppDto.getRedirectURL(),
				externalAppDto.getExternalNetworkId(),
				externalAppDto.getClientPlatformId(), application);

		return Response.ok().build();
	}
	/***
	 * Get external network applications by application Id 
	 * @param developerId
	 * @param applicationId
	 * @return the external network applications if the developer have access to this application 
	 * @throws IOException
	 */
	@GET
	@Path("/{developerId}/applications/{applicationId}/externalapplications")
	public Response getExternalApplicationByApplicationId(
			@PathParam("developerId") Long developerId,
			@PathParam("applicationId") Long applicationId) throws IOException {

		DeveloperService developerService = ServiceFactory
				.getDeveloperService();
		List<ExternalNetworkApplication> externalNetworkApplications = developerService
				.getExternalApplicationByApplicationId(developerId,
						applicationId);
		ExternalApplicationsDto result = new ExternalApplicationsDto() ;
		for (ExternalNetworkApplication externalNetworkApplication : externalNetworkApplications) {
			result.getExternalApplications().add(DtoAssembler.assemble(externalNetworkApplication));
		}
		return Response.ok()
				.entity(jsonConverter.convertToPayload(result))
				.build();

	}

}
