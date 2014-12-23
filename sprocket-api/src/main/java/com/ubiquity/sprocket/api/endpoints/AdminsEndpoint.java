package com.ubiquity.sprocket.api.endpoints;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.identity.domain.Admin;
import com.ubiquity.identity.service.AuthenticationService;
import com.ubiquity.integration.api.exception.AuthorizationException;
import com.ubiquity.integration.domain.AdminInterest;
import com.ubiquity.sprocket.api.DtoAssembler;
import com.ubiquity.sprocket.api.dto.containers.AdminInterestsDto;
import com.ubiquity.sprocket.api.dto.model.AdminDto;
import com.ubiquity.sprocket.api.dto.model.AdminInterestDto;
import com.ubiquity.sprocket.api.validation.AuthenticationValidation;
import com.ubiquity.sprocket.service.ServiceFactory;

@Path("/1.0/admins")
public class AdminsEndpoint {

	private JsonConverter jsonConverter = JsonConverter.getInstance();

	private Logger log = LoggerFactory.getLogger(getClass());

	@POST
	@Path("/{adminId}/externalInterest")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getExternalInterests(InputStream payload) throws IOException {
		AdminInterestsDto externalInterests = jsonConverter.convertFromPayload(
				payload, AdminInterestsDto.class);

		List<AdminInterest> deleteList = new LinkedList<AdminInterest>();
		for (AdminInterestDto adminIterets : externalInterests.getDelete())
			deleteList.add(DtoAssembler.assemble(adminIterets));

		List<AdminInterest> addList = new LinkedList<AdminInterest>();
		for (AdminInterestDto adminIterets : externalInterests.getAdd())
			addList.add(DtoAssembler.assemble(adminIterets));
		Boolean finished = ServiceFactory.getAnalyticsService()
				.updateAdminInterests(deleteList, addList);
		if (finished)
			return Response.ok().build();
		return null;
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
		AdminDto adminDto = jsonConverter.convertFromPayload(payload,
				AdminDto.class, AuthenticationValidation.class);

		AuthenticationService authenticationService = ServiceFactory
				.getAuthenticationService();
		Admin admin = authenticationService.authenticateAdmin(
				adminDto.getUsername(), adminDto.getPassword());
		if (admin == null)
			throw new AuthorizationException("Username / password incorrect",
					null);

		// create api key and pass back associated rules for this admin

		String apiKey = AuthenticationService.generateAPIKey();
		
		adminDto = new AdminDto.Builder().apiKey(apiKey)
				.adminId(admin.getAdminId()).build();

		// Save admin and APIKey in Redis cache database
		authenticationService.saveAdminAuthkey(admin.getAdminId(), apiKey);

		log.debug("Authenticated admin {}", admin);

		return Response.ok().entity(jsonConverter.convertToPayload(adminDto))
				.build();
	}

}
