package com.ubiquity.sprocket.network.api.endpoints;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.sprocket.network.api.dto.model.Contact;
import com.ubiquity.sprocket.network.api.random.generator.RandomObjectGenerator;

@Path("/1.0/users")
public class UsersEndpoint {
	
	private JsonConverter jsonConverter = JsonConverter.getInstance();
	private Logger log = LoggerFactory.getLogger(getClass());

	@GET
	@Path("/ping")
	@Produces(MediaType.APPLICATION_JSON)
	public Response ping() {
		log.info("calling ping endpoint");
		return Response.ok().entity("{\"message\":\"pong\"}").build();
	}
	
	@GET
	@Path("/{userId}/authenticate")
	@Produces(MediaType.APPLICATION_JSON)
	public Response authenticate(@PathParam("userId") Long userId) {
		if(userId !=null){
			Contact contact = RandomObjectGenerator.generateContact(userId, null);
			return Response.ok().entity(jsonConverter.convertToPayload(contact)).build();
		}else{
			throw new IllegalArgumentException("userId could not be null");
		}
	}

	
}