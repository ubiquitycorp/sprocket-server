package com.ubiquity.sprocket.api.endpoints;

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.niobium.common.serialize.JsonConverter;
import com.niobium.repository.CollectionVariant;
import com.ubiquity.integration.domain.Activity;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.Interest;
import com.ubiquity.integration.domain.VideoContent;
import com.ubiquity.sprocket.api.DtoAssembler;
import com.ubiquity.sprocket.api.dto.containers.InterestsDto;
import com.ubiquity.sprocket.api.dto.containers.RecommendationsDto;
import com.ubiquity.sprocket.api.interceptors.Secure;
import com.ubiquity.sprocket.service.AnalyticsService;
import com.ubiquity.sprocket.service.ServiceFactory;

@Path("/1.0/analytics")
public class AnalyticsEndpoint {

	private JsonConverter jsonConverter = JsonConverter.getInstance();

	@GET
	@Path("users/{userId}/recommendations")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response recommendations(@PathParam("userId") Long userId) {
		throw new UnsupportedOperationException("This endpoint is currently not supported");
	}


	@GET
	@Path("users/{userId}/interests")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response interests(@HeaderParam("If-Modified-Since") Long ifModifiedSince) {

		InterestsDto interestsDto = new InterestsDto();
		
		CollectionVariant<Interest> variant = ServiceFactory.getAnalyticsService().findInterests(ifModifiedSince);
		// Throw a 304 if if there is no variant (no change)
		if (variant == null)
			return Response.notModified().build();
		
		
		interestsDto.getInterests().addAll(DtoAssembler.assemble(variant.getCollection()));
		
		return Response.ok()
				.header("Last-Modified", variant.getLastModified())
				.entity(jsonConverter.convertToPayload(interestsDto))
				.build();
	}

	@GET
	@Path("users/{userId}/providers/{externalNetworkId}/externalinterests")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response externalInterests(@PathParam("userId") Long userId, @PathParam("externalNetworkId") Integer externalNetworkId,@HeaderParam("If-Modified-Since") Long ifModifiedSince) {

		InterestsDto interestsDto = new InterestsDto();
		
		ExternalNetwork externalNetwork = ExternalNetwork.getNetworkById(externalNetworkId);
		CollectionVariant<Interest> variant = ServiceFactory.getAnalyticsService().findInterestsByExternalNetworkId(externalNetwork, ifModifiedSince);
		// Throw a 304 if if there is no variant (no change)
//		if (variant == null)
//			return Response.notModified().build();
		
		
		for(Interest interest : variant.getCollection())
			interestsDto.getInterests().add(DtoAssembler.assemble(interest));
		
		return Response.ok()
				//.header("Last-Modified", variant.getLastModified())
				.entity(jsonConverter.convertToPayload(interestsDto))
				.build();
	}
	@GET
	@Path("users/{userId}/providers/{externalNetworkId}/activities/recommended")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response recommendedActivitiesByProvider(@PathParam("userId") Long userId, @PathParam("externalNetworkId") Integer externalNetworkId, @HeaderParam("If-Modified-Since") Long ifModifiedSince) {

		RecommendationsDto recommendationsDto = new RecommendationsDto();
		AnalyticsService analyticsService = ServiceFactory.getAnalyticsService();

		ExternalNetwork externalNetwork = ExternalNetwork.getNetworkById(externalNetworkId);

		CollectionVariant<Activity> variant = analyticsService.findAllRecommendedActivities(userId, externalNetwork, ifModifiedSince);

		// Throw a 304 if if there is no variant (no change)
		if (variant == null)
			return Response.notModified().build();

		Collection<Activity> activities = variant.getCollection();
		for(Activity activity : activities) {
			recommendationsDto.getActivities().add(DtoAssembler.assemble(activity));
		}

		return Response.ok()
				.header("Last-Modified", variant.getLastModified())
				.entity(jsonConverter.convertToPayload(recommendationsDto))
				.build();
	}

	@GET
	@Path("users/{userId}/providers/{externalNetworkId}/videos/recommended")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response recommendedVideosByProvider(@PathParam("userId") Long userId, @PathParam("externalNetworkId") Integer externalNetworkId, @HeaderParam("If-Modified-Since") Long ifModifiedSince) {

		RecommendationsDto recommendationsDto = new RecommendationsDto();
		AnalyticsService analyticsService = ServiceFactory.getAnalyticsService();

		ExternalNetwork externalNetwork = ExternalNetwork.getNetworkById(externalNetworkId);

		CollectionVariant<VideoContent> variant = analyticsService.findAllRecommendedVideos(userId, externalNetwork, ifModifiedSince);


		// Throw a 304 if if there is no variant (no change)
		if (variant == null)
			return Response.notModified().build();


		Collection<VideoContent> videos = variant.getCollection();
		for(VideoContent videoContent : videos) {
			recommendationsDto.getVideos().add(DtoAssembler.assemble(videoContent));
		}

		return Response.ok()
				.header("Last-Modified", variant.getLastModified())
				.entity(jsonConverter.convertToPayload(recommendationsDto))
				.build();
	}

}
