package com.ubiquity.sprocket.network.api.endpoints;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.sprocket.network.api.cache.CacheFactory;
import com.ubiquity.sprocket.network.api.facebook.FacebookMockNetwork;
import com.ubiquity.sprocket.network.api.facebook.dto.container.FacebookDataDto;
import com.ubiquity.sprocket.network.api.facebook.dto.model.FacebookBatchResponseDto;
import com.ubiquity.sprocket.network.api.facebook.dto.model.FacebookContactDto;

@Path("/1.0/facebook/")
public class FacebookEndPoint {

	private JsonConverter jsonConverter = JsonConverter.getInstance();

	@GET
	@Path("me/activities")
	@Produces("application/json")
	public Response getActivities(@QueryParam("access_token") String accessToken,
			@QueryParam("date_format") String dateFormat) {
		return null;
	}

	@GET
	@Path("me/inbox")
	@Produces("application/json")
	public Response getInbox(@QueryParam("access_token") String accessToken,
			@QueryParam("date_format") String dateFormat) {
		if(accessToken !=null){
			Long userId = CacheFactory.findOrCreateUser(accessToken);
			Long lastRequestTime = CacheFactory.getLastRequestTime(userId);
			Long thisRequestTime = System.currentTimeMillis();
			if(lastRequestTime == null){
				lastRequestTime = thisRequestTime;
				CacheFactory.setLastRequestTime(userId, lastRequestTime);
			}
			FacebookDataDto facebookData = FacebookMockNetwork.getInbox(userId, lastRequestTime, thisRequestTime);
			CacheFactory.checkUpdatedRequestTime(userId,lastRequestTime,thisRequestTime);
			return Response.ok().entity(jsonConverter.convertToPayload(facebookData)).build();
		}else{
			throw new IllegalArgumentException("accessToken could not be null");
		}
	}

	@GET
	@Path("me")
	@Produces("application/json")
	public Response getMe(@QueryParam("access_token") String accessToken,
			@QueryParam("fields") String fields,
			@QueryParam("date_format") String dateFormat) {
		if (accessToken != null) {
			Long userId = CacheFactory.findOrCreateUser(accessToken);
			FacebookContactDto contact = FacebookMockNetwork
					.Authenticate(userId);
			return Response.ok()
					.entity(jsonConverter.convertToPayload(contact)).build();
		} else {
			throw new IllegalArgumentException("access_token could not be null");
		}
	}

	@GET
	@Path("me/friends")
	@Produces("application/json")
	public Response getFriends(@QueryParam("access_token") String accessToken,
			@QueryParam("fields") String fields,
			@QueryParam("date_format") String dateFormat) {
		Long userId = CacheFactory.findOrCreateUser(accessToken);
		FacebookDataDto facebookData = FacebookMockNetwork.getFriends(userId);
		return Response.ok().entity(jsonConverter.convertToPayload(facebookData)).build();
	}

	@GET
	@Path("{profile}/feed")
	@Produces("application/json")
	public Response getFeed(@QueryParam("access_token") String accessToken,
			@PathParam("profile") String profile,
			@QueryParam("date_format") String dateFormat) {
		return null;
	}

	@GET
	@Path("me/feed")
	@Produces("application/json")
	public Response getFeed(@QueryParam("access_token") String accessToken,
			@QueryParam("date_format") String dateFormat) {
		return null;
	}

	@GET
	@Path("me/home")
	@Produces("application/json")
	public Response getNewsFeed(@QueryParam("access_token") String accessToken,
			@QueryParam("date_format") String dateFormat) {
		if(accessToken !=null){
			Long userId = CacheFactory.findOrCreateUser(accessToken);
			Long lastRequestTime = CacheFactory.getLastRequestTime(userId);
			Long thisRequestTime = System.currentTimeMillis();
			if(lastRequestTime == null){
				lastRequestTime = thisRequestTime;
				CacheFactory.setLastRequestTime(userId, lastRequestTime);
			}
			FacebookDataDto facebookData = FacebookMockNetwork.getNewsFeed(userId, lastRequestTime, thisRequestTime,20);
			CacheFactory.checkUpdatedRequestTime(userId,lastRequestTime,thisRequestTime);
			return Response.ok().entity(jsonConverter.convertToPayload(facebookData)).build();
		}else{
			throw new IllegalArgumentException("accessToken could not be null");
		}
	}

	@GET
	@Path("search")
	@Produces("application/json")
	public Response search(@QueryParam("access_token") String accessToken,
			@QueryParam("type") String type, @QueryParam("q") String q,
			@QueryParam("fields") String fields,
			@QueryParam("center") String center,
			@QueryParam("distance") Integer distance,
			@QueryParam("limit") Integer limit,
			@QueryParam("offset") Integer offset,
			@QueryParam("date_format") String dateFormat) {
		FacebookDataDto facebookData = FacebookMockNetwork.search(10);
		return Response.ok().entity(jsonConverter.convertToPayload(facebookData)).build();
	}

	@POST
	@Path("/")
	@Produces("application/json")
	public Response batch(@QueryParam("access_token") String accessToken,
			@QueryParam("batch") String batch,
			@QueryParam("include_headers") Boolean includeHeaders,
			@QueryParam("date_format") String dateFormat) {
		if(accessToken !=null){
			Long userId = CacheFactory.findOrCreateUser(accessToken);
			Long lastRequestTime = CacheFactory.getLastRequestTime(userId);
			Long thisRequestTime = System.currentTimeMillis();
			if(lastRequestTime == null){
				lastRequestTime = thisRequestTime;
				CacheFactory.setLastRequestTime(userId, lastRequestTime);
			}
			List<FacebookBatchResponseDto> facebookData = FacebookMockNetwork.getbatchActivity(userId, lastRequestTime, thisRequestTime,10);
			CacheFactory.checkUpdatedRequestTime(userId,lastRequestTime,thisRequestTime);
			return Response.ok().entity(jsonConverter.convertToPayload(facebookData)).build();
		}else{
			throw new IllegalArgumentException("accessToken could not be null");
		}
	}

	@GET
	@Path("/oauth/access_token")
	@Produces("application/json")
	public Response exchangeAccessToken(@QueryParam("grant_type") String grantType,
			@QueryParam("client_id") String clientId,
			@QueryParam("client_secret") String clientSecret,
			@QueryParam("fb_exchange_token") String shortLivedAccessToken) {
		String accessToken = "sdjshdj="+shortLivedAccessToken+"&kjsadghkjasgdh"; 

		return Response.ok().entity(jsonConverter.convertToPayload(accessToken)).build();
	}

}
