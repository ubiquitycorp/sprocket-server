package com.ubiquity.sprocket.api.endpoints;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.social.domain.ContentProvider;
import com.ubiquity.sprocket.api.dto.containers.DocumentsDto;
import com.ubiquity.sprocket.api.dto.model.DocumentDto;
import com.ubiquity.sprocket.api.dto.model.ImageDto;
import com.ubiquity.sprocket.api.dto.model.VideoDto;

@Path("/1.0/documents")
public class DocumentsEndpoint {

	private JsonConverter jsonConverter = JsonConverter.getInstance();

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(@QueryParam("q") String q) {

		DocumentsDto documents = new DocumentsDto();
		DocumentDto documentDto = new DocumentDto.Builder()
		.rank(1)
		.dataType("Video")
		.data(new VideoDto.Builder()
			.contentProviderId(ContentProvider.YouTube.ordinal())
			.itemKey("UC_x5XG1OV2P6uZZ5FSM9Ttw")
			.thumb(new ImageDto("https://yt3.ggpht.com/-Fgp8KFpgQqE/AAAAAAAAAAI/AAAAAAAAAAA/Wyh1vV5Up0I/s88-c-k-no/photo.jpg"))
			.title("Google Developers")
			.description("Talks, screencasts, interviews, and more relevant to Google's developer products.")
			.build())
		.build();
		documents.getDocuments().add(documentDto);
		
		documentDto = new DocumentDto.Builder()
		.rank(2)
		.dataType("Video")
		.data(new VideoDto.Builder()
			.contentProviderId(ContentProvider.YouTube.ordinal())
			.itemKey("UC_x5XG1OV2P6uZZ5FSM9Ttw")
			.thumb(new ImageDto("https://yt3.ggpht.com/-Fgp8KFpgQqE/AAAAAAAAAAI/AAAAAAAAAAA/Wyh1vV5Up0I/s88-c-k-no/photo.jpg"))
			.title("Google Developers")
			.description("Talks, screencasts, interviews, and more relevant to Google's developer products.")
			.build())
		.build();
		documents.getDocuments().add(documentDto);
		
		documentDto = new DocumentDto.Builder()
		.rank(2)
		.dataType("Video")
		.data(new VideoDto.Builder()
			.contentProviderId(ContentProvider.YouTube.ordinal())
			.itemKey("UC_x5XG1OV2P6uZZ5FSM9Ttw")
			.thumb(new ImageDto("https://yt3.ggpht.com/-Fgp8KFpgQqE/AAAAAAAAAAI/AAAAAAAAAAA/Wyh1vV5Up0I/s88-c-k-no/photo.jpg"))
			.title("Google Developers")
			.description("Talks, screencasts, interviews, and more relevant to Google's developer products.")
			.build())
		.build();
		documents.getDocuments().add(documentDto);
		

		

		return Response.ok().entity(jsonConverter.convertToPayload(documents)).build();
	}

}


