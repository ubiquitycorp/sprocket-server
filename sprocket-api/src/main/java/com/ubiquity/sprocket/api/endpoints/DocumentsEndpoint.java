package com.ubiquity.sprocket.api.endpoints;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.content.domain.VideoContent;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.identity.domain.User;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.domain.Message;
import com.ubiquity.sprocket.api.DtoAssembler;
import com.ubiquity.sprocket.api.dto.containers.DocumentsDto;
import com.ubiquity.sprocket.api.dto.model.ActivityDto;
import com.ubiquity.sprocket.api.dto.model.DocumentDto;
import com.ubiquity.sprocket.domain.Document;
import com.ubiquity.sprocket.messaging.MessageConverterFactory;
import com.ubiquity.sprocket.messaging.MessageQueueFactory;
import com.ubiquity.sprocket.messaging.definition.UserEngagedDocument;
//import com.ubiquity.sprocket.messaging.definition.EventTracked;
import com.ubiquity.sprocket.service.ServiceFactory;

@Path("/1.0/documents")
public class DocumentsEndpoint {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	private JsonConverter jsonConverter = JsonConverter.getInstance();
	
	@POST
	@Path("/users/{userId}/live/engaged")
	@Produces(MediaType.APPLICATION_JSON)
	public Response engaged(@PathParam("userId") Long userId, InputStream payload) throws IOException {

		
		 
//		 JsonParser parser = new JsonParser();
//		 String str = IOUtils.toString(payload);
//		 JsonObject object = parser.parse(str).getAsJsonObject();
//		 JsonArray array = object.getAsJsonArray("documents");
//		 Iterator<JsonElement> it = array.iterator();
//		 while(it.hasNext()) {
//			 JsonElement element = it.next();
//			 JsonObject jsonObject = element.getAsJsonObject();
//			 JsonElement dataElement = jsonObject.get("data");
//			 
//			ActivityDto activityDto = new Gson().fromJson(dataElement, ActivityDto.class);
//			log.debug("activityDto {}", activityDto);
//		 }
		    
		// convert payload
		DocumentsDto documentsDto = jsonConverter.convertFromPayload(payload, DocumentsDto.class);
		log.debug("documents engaged {}", documentsDto);
		for(DocumentDto documentDto : documentsDto.getDocuments()) {
		
			sendTrackAndSyncMessage(userId, documentDto);
		}
		
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
		
		List<Document> documents = ServiceFactory.getSearchService().searchLiveDocuments(q, user, socialNetwork, page);
		
		for(Document document : documents) {
			result.getDocuments().add(DtoAssembler.assemble(document));
		}
		
		return Response.ok().entity(jsonConverter.convertToPayload(result)).build();
		
	}
	
	/**
	 * Drops a message for tracking this event
	 * 
	 * @param userId
	 * @param activityDto
	 * @throws IOException
	 */
	private void sendTrackAndSyncMessage(Long userId, DocumentDto documentDto) throws IOException {
		
		
		Document document = DtoAssembler.assemble(documentDto);
		String dataType = document.getDataType();
		
		UserEngagedDocument messageContent;
		if(dataType.equalsIgnoreCase(Activity.class.getSimpleName()))
			messageContent = new UserEngagedDocument(userId, (Activity)document.getData(), dataType);
		else if(dataType.equalsIgnoreCase(VideoContent.class.getSimpleName()))
			messageContent = new UserEngagedDocument(userId, (VideoContent)document.getData(), dataType);
		else if(dataType.equalsIgnoreCase(Message.class.getSimpleName()))
			messageContent = new UserEngagedDocument(userId, (Message)document.getData(), dataType);
		else
			throw new IllegalArgumentException("Unknown data type for document: " + dataType);
		
		String message = MessageConverterFactory.getMessageConverter().serialize(new com.ubiquity.messaging.format.Message(messageContent));
		byte[] bytes = message.getBytes();
		MessageQueueFactory.getTrackQueueProducer().write(bytes);
		MessageQueueFactory.getCacheInvalidationQueueProducer().write(bytes);


	}

}


