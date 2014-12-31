package com.ubiquity.sprocket.api.endpoints;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.common.serialize.JsonConverter;
import com.niobium.repository.CollectionVariant;
import com.ubiquity.identity.domain.Admin;
import com.ubiquity.identity.service.AuthenticationService;
import com.ubiquity.integration.api.exception.AuthorizationException;
import com.ubiquity.integration.domain.AdminInterest;
import com.ubiquity.integration.domain.ExternalInterest;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.Interest;
import com.ubiquity.integration.domain.UnmappedInterest;
import com.ubiquity.sprocket.api.DtoAssembler;
import com.ubiquity.sprocket.api.dto.containers.AdminInterestsDto;
import com.ubiquity.sprocket.api.dto.containers.InterestsDto;
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
	
	@GET
	@Path("/{adminId}/interests")
	@Produces(MediaType.APPLICATION_JSON)
	public Response interests(@HeaderParam("If-Modified-Since") Long ifModifiedSince) {

		InterestsDto interestsDto = new InterestsDto();
		
		CollectionVariant<Interest> variant = ServiceFactory.getAnalyticsService().findInterests(ifModifiedSince);
		// Throw a 304 if if there is no variant (no change)
		if (variant == null)
			return Response.notModified().build();
		
		
		interestsDto.getInterests().addAll(DtoAssembler.assemble(variant.getCollection()));
		
		return Response.ok()
				.header("Last-Modified", variant.getLastModified())
				.entity(jsonConverter.convertToPayload(interestsDto))
				.build();
	}
	
	@GET
	@Path("/{adminId}/providers/{externalNetworkId}/externalinterests")
	@Produces(MediaType.APPLICATION_JSON)
	public Response networkExternalInterests(@PathParam("adminId") Long adminId, @PathParam("externalNetworkId") Integer externalNetworkId,@HeaderParam("If-Modified-Since") Long ifModifiedSince) {

		AdminInterestsDto interestsDto = new AdminInterestsDto();
		
		ExternalNetwork externalNetwork = ExternalNetwork.getNetworkById(externalNetworkId);
		CollectionVariant<ExternalInterest> variant = ServiceFactory.getAnalyticsService().findExternalInterestsByExternalNetworkId(externalNetwork);
		// Throw a 304 if if there is no variant (no change)
//		if (variant == null)
//			return Response.notModified().build();
		
		
		for(ExternalInterest interest : variant.getCollection())
			interestsDto.getInterests().add(DtoAssembler.assemble(interest));
		
		return Response.ok()
				//.header("Last-Modified", variant.getLastModified())
				.entity(jsonConverter.convertToPayload(interestsDto))
				.build();
	}
	
	@GET
	@Path("/{adminId}/providers/{externalNetworkId}/unmappedinterests")
	@Produces(MediaType.APPLICATION_JSON)
	public Response networkUnmappedInterests(@PathParam("adminId") Long adminId, @PathParam("externalNetworkId") Integer externalNetworkId,@HeaderParam("If-Modified-Since") Long ifModifiedSince) {

		AdminInterestsDto interestsDto = new AdminInterestsDto();
		
		ExternalNetwork externalNetwork = ExternalNetwork.getNetworkById(externalNetworkId);
		CollectionVariant<UnmappedInterest> variant = ServiceFactory.getAnalyticsService().findUnmappedInterestByExternalNetworkId(externalNetwork);
		// Throw a 304 if if there is no variant (no change)
//		if (variant == null)
//			return Response.notModified().build();
		
		
		for(UnmappedInterest unmappedInterest : variant.getCollection())
			interestsDto.getInterests().add(DtoAssembler.assemble(unmappedInterest));
		
		return Response.ok()
				//.header("Last-Modified", variant.getLastModified())
				.entity(jsonConverter.convertToPayload(interestsDto))
				.build();
	}

}
