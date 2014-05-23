package com.ubiquity.social.api.facebook.endpoints;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.jboss.resteasy.client.ClientResponse;

public interface FacebookGraphApiEndpoints {
	
    @GET
    @Path("{userId}/events/")
    @Consumes("application/json")
    @Produces("application/json")
    ClientResponse<String> getEvents(@PathParam("userId") Long userId, @QueryParam("access_token") String accessToken);

    @GET
    @Path("me/activities")
    @Consumes("application/json")
    @Produces("application/json")
    ClientResponse<String> getActivities(@QueryParam("access_token") String accessToken);

    @GET
    @Path("me/inbox")
    @Produces("application/json")
    ClientResponse<String> getInbox(@QueryParam("access_token") String accessToken);
    
    @GET
    @Path("me")
    @Consumes("application/json")
    @Produces("application/json")
    ClientResponse<String> getMe(@QueryParam("access_token") String accessToken);
    
    @GET
    @Path("me/friends")
    @Consumes("application/json")
    @Produces("application/json")
    ClientResponse<String> getFriends(@QueryParam("access_token") String accessToken, @QueryParam("fields") String fields);

    @GET
    @Path("me/feed")
    @Consumes("application/json")
    @Produces("application/json")
	ClientResponse<String> getFeed(@QueryParam("access_token") String accessToken);
}
