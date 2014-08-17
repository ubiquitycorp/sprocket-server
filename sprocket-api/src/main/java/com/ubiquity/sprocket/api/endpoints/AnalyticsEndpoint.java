package com.ubiquity.sprocket.api.endpoints;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.content.domain.VideoContent;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.sprocket.api.DtoAssembler;
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

		RecommendationsDto recommendationsDto = new RecommendationsDto();
		AnalyticsService analyticsService = ServiceFactory.getAnalyticsService();
		
		List<Activity> activities = analyticsService.getRecommendedActivities(userId);
		for(Activity activity : activities)
			recommendationsDto.getActivities().add(DtoAssembler.assemble(activity));
		
		List<VideoContent> videos = analyticsService.getRecommendedVideos(userId);
		for(VideoContent videoContent : videos)
			recommendationsDto.getVideos().add(DtoAssembler.assemble(videoContent));
		

		return Response.ok().entity(jsonConverter.convertToPayload(recommendationsDto)).build();
	}
}
