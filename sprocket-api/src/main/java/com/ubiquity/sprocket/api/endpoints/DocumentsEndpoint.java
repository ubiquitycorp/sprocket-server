package com.ubiquity.sprocket.api.endpoints;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.social.domain.ContentProvider;
import com.ubiquity.social.domain.SocialProvider;
import com.ubiquity.sprocket.api.dto.containers.DocumentsDto;
import com.ubiquity.sprocket.api.dto.model.ActivityDto;
import com.ubiquity.sprocket.api.dto.model.ContactDto;
import com.ubiquity.sprocket.api.dto.model.DocumentDto;
import com.ubiquity.sprocket.api.dto.model.ImageDto;
import com.ubiquity.sprocket.api.dto.model.MessageDto;
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
		.rank(3)
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
		.rank(4)
		.dataType("Message")
		.data(new MessageDto.Builder()
		.subject("Message subject 1")
		.date(System.currentTimeMillis())
		.socialProviderId(SocialProvider.Facebook.getValue())
		.body("Body of message 1, lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsumlorem ipsum lorem ipsumlorem ipsum lorem ipsumlorem ipsum lorem ipsumlorem ipsum lorem ipsumlorem ipsum lorem ipsum")
		.sender(new ContactDto.Builder().contactId(1l).displayName("Contact 1").firstName("Contact").lastName("One").imageUrl("https://graph.facebook.com/754592629/picture").build())
		.build())
		.build();
		documents.getDocuments().add(documentDto);

		documentDto = new DocumentDto.Builder()
		.rank(5)
		.dataType("Activity")
		
		.data(new ActivityDto.Builder()
		.body("Activity 3 body lorem ipsum lorem ipsume lorem ipsumelorem ipsum lorem ipsume lorem ipsumelorem ipsum lorem ipsume lorem ipsumelorem ipsum lorem ipsume lorem ipsume")
		.date(System.currentTimeMillis())
		.socialProviderId(SocialProvider.LinkedIn.getValue())
		.title("Activity title 3")
		.imageUrl(null)
		.postedBy(new ContactDto.Builder().contactId(2l).displayName("Contact 1").firstName("Contact").lastName("One").imageUrl("https://graph.facebook.com/754592628/picture").build())
		.build())
		.build();
		documents.getDocuments().add(documentDto);




		return Response.ok().entity(jsonConverter.convertToPayload(documents)).build();
	}

}


