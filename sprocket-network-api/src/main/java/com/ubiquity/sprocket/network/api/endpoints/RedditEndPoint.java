package com.ubiquity.sprocket.network.api.endpoints;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.sprocket.network.api.cache.CacheFactory;
import com.ubiquity.sprocket.network.api.googleplus.model.RefreshTokenResponseDto;
import com.ubiquity.sprocket.network.api.reddit.RedditMockNetwork;
import com.ubiquity.sprocket.network.api.reddit.dto.container.RedditCommentDataContainerDto;
import com.ubiquity.sprocket.network.api.reddit.dto.container.RedditPostDataContainerDto;
import com.ubiquity.sprocket.network.api.reddit.dto.model.RedditContactDto;

@Path("/1.0/reddit")
public class RedditEndPoint {
	private JsonConverter jsonConverter = JsonConverter.getInstance();
	
	@GET
    @Path("/api/v1/me")
    @Produces("application/json")
    public Response getMe(@HeaderParam("Authorization") String accessToken,@HeaderParam("User-Agent") String userAgent){
		if (accessToken != null) {
			Long userId = CacheFactory.findOrCreateUser(accessToken);
			RedditContactDto contact = RedditMockNetwork
					.Authenticate(userId);
			return Response.ok()
					.entity(jsonConverter.convertToPayload(contact)).build();
		} else {
			throw new IllegalArgumentException("access_token could not be null");
		}
	}
	
	@GET
    @Path("/inbox.json")
    @Produces("application/json")
	@Consumes("application/json")
    public Response getInbox(@HeaderParam("Authorization") String accessToken,@QueryParam("limit") String limit,@HeaderParam("show") String show){
		// TODO Auto-generated method stub
		return Response.ok().build();
	}
	
	@GET
    @Path("//hot")
    @Produces("application/json")
	@Consumes("application/json")
    public Response getHotPosts(@HeaderParam("Authorization") String accessToken,@HeaderParam("User-Agent") String userAgent,@QueryParam("limit") Integer limit){
		if(accessToken !=null){
			Long userId = CacheFactory.findOrCreateUser(accessToken);
			Long lastRequestTime = CacheFactory.getLastRequestTime(userId);
			Long thisRequestTime = System.currentTimeMillis();
			if(lastRequestTime == null){
				lastRequestTime = thisRequestTime;
				CacheFactory.setLastRequestTime(userId, lastRequestTime);
			}
			RedditPostDataContainerDto redditData = RedditMockNetwork.getHotPosts(userId, lastRequestTime, thisRequestTime,limit,"");
			CacheFactory.checkUpdatedRequestTime(userId,lastRequestTime,thisRequestTime);
			return Response.ok().entity(jsonConverter.convertToPayload(redditData)).build();
		}else{
			throw new IllegalArgumentException("accessToken could not be null");
		}
	}
	
	@GET
    @Path("/r/{subReddit}/comments/{article}")
    @Produces("application/json")
	@Consumes("application/json")
    public Response getComments(@HeaderParam("Authorization") String accessToken,@HeaderParam("User-Agent") String userAgent,@QueryParam("limit") Integer limit ,@PathParam("subReddit") String subReddit,@PathParam("article") String article){
		if(accessToken !=null){
			Long userId = CacheFactory.findOrCreateUser(accessToken);
			Long lastRequestTime = CacheFactory.getLastRequestTime(userId);
			Long thisRequestTime = System.currentTimeMillis();
			if(lastRequestTime == null){
				lastRequestTime = thisRequestTime;
				CacheFactory.setLastRequestTime(userId, lastRequestTime);
			}
			List<RedditCommentDataContainerDto> redditData = RedditMockNetwork.getComments(userId, lastRequestTime, thisRequestTime,20, article);
			CacheFactory.checkUpdatedRequestTime(userId,lastRequestTime,thisRequestTime);
			return Response.ok().entity(jsonConverter.convertToPayload(redditData)).build();
		}else{
			throw new IllegalArgumentException("accessToken could not be null");
		}
		
	}
	
	@GET
    @Path("//search")
    @Produces("application/json")
	@Consumes("application/json")
	public Response liveSearch(@HeaderParam("Authorization") String accessToken,@HeaderParam("User-Agent") String userAgent,@QueryParam("q") String searchTerm, @QueryParam("limit") Integer limit, @QueryParam("after") String after){
		if(accessToken !=null){
			Long userId = CacheFactory.findOrCreateUser(accessToken);
			Long lastRequestTime = CacheFactory.getLastRequestTime(userId);
			Long thisRequestTime = System.currentTimeMillis();
			if(lastRequestTime == null){
				lastRequestTime = thisRequestTime;
				CacheFactory.setLastRequestTime(userId, lastRequestTime);
			}
			RedditPostDataContainerDto redditData = RedditMockNetwork.getHotPosts(userId, lastRequestTime, thisRequestTime,limit,searchTerm);
			CacheFactory.checkUpdatedRequestTime(userId,lastRequestTime,thisRequestTime);
			return Response.ok().entity(jsonConverter.convertToPayload(redditData)).build();
		}else{
			throw new IllegalArgumentException("accessToken could not be null");
		}
	}
	
	@POST
	@Path("//api/submit")
	@Produces("application/json")
	@Consumes("application/json")
	public Response postActivity(@HeaderParam("Authorization") String accessToken,@HeaderParam("User-Agent") String userAgent,@QueryParam("sr") String subReddit ,@QueryParam("text") String text,@QueryParam("title") String title,@QueryParam("url") String url,@QueryParam("kind") String kind ,@QueryParam("captcha") String captcha,@QueryParam("iden") String captchaIden,@QueryParam("api_type") String apiType){
		Long userId = CacheFactory.findOrCreateUser(accessToken);
		RedditMockNetwork.postActivity(userId, text, title, url, kind);
		return Response.ok()
				.entity(jsonConverter.convertToPayload(RedditMockNetwork.getjsonResponse())).build();
	}
	
	@POST
    @Path("/api/comment")
    @Produces("application/json")
	@Consumes("application/json")
    public Response postComment(@HeaderParam("Authorization") String accessToken,@HeaderParam("User-Agent") String userAgent,@QueryParam("thing_id") String thing_id ,@QueryParam("text") String text,@QueryParam("api_type") String apiType){
		return Response.ok()
				.entity(jsonConverter.convertToPayload(RedditMockNetwork.getjsonResponse())).build();
	}
	
	@POST
    @Path("/api/vote")
    @Produces("application/json")
	@Consumes("application/json")
    public Response postVote(@HeaderParam("Authorization") String accessToken,@HeaderParam("User-Agent") String userAgent,@QueryParam("id") String thing_id ,@QueryParam("dir") int direction,@QueryParam("api_type") String apiType){
		return Response.ok()
				.entity(jsonConverter.convertToPayload(RedditMockNetwork.getjsonResponse())).build();
	}
	
	@POST
    @Path("/api/new_captcha")
    @Produces("application/json")
	@Consumes("application/json")
    public Response newCaptcha(@HeaderParam("Authorization") String accessToken,@HeaderParam("User-Agent") String userAgent,@QueryParam("api_type") String apiType){
		// TODO Auto-generated method stub
		return null;
	}
	
	@GET
    @Path("/captcha/{iden}")
    @Produces("application/json")
	@Consumes("image/png")
    public Response getCaptcha(@HeaderParam("Authorization") String accessToken,@HeaderParam("User-Agent") String userAgent, @PathParam("iden") String iden){
		// TODO Auto-generated method stub
		return null;
	}
	
	@POST
    @Path("/api/v1/access_token")
    @Produces("application/json")
    public Response accessToken(@HeaderParam("Authorization") String authorization,@HeaderParam("User-Agent") String userAgent,@FormParam("grant_type") String grantType,   @FormParam("code") String code,@FormParam("redirect_uri") String redirectUri){
		// TODO Auto-generated method stub
		return null;
	}
	
	@POST
    @Path("/api/v1/access_token")
    @Produces("application/json")
    public Response refreshToken(@HeaderParam("Authorization") String authorization,@HeaderParam("User-Agent") String userAgent,@FormParam("grant_type") String grantType,  @FormParam("refresh_token") String refreshToken){
		RefreshTokenResponseDto refreshTokenResponseDto =new RefreshTokenResponseDto.Builder().access_token(refreshToken).expires_in(3600L).scope(UUID.randomUUID().toString()).token_type(UUID.randomUUID().toString()).build();
		return Response.ok()
				.entity(jsonConverter.convertToPayload(refreshTokenResponseDto)).build();
	}
	
}
