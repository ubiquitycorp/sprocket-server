package com.ubiquity.sprocket.api.endpoints;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import com.niobium.common.serialize.JsonConverter;
import com.niobium.repository.CollectionVariant;
import com.ubiquity.location.domain.Place;
import com.ubiquity.sprocket.api.DtoAssembler;
import com.ubiquity.sprocket.api.dto.containers.PlacesDto;
import com.ubiquity.sprocket.service.ServiceFactory;
import com.ubiquity.external.domain.ExternalNetwork;

@Path("/1.0/places")
public class PlacesEndpoint {
	//private Logger log = LoggerFactory.getLogger(getClass());
	private JsonConverter jsonConverter = JsonConverter.getInstance();
	
	@GET
	@Path("/users/{userId}/locale/{locale}/neighborhoods")
	@Produces(MediaType.APPLICATION_JSON)
	public Response neighborhoods(@PathParam("userId") Long userId,@PathParam("locale") String locale, @HeaderParam("delta") Boolean delta, @HeaderParam("If-Modified-Since")Long ifModifiedSince) throws IOException {
		PlacesDto results = new PlacesDto();

		//ExternalNetwork socialNetwork = ExternalNetwork.getNetworkById(socialProviderId);

		CollectionVariant<Place> variant = ServiceFactory.getLocationService().getAllCitiesAndNeighborhoods(locale,ifModifiedSince,delta);

		// Throw a 304 if if there is no variant (no change)
		if (variant == null)
			return Response.notModified().build();
		
		for(Place place : variant.getCollection()) {
			results.getPlaces().add(DtoAssembler.assemble(place));
		}

		return Response.ok()
				.header("Last-Modified", variant.lastModified)
				.entity(jsonConverter.convertToPayload(results))
				.build();
	}
	@GET
	@Path("/users/{userId}/provider/{externalNetworkId}/location/{placeId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response neighborhoods(@PathParam("userId") Long userId, @PathParam("socialNetworkId") Integer socialProviderId ,@PathParam("placeId") String placeId, @QueryParam("interestId") Long interestId) throws IOException {
		
		PlacesDto results = new PlacesDto();
		ExternalNetwork externalNetwork = ExternalNetwork.getNetworkById(socialProviderId);
		
		List<Place> places = ServiceFactory.getLocationService().findPlacesByInterestId(interestId, externalNetwork);
		
		for(Place place : places) {
			results.getPlaces().add(DtoAssembler.assemble(place));
		}

		return Response.ok()
				//.header("Last-Modified", places.lastModified)
				.entity(jsonConverter.convertToPayload(results))
				.build();
	}
}
