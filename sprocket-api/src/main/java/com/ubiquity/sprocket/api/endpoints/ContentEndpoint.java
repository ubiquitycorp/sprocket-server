package com.ubiquity.sprocket.api.endpoints;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.identity.domain.Identity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.social.api.ContentAPI;
import com.ubiquity.social.api.ContentAPIFactory;
import com.ubiquity.social.domain.ContentProvider;
import com.ubiquity.social.domain.ExternalIdentity;
import com.ubiquity.social.domain.VideoContent;
import com.ubiquity.sprocket.api.dto.model.ImageDto;
import com.ubiquity.sprocket.api.dto.model.VideoDto;
import com.ubiquity.sprocket.service.ServiceFactory;

@Path("/1.0/content")
public class ContentEndpoint {

	private JsonConverter jsonConverter = JsonConverter.getInstance();

	@GET
	@Path("/users/{userId}/providers/{contentProviderId}/videos")
	@Produces(MediaType.APPLICATION_JSON)
	public Response videos(@PathParam("userId") Long userId) {
		
		List<VideoDto> results = new LinkedList<VideoDto>();
		
		User user = ServiceFactory.getUserService().getUserById(userId);
		ContentAPI contentApi = ContentAPIFactory.createProvider(ContentProvider.YouTube);
		//List<Video> videos = contentApi.findVideosByExternalIdentity(externalIdentity)
		// Go through google
		
		List<VideoContent> videos = null;
		for(Identity identity : user.getIdentities()) {
			if(identity instanceof ExternalIdentity) {
				ExternalIdentity external = (ExternalIdentity)identity;
				if(external.getContentProvider() != null) { // for now this is good enough to find youtube, because 
					videos = contentApi.findVideosByExternalIdentity(external);
					break;
				}
			}
		}
		
		if(videos != null) {
			for(VideoContent videoContent : videos) {
				results.add(new VideoDto.Builder()
				.contentProviderId(ContentProvider.YouTube.ordinal())
				.itemKey("UC_x5XG1OV2P6uZZ5FSM9Ttw")
				.thumb(new ImageDto("https://yt3.ggpht.com/-Fgp8KFpgQqE/AAAAAAAAAAI/AAAAAAAAAAA/Wyh1vV5Up0I/s88-c-k-no/photo.jpg"))
				.title("Google Developers")
				.description("Talks, screencasts, interviews, and more relevant to Google's developer products.")
				.build());
			}
		}
		
					
		

//		videos.add(new VideoDto.Builder()
//		.contentProviderId(ContentProvider.YouTube.ordinal())
//		.itemKey("UC_x5XG1OV2P6uZZ5FSM9Ttw")
//		.thumb(new ImageDto("https://yt3.ggpht.com/-Fgp8KFpgQqE/AAAAAAAAAAI/AAAAAAAAAAA/Wyh1vV5Up0I/s88-c-k-no/photo.jpg"))
//		.title("Google Developers")
//		.description("Talks, screencasts, interviews, and more relevant to Google's developer products.")
//		.build());
//		videos.add(new VideoDto.Builder()
//		.contentProviderId(ContentProvider.YouTube.ordinal())
//		.itemKey("UC_x5XG1OV2P6uZZ5FSM9Ttw")
//		.thumb(new ImageDto("https://yt3.ggpht.com/-Fgp8KFpgQqE/AAAAAAAAAAI/AAAAAAAAAAA/Wyh1vV5Up0I/s88-c-k-no/photo.jpg"))
//		.title("Google Developers")
//		.description("Talks, screencasts, interviews, and more relevant to Google's developer products.")
//		.build());
//		videos.add(new VideoDto.Builder()
//		.contentProviderId(ContentProvider.YouTube.ordinal())
//		.itemKey("UC_x5XG1OV2P6uZZ5FSM9Ttw")
//		.thumb(new ImageDto("https://yt3.ggpht.com/-Fgp8KFpgQqE/AAAAAAAAAAI/AAAAAAAAAAA/Wyh1vV5Up0I/s88-c-k-no/photo.jpg"))
//		.title("Google Developers")
//		.description("Talks, screencasts, interviews, and more relevant to Google's developer products.")
//		.build());
//		videos.add(new VideoDto.Builder()
//		.contentProviderId(ContentProvider.YouTube.ordinal())
//		.itemKey("UC_x5XG1OV2P6uZZ5FSM9Ttw")
//		.thumb(new ImageDto("https://yt3.ggpht.com/-Fgp8KFpgQqE/AAAAAAAAAAI/AAAAAAAAAAA/Wyh1vV5Up0I/s88-c-k-no/photo.jpg"))
//		.title("Google Developers")
//		.description("Talks, screencasts, interviews, and more relevant to Google's developer products.")
//		.build());

		return Response.ok().entity(jsonConverter.convertToPayload(videos)).build();
	}

}


