package com.ubiquity.sprocket.network.api.endpoints;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.sprocket.network.api.cache.CacheFactory;
import com.ubiquity.sprocket.network.api.googleplus.GooglePlusMockNetwork;
import com.ubiquity.sprocket.network.api.googleplus.model.GooglePersonDto;
import com.ubiquity.sprocket.network.api.googleplus.model.RefreshTokenResponseDto;

@Path("/1.0/googleplus")
public class GooglePlusEndPoint {

	private JsonConverter jsonConverter = JsonConverter.getInstance();

	@GET
	@Path("/people/me")
	@Produces("application/json")
	public Response getMe(@QueryParam("access_token") String accessToken) {
		if (accessToken != null) {
			Long userId = CacheFactory.findOrCreateUser(accessToken);
			GooglePersonDto contact = GooglePlusMockNetwork
					.Authenticate(userId);
			return Response.ok()
					.entity(jsonConverter.convertToPayload(contact)).build();
		} else {
			throw new IllegalArgumentException("access_token could not be null");
		}
	}

	@POST
	@Path("/o/oauth2/token")
	@Consumes("application/x-www-form-urlencoded")
	@Produces(MediaType.APPLICATION_JSON)
	public Response refreshToken(@FormParam("client_id") String clientId,
			@FormParam("client_secret") String clientSecret,
			@FormParam("refresh_token") String refreshToken,
			@FormParam("grant_type") String grantType) {
		if (refreshToken != null) {
			// Long userId = CacheFactory.findOrCreateUser(accessToken);
			RefreshTokenResponseDto contact = GooglePlusMockNetwork
					.refreshToken(refreshToken);
			return Response.ok()
					.entity(jsonConverter.convertToPayload(contact)).build();
		} else {
			return Response.ok().build();
		}
	}

}
