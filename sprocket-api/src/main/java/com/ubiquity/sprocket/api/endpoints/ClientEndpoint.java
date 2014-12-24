package com.ubiquity.sprocket.api.endpoints;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.sprocket.api.DtoAssembler;
import com.ubiquity.sprocket.api.dto.containers.ConfigurationDto;
import com.ubiquity.sprocket.api.dto.model.ExternalNetworkConfigurationDto;
import com.ubiquity.sprocket.service.ClientConfigurationService;
import com.ubiquity.sprocket.service.ServiceFactory;

@Path("/1.0/clients")
public class ClientEndpoint {
	
	private JsonConverter jsonConverter = JsonConverter.getInstance();
		
	@GET
	@Path("/configuration")
	@Produces(MediaType.APPLICATION_JSON)
	public Response config() throws IOException {
		ConfigurationDto results = new ConfigurationDto();
		ClientConfigurationService configurationService = ServiceFactory.getClientConfigurationService();
		results.getServices().putAll(
				configurationService.getServices());
		results.setRules(DtoAssembler.assembleConfigurationList(configurationService.getRules()));
		return Response.ok()
				.entity(jsonConverter.convertToPayload(results))
				.build();
	}
	
	@GET
	@Path("/networks")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getActiveNetworks() throws IOException {
		
		List<ExternalNetworkConfigurationDto> networks = DtoAssembler.getNetworks();
		
		return Response.ok()
				.entity(jsonConverter.convertToPayload(networks))
				.build();
	}
}
