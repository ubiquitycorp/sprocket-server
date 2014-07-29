package com.ubiquity.sprocket.api.endpoints;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.sprocket.api.dto.containers.RecommendationsDto;

@Path("/1.0/analytics")
public class AnalyticsEndpoint {

	private JsonConverter jsonConverter = JsonConverter.getInstance();

	@GET
	@Path("users/{userId}/recommendations")
	@Produces(MediaType.APPLICATION_JSON)
	public Response recommendations(@PathParam("userId") Long userId) {

		RecommendationsDto recommendationsDto = new RecommendationsDto();
		
		return Response.ok().entity(null).build();
	}
}
