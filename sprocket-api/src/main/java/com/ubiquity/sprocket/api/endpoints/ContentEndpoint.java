package com.ubiquity.sprocket.api.endpoints;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.social.domain.ContentProvider;
import com.ubiquity.sprocket.api.dto.model.ImageDto;
import com.ubiquity.sprocket.api.dto.model.VideoDto;

@Path("/1.0/content/providers")
public class ContentEndpoint {

	private JsonConverter jsonConverter = JsonConverter.getInstance();

	@GET
	@Path("/{contentProviderId}/videos")
	@Produces(MediaType.APPLICATION_JSON)
	public Response ping() {
		List<VideoDto> videos = new LinkedList<VideoDto>();

		videos.add(new VideoDto.Builder()
		.contentProviderId(ContentProvider.YouTube.ordinal())
		.itemKey("UC_x5XG1OV2P6uZZ5FSM9Ttw")
		.thumb(new ImageDto("https://yt3.ggpht.com/-Fgp8KFpgQqE/AAAAAAAAAAI/AAAAAAAAAAA/Wyh1vV5Up0I/s88-c-k-no/photo.jpg"))
		.title("Google Developers")
		.description("Talks, screencasts, interviews, and more relevant to Google's developer products.")
		.build());
		videos.add(new VideoDto.Builder()
		.contentProviderId(ContentProvider.YouTube.ordinal())
		.itemKey("UC_x5XG1OV2P6uZZ5FSM9Ttw")
		.thumb(new ImageDto("https://yt3.ggpht.com/-Fgp8KFpgQqE/AAAAAAAAAAI/AAAAAAAAAAA/Wyh1vV5Up0I/s88-c-k-no/photo.jpg"))
		.title("Google Developers")
		.description("Talks, screencasts, interviews, and more relevant to Google's developer products.")
		.build());
		videos.add(new VideoDto.Builder()
		.contentProviderId(ContentProvider.YouTube.ordinal())
		.itemKey("UC_x5XG1OV2P6uZZ5FSM9Ttw")
		.thumb(new ImageDto("https://yt3.ggpht.com/-Fgp8KFpgQqE/AAAAAAAAAAI/AAAAAAAAAAA/Wyh1vV5Up0I/s88-c-k-no/photo.jpg"))
		.title("Google Developers")
		.description("Talks, screencasts, interviews, and more relevant to Google's developer products.")
		.build());
		videos.add(new VideoDto.Builder()
		.contentProviderId(ContentProvider.YouTube.ordinal())
		.itemKey("UC_x5XG1OV2P6uZZ5FSM9Ttw")
		.thumb(new ImageDto("https://yt3.ggpht.com/-Fgp8KFpgQqE/AAAAAAAAAAI/AAAAAAAAAAA/Wyh1vV5Up0I/s88-c-k-no/photo.jpg"))
		.title("Google Developers")
		.description("Talks, screencasts, interviews, and more relevant to Google's developer products.")
		.build());

		return Response.ok().entity(jsonConverter.convertToPayload(videos)).build();
	}

}


