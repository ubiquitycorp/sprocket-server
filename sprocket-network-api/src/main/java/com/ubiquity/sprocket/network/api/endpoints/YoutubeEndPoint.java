package com.ubiquity.sprocket.network.api.endpoints;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.sprocket.network.api.cache.CacheFactory;
import com.ubiquity.sprocket.network.api.dto.model.Category;
import com.ubiquity.sprocket.network.api.youtube.YoutubeMockNetwork;
import com.ubiquity.sprocket.network.api.youtube.dto.container.YouTubeItemsDto;

@Path("/1.0/youtube")
public class YoutubeEndPoint {
	
	private JsonConverter jsonConverter = JsonConverter.getInstance();
	
	@GET
    @Path("/v3/videos")
    @Produces(MediaType.APPLICATION_JSON)
	public Response mostPopular(@QueryParam("part") String part, @QueryParam("chart") String chart, @QueryParam("maxResults") Integer maxResults, @QueryParam("key") String apiKey, @HeaderParam("Authorization") String accessToken , @HeaderParam("If-None-Match") String etag){
		if(accessToken !=null){
			Long userId = CacheFactory.findOrCreateUser(accessToken);
			Long lastRequestTime = CacheFactory.getLastRequestTime(userId);
			Long thisRequestTime = System.currentTimeMillis();
			if(lastRequestTime == null){
				lastRequestTime = thisRequestTime;
				CacheFactory.setLastRequestTime(userId, lastRequestTime);
			}
			if(maxResults ==null)
				maxResults =10;
			YouTubeItemsDto youtubeItemDto = YoutubeMockNetwork.getVideos(userId, lastRequestTime, thisRequestTime,Category.MostPopular,maxResults);
			CacheFactory.checkUpdatedRequestTime(userId,lastRequestTime,thisRequestTime);
			return Response.ok().entity(jsonConverter.convertToPayload(youtubeItemDto)).build();
		}else{
			throw new IllegalArgumentException("accessToken could not be null");
		}
	}
    
    @GET
    @Path("/v3/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(@QueryParam("q") String q, @QueryParam("type") String type, @QueryParam("part") String part, @QueryParam("pageToken") String pageToken, @QueryParam("maxResults") Integer maxResults, @QueryParam("key") String apiKey, @HeaderParam("Authorization") String accessToken){
    	if(accessToken !=null){
			Long userId = CacheFactory.findOrCreateUser(accessToken);
			Long lastRequestTime = CacheFactory.getLastRequestTime(userId);
			Long thisRequestTime = System.currentTimeMillis();
			if(lastRequestTime == null){
				lastRequestTime = thisRequestTime;
				CacheFactory.setLastRequestTime(userId, lastRequestTime);
			}
			if(maxResults ==null)
				maxResults =10;
			YouTubeItemsDto youtubeItemDto = YoutubeMockNetwork.searchVideos(userId, lastRequestTime, thisRequestTime ,maxResults);
			CacheFactory.checkUpdatedRequestTime(userId,lastRequestTime,thisRequestTime);
			return Response.ok().entity(jsonConverter.convertToPayload(youtubeItemDto)).build();
		}else{
			throw new IllegalArgumentException("accessToken could not be null");
		}
	}
   
    @GET
    @Path("/v3/activities")
    @Produces(MediaType.APPLICATION_JSON)
    public Response activities(@QueryParam("part") String part,@QueryParam("channelId") String channelId, @QueryParam("maxResults") Integer maxResults, @QueryParam("key") String apiKey, @HeaderParam("Authorization") String accessToken){
    	if(accessToken !=null){
			Long userId = CacheFactory.findOrCreateUser(accessToken);
			Long lastRequestTime = CacheFactory.getLastRequestTime(userId);
			Long thisRequestTime = System.currentTimeMillis();
			if(lastRequestTime == null){
				lastRequestTime = thisRequestTime;
				CacheFactory.setLastRequestTime(userId, lastRequestTime);
			}
			if(maxResults ==null)
				maxResults =10;
			YouTubeItemsDto youtubeItemDto = YoutubeMockNetwork.getVideos(userId, lastRequestTime, thisRequestTime,Category.Subscriptions ,maxResults);
			CacheFactory.checkUpdatedRequestTime(userId,lastRequestTime,thisRequestTime);
			return Response.ok().entity(jsonConverter.convertToPayload(youtubeItemDto)).build();
		}else{
			throw new IllegalArgumentException("accessToken could not be null");
		}
	}
    
    @GET
    @Path("/v3/subscriptions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response subscriptions(@QueryParam("part") String part, @QueryParam("mine") String mine, @QueryParam("maxResults") Integer maxResults, @QueryParam("key") String apiKey, @HeaderParam("Authorization") String accessToken){
    	if(accessToken !=null){
			Long userId = CacheFactory.findOrCreateUser(accessToken);
			Long lastRequestTime = CacheFactory.getLastRequestTime(userId);
			Long thisRequestTime = System.currentTimeMillis();
			if(lastRequestTime == null){
				lastRequestTime = thisRequestTime;
				CacheFactory.setLastRequestTime(userId, lastRequestTime);
			}
			if(maxResults ==null)
				maxResults =10;
			YouTubeItemsDto youtubeItemDto = YoutubeMockNetwork.getSubscriptions(maxResults);
			CacheFactory.checkUpdatedRequestTime(userId,lastRequestTime,thisRequestTime);
			return Response.ok().entity(jsonConverter.convertToPayload(youtubeItemDto)).build();
		}else{
			throw new IllegalArgumentException("accessToken could not be null");
		}
	};
    
    @GET
    @Path("/v3/channels")
    @Produces(MediaType.APPLICATION_JSON)
    public Response channels(@QueryParam("part") String part, @QueryParam("mine") String mine, @QueryParam("maxResults") Integer maxResults, @QueryParam("key") String apiKey, @HeaderParam("Authorization") String accessToken){
    	if(accessToken !=null){
			Long userId = CacheFactory.findOrCreateUser(accessToken);
			Long lastRequestTime = CacheFactory.getLastRequestTime(userId);
			Long thisRequestTime = System.currentTimeMillis();
			if(lastRequestTime == null){
				lastRequestTime = thisRequestTime;
				CacheFactory.setLastRequestTime(userId, lastRequestTime);
			}
			if(maxResults ==null)
				maxResults =10;
			YouTubeItemsDto youtubeItemDto = YoutubeMockNetwork.getChannels(maxResults);
			CacheFactory.checkUpdatedRequestTime(userId,lastRequestTime,thisRequestTime);
			return Response.ok().entity(jsonConverter.convertToPayload(youtubeItemDto)).build();
		}else{
			throw new IllegalArgumentException("accessToken could not be null");
		}
	}
    
    @GET
    @Path("/v3/playlistItems")
    @Produces(MediaType.APPLICATION_JSON)
    public Response playlistItems(@QueryParam("part") String part, @QueryParam("mine") String mine, @QueryParam("maxResults") Integer maxResults,@QueryParam("playlistId") String playlistId, @QueryParam("key") String apiKey, @HeaderParam("Authorization") String accessToken, @HeaderParam("If-None-Match") String etag){
    	if(accessToken !=null){
			Long userId = CacheFactory.findOrCreateUser(accessToken);
			Long lastRequestTime = CacheFactory.getLastRequestTime(userId);
			Long thisRequestTime = System.currentTimeMillis();
			if(lastRequestTime == null){
				lastRequestTime = thisRequestTime;
				CacheFactory.setLastRequestTime(userId, lastRequestTime);
			}
			if(maxResults ==null)
				maxResults =10;
			YouTubeItemsDto youtubeItemDto = YoutubeMockNetwork.getVideos(userId, lastRequestTime, thisRequestTime,Category.MyHistory,maxResults);
			CacheFactory.checkUpdatedRequestTime(userId,lastRequestTime,thisRequestTime);
			return Response.ok().entity(jsonConverter.convertToPayload(youtubeItemDto)).build();
		}else{
			throw new IllegalArgumentException("accessToken could not be null");
		}
	}
    
}
