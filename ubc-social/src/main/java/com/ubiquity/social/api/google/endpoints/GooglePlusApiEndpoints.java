package com.ubiquity.social.api.google.endpoints;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.jboss.resteasy.client.ClientResponse;

public interface GooglePlusApiEndpoints {

    @GET
    @Path("people/me")
    @Consumes("application/json")
    @Produces("application/json")
    ClientResponse<String> getMe(@QueryParam("access_token") String accessToken);
    
    @GET
    @Path("people/me/people/visible")
    @Consumes("application/json")
    @Produces("application/json")
    ClientResponse<String> getFriends(@QueryParam("access_token") String accessToken);
}
