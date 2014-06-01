package com.ubiquity.content.api.youtube.endpoints;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.ClientResponse;

public interface YouTubeApiEndpoints {

    @GET
    @Path("v3/videos")
    @Produces(MediaType.APPLICATION_JSON)
    ClientResponse<String> getVideos(@QueryParam("part") String part, @QueryParam("chart") String chart, @QueryParam("key") String apiKey, @HeaderParam("Authorization") String accessToken);
    
 
}
