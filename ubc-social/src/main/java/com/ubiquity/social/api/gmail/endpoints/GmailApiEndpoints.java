package com.ubiquity.social.api.gmail.endpoints;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jboss.resteasy.client.ClientResponse;

public interface GmailApiEndpoints {
	@GET
	@Path("mail/feed/atom")
	@Produces("application/atom+xml")
	public ClientResponse<String> getFeed(@HeaderParam("Authorization") String bearerToken);

}
