package com.ubiquity.sprocket.api.endpoints;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.identity.domain.Application;
import com.ubiquity.identity.domain.ExternalNetworkApplication;
import com.ubiquity.identity.service.DeveloperService;
import com.ubiquity.sprocket.api.DtoAssembler;
import com.ubiquity.sprocket.api.dto.containers.ConfigurationDto;
import com.ubiquity.sprocket.api.dto.containers.ExternalApplicationsDto;
import com.ubiquity.sprocket.api.dto.model.ExternalNetworkConfigurationDto;
import com.ubiquity.sprocket.api.dto.model.developer.ApplicationDto;
import com.ubiquity.sprocket.api.validation.AuthenticationValidation;
import com.ubiquity.sprocket.service.ClientConfigurationService;
import com.ubiquity.sprocket.service.ServiceFactory;

@Path("/1.0/clients")
public class ClientEndpoint {

	private JsonConverter jsonConverter = JsonConverter.getInstance();
	private DeveloperService developerService = ServiceFactory.getDeveloperService();
	private ClientConfigurationService configurationService = ServiceFactory
			.getClientConfigurationService();

	@GET
	@Path("/configuration")
	@Produces(MediaType.APPLICATION_JSON)
	public Response config() throws IOException {
		ConfigurationDto results = new ConfigurationDto();
		
		results.getServices().putAll(configurationService.getServices());
		results.setRules(DtoAssembler
				.assembleConfigurationList(configurationService.getRules()));
		return Response.ok().entity(jsonConverter.convertToPayload(results))
				.build();
	}

	@POST
	@Path("/configuration/external_apps")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getExternalApplications(InputStream payload)
			throws IOException {
		ApplicationDto applicationDto = jsonConverter.convertFromPayload(
				payload, ApplicationDto.class, AuthenticationValidation.class);
		
		Application application = developerService
				.getApplicationByAppkeyAndAppSecret(applicationDto.getAppKey(),
						applicationDto.getAppSecret());
		
		List<ExternalNetworkApplication> externalNetworkApplications = developerService.getExternalApplicationByApplicationId(
						application.getOwner().getDeveloperId(), application.getAppId());
		
		ExternalApplicationsDto result = new ExternalApplicationsDto();
		for (ExternalNetworkApplication externalNetworkApplication : externalNetworkApplications) {
			result.getExternalApplications().add(
					DtoAssembler.assemble(externalNetworkApplication));
		}
		return Response.ok().entity(jsonConverter.convertToPayload(result))
				.build();

	}

	@GET
	@Path("/networks")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getActiveNetworks() throws IOException {

		List<ExternalNetworkConfigurationDto> networks = DtoAssembler
				.getNetworks();

		return Response.ok().entity(jsonConverter.convertToPayload(networks))
				.build();
	}
}
