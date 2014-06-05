package com.ubiquity.social.api.youtube.endpoints;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.jboss.resteasy.client.ClientResponse;

public interface YouTubeApiEndpoints {

    @GET
    @Path("v3/videos")
    @Consumes("application/json")
    @Produces("application/json")
    ClientResponse<String> getVideos(@QueryParam("part") String part, @QueryParam("chart") String chart, @QueryParam("key") String apiKey, @HeaderParam("Authorization") String accessToken);
    
 
}
