package com.ubiquity.sprocket.network.api.endpoints;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.sprocket.network.api.cache.CacheFactory;
import com.ubiquity.sprocket.network.api.dto.model.VideoContent;
import com.ubiquity.sprocket.network.api.random.generator.RandomListGenerator;

@Path("/1.0/content")
public class ContentEndPoint {
	
	private JsonConverter jsonConverter = JsonConverter.getInstance();
	
	@GET
	@Path("/{userId}/videos")
	@Produces(MediaType.APPLICATION_JSON)
	public Response GetVideos(@PathParam("userId") Long userId) {
		if(userId !=null){
			Long lastRequestTime = CacheFactory.getLastRequestTime(userId);
			Long thisRequestTime = System.currentTimeMillis();
			if(lastRequestTime == null){
				lastRequestTime = thisRequestTime;
				CacheFactory.setLastRequestTime(userId, lastRequestTime);
			}
			List<VideoContent> videos = RandomListGenerator
					.GenerateVideoList(userId, lastRequestTime, thisRequestTime);
			
			CacheFactory.checkUpdatedRequestTime(userId,lastRequestTime,thisRequestTime);
			return Response.ok().entity(jsonConverter.convertToPayload(videos)).build();
		}else{
			throw new IllegalArgumentException("userId could not be null");
		}
	}
}
