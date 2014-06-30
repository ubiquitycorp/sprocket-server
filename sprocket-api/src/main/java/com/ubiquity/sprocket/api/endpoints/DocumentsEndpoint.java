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
import com.ubiquity.api.utils.Page;
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

	/***
	 * Searches over indexed content for all social networks and content providers
	 * @param userId
	 * @param q
	 * @param page
	 * @return
	 * @throws IOException
	 */
	@GET
	@Path("users/{userId}/indexed")
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(@PathParam("userId") Long userId, @QueryParam("q") String q, @QueryParam("page") Integer page) throws IOException {

		DocumentsDto result = new DocumentsDto();
		List<Document> documents = ServiceFactory.getSearchService().searchDocuments(q, userId);
		
		Page<Document> pager = new Page<Document>(documents, page, 3);
		result.getPagination().setHasNextPage(pager.getHasNextPage());
				
		for(Document document : pager.getSubList())
			result.getDocuments().add(DtoAssembler.assemble(document));

		// now track this
		sendEventTrackedMessage(q);

		return Response.ok().entity(jsonConverter.convertToPayload(result)).build();
	}
	
	@GET
	@Path("users/{userId}/socialnetworks/{socialNetworkId}/indexed")
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(@PathParam("userId") Long userId, @PathParam("socialNetworkId") Integer socialNetworkId, @QueryParam("q") String q, @QueryParam("page") Integer page) throws IOException {
		DocumentsDto result = new DocumentsDto();

		List<Document> documents = ServiceFactory.getSearchService().searchDocuments(q, userId);

		
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


