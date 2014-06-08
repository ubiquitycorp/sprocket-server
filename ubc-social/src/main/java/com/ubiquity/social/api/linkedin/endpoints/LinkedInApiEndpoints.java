package com.ubiquity.social.api.linkedin.endpoints;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.jboss.resteasy.client.ClientResponse;

public interface LinkedInApiEndpoints {
	
    @GET
    @Path("people/~:(id,first-name,last-name,formatted-name,email-address,picture-url,public-profile-url)")
    @Consumes("application/json")
    @Produces("application/json")
    ClientResponse<String> getProfile(@QueryParam("oauth2_access_token") String accessToken, @QueryParam("format") String format);
    
    @GET
    @Path("people/~/connections:(id,first-name,last-name,formatted-name,picture-url,public-profile-url)")
    @Consumes("application/json")
    @Produces("application/json")
    ClientResponse<String> getConnections(@QueryParam("oauth2_access_token") String accessToken, @QueryParam("modified") String modified, @QueryParam("format") String format);

    @POST
    @Path("people/~/mailbox")
    @Consumes("application/json")
    @Produces("application/json")
    ClientResponse<String> postMessage(InputStream payload, @QueryParam("oauth2_access_token") String accessToken);

}
