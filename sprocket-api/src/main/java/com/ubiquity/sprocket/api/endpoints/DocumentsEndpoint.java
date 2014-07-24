package com.ubiquity.sprocket.api.endpoints;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.User;
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

	private Logger log = LoggerFactory.getLogger(getClass());
	
	private JsonConverter jsonConverter = JsonConverter.getInstance();
	
	@POST
	@Path("/users/{userId}/live/engaged")
	@Produces(MediaType.APPLICATION_JSON)
	public Response engaged(@PathParam("userId") Long userId, @PathParam("externalNetworkId") Integer externalNetworkId, InputStream payload) {

		// convert payload
		DocumentsDto documentsDto = jsonConverter.convertFromPayload(payload, DocumentsDto.class);
		log.debug("documents engaged {}", documentsDto);
		
		return Response.ok().build();
	}
	@GET
	@Path("users/{userId}/providers/{externalNetworkId}/indexed")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchIndexed(@PathParam("userId") Long userId, @PathParam("externalNetworkId") Integer externalNetworkId, @QueryParam("q") String q, @QueryParam("page") Integer page) throws IOException {
		DocumentsDto result = new DocumentsDto();

		ExternalNetwork externalNetwork = ExternalNetwork.getNetworkById(externalNetworkId);
		
		List<Document> documents = ServiceFactory.getSearchService().searchIndexedDocuments(q, userId, externalNetwork);
		for(Document document : documents) {
			result.getDocuments().add(DtoAssembler.assemble(document));
		}
		
		return Response.ok().entity(jsonConverter.convertToPayload(result)).build();
	}
	
	@GET
	@Path("users/{userId}/providers/{externalNetworkId}/live")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchLive(@PathParam("userId") Long userId, @PathParam("externalNetworkId") Integer externalNetworkId, @QueryParam("q") String q, @QueryParam("page") Integer page) throws IOException {
		DocumentsDto result = new DocumentsDto();

		ExternalNetwork socialNetwork = ExternalNetwork.getNetworkById(externalNetworkId);
		User user = ServiceFactory.getUserService().getUserById(userId);
		
		List<Document> documents = ServiceFactory.getSearchService().searchLiveDocuments(q, user, socialNetwork, ClientPlatform.Android, page);
		
		for(Document document : documents) {
			result.getDocuments().add(DtoAssembler.assemble(document));
		}
		
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


