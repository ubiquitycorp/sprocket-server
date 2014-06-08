package com.ubiquity.sprocket.api.endpoints;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.sprocket.api.dto.containers.MostPopularDto;
import com.ubiquity.sprocket.domain.EventType;
import com.ubiquity.sprocket.service.ServiceFactory;

@Path("/1.0/analytics")
public class AnalyticsEndpoint {

	private JsonConverter jsonConverter = JsonConverter.getInstance();

	private Logger log = LoggerFactory.getLogger(getClass());
	
	@GET
	@Path("popular")
	@Produces(MediaType.APPLICATION_JSON)
	public Response search() throws IOException {

		MostPopularDto result = new MostPopularDto();
		Map<String, Long> searchTerms = ServiceFactory.getAnalyticsService().getTopOccurancesOf(EventType.Search, "q", 5);
		if(searchTerms.isEmpty())  {
			log.warn("Not enough data to report on search term, returning mock data");

			result.getSearchTerms().put("Obama", 34l);
			result.getSearchTerms().put("Cats", 22l);
			result.getSearchTerms().put("Dogs", 10l);
		} else {
			
			result.getSearchTerms().putAll(searchTerms);
		
		}
		
		Map<String, Long> socialNetworks = ServiceFactory.getAnalyticsService().getTopOccurancesOf(EventType.UserAddedIdentity, "social_network", 2);
		if(socialNetworks.isEmpty())  {
			log.warn("Not enough data to report on search term, returning mock data");
			result.getSearchTerms().put("Facebook", 12l);
			result.getSearchTerms().put("Google", 3l);
		} else {
			result.getSocialNetworks().putAll(socialNetworks);
		}
		
		
		
		

		

		return Response.ok().entity(jsonConverter.convertToPayload(result)).build();
	}

	
}


