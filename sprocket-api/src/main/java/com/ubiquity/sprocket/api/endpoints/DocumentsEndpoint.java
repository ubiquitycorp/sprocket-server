package com.ubiquity.sprocket.api.endpoints;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.messaging.format.Message;
import com.ubiquity.sprocket.api.DtoAssembler;
import com.ubiquity.sprocket.api.dto.containers.DocumentsDto;
import com.ubiquity.sprocket.domain.Document;
import com.ubiquity.sprocket.domain.EventType;
import com.ubiquity.sprocket.messaging.MessageConverterFactory;
import com.ubiquity.sprocket.messaging.MessageQueueFactory;
import com.ubiquity.sprocket.messaging.definition.EventTracked;
import com.ubiquity.sprocket.service.ServiceFactory;

@Path("/1.0/documents")
public class DocumentsEndpoint {

	private JsonConverter jsonConverter = JsonConverter.getInstance();

	@GET
	@Path("users/{userId}/indexed")
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(@PathParam("userId") Long userId, @QueryParam("q") String q) throws IOException {

		DocumentsDto result = new DocumentsDto();
		List<Document> documents = ServiceFactory.getSearchService().searchDocuments(q, userId);
		for(Document document : documents)
			result.getDocuments().add(DtoAssembler.assemble(document));
		
		// now track this
		sendEventTrackedMessage(q);
		
		return Response.ok().entity(jsonConverter.convertToPayload(result)).build();
	}

	private void sendEventTrackedMessage(String q) throws IOException {
		EventTracked content = new EventTracked(EventType.Search.ordinal());
		content.getProperties().put("q", q);

		// serialize and send itit
		String message = MessageConverterFactory.getMessageConverter().serialize(new Message(content));
		MessageQueueFactory.getTrackQueueProducer().write(message.getBytes());
		
	}

}


