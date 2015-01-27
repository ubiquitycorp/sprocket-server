package com.ubiquity.sprocket.api.endpoints;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.sprocket.api.dto.model.user.AccountDto;
import com.ubiquity.sprocket.api.validation.MessageServiceAuthenticationValidation;
import com.ubiquity.sprocket.service.ServiceFactory;

@Path("/1.0/internal")
public class InternalServicesEndpoint {
	
	private JsonConverter jsonConverter = JsonConverter.getInstance();
		
	@POST
	@Path("/ms/users/authenticated")
	@Produces(MediaType.APPLICATION_JSON)
	public Response config(InputStream payload) throws IOException {
		AccountDto accountDto = jsonConverter.convertFromPayload(payload,
				AccountDto.class, MessageServiceAuthenticationValidation.class);
		
		Boolean isAuthenticated = ServiceFactory.getAuthenticationService().isUserAuthenticated(
				String.valueOf(accountDto.getUserId()), accountDto.getAuthToken());
		if(isAuthenticated)
			return Response.ok().build();
		else
			return Response.status(Status.UNAUTHORIZED).build();
	}
}
