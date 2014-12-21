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
import com.ubiquity.integration.domain.AdminInterest;
import com.ubiquity.sprocket.api.DtoAssembler;
import com.ubiquity.sprocket.api.dto.containers.AdminInterestsDto;
import com.ubiquity.sprocket.api.dto.model.AdminInterestDto;
import com.ubiquity.sprocket.service.ServiceFactory;

@Path("/1.0/admins")
public class AdminsEndpoint {

	private JsonConverter jsonConverter = JsonConverter.getInstance();

	private Logger log = LoggerFactory.getLogger(getClass());

	@POST
	@Path("/{adminId}/externalInterest")
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(InputStream payload) throws IOException {
		AdminInterestsDto externalInterests = jsonConverter.convertFromPayload(
				payload, AdminInterestsDto.class);
		
		List<AdminInterest> deleteList = new LinkedList<AdminInterest>();
		for (AdminInterestDto adminIterets : externalInterests.getDelete())
			deleteList.add(DtoAssembler.assemble(adminIterets));
		
		List<AdminInterest> addList = new LinkedList<AdminInterest>();
		for (AdminInterestDto adminIterets : externalInterests.getAdd())
			addList.add(DtoAssembler.assemble(adminIterets));
		Boolean finished = ServiceFactory.getAnalyticsService()
				.updateAdminInterests(deleteList,addList);
		if (finished)
			return Response.ok().build();
		return null;
	}

}
