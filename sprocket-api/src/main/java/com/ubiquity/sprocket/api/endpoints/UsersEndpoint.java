package com.ubiquity.sprocket.api.endpoints;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.common.serialize.JsonConverter;


@Path("/1.0/users")
public class UsersEndpoint {

	@SuppressWarnings("unused")
	private Logger log = LoggerFactory.getLogger(getClass());

	@SuppressWarnings("unused")
	private JsonConverter jsonConverter = JsonConverter.getInstance();

	@GET
	@Path("/ping")
	@Produces(MediaType.APPLICATION_JSON)
	public Response ping() {
		return Response.ok().entity("{\"message\":\"pong\"}").build();
	}
}