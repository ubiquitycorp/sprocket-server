package com.ubiquity.sprocket.api.endpoints;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
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
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.ExternalNetworkApplication;
import com.ubiquity.identity.domain.User;
import com.ubiquity.integration.domain.Activity;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.Message;
import com.ubiquity.integration.domain.VideoContent;
import com.ubiquity.sprocket.api.DtoAssembler;
import com.ubiquity.sprocket.api.dto.containers.DocumentsDto;
import com.ubiquity.sprocket.api.dto.model.DocumentDto;
import com.ubiquity.sprocket.api.interceptors.Secure;
import com.ubiquity.sprocket.api.validation.EngagementValidation;
import com.ubiquity.sprocket.domain.Document;
import com.ubiquity.sprocket.messaging.MessageConverterFactory;
import com.ubiquity.sprocket.messaging.MessageQueueFactory;
import com.ubiquity.sprocket.messaging.definition.UserEngagedDocument;
import com.ubiquity.sprocket.service.ServiceFactory;

@Path("/1.0/documents")
public class DocumentsEndpoint {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	private JsonConverter jsonConverter = JsonConverter.getInstance();
	
	@POST
	@Path("/users/{userId}/live/engaged")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response engaged(@PathParam("userId") Long userId, InputStream payload) throws IOException {

		// convert payload
		DocumentsDto documentsDto = jsonConverter.convertFromPayload(payload, DocumentsDto.class, EngagementValidation.class);
		log.debug("documents engaged {}", documentsDto);
		for(DocumentDto documentDto : documentsDto.getDocuments()) {
			sendTrackAndSyncMessage(userId, documentsDto.getSearchTerm(), documentDto);
		}
		
		return Response.ok().build();
	}
	
	@GET
	@Path("providers/{externalNetworkId}/indexed")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchIndexed(@PathParam("externalNetworkId") Integer externalNetworkId, @QueryParam("q") String q, @QueryParam("page") Integer page) throws IOException {
		DocumentsDto result = new DocumentsDto(q);

		ExternalNetwork externalNetwork = ExternalNetwork.getNetworkById(externalNetworkId);
		
		List<Document> documents = ServiceFactory.getSearchService().searchIndexedDocuments(q, null, externalNetwork);
		for(Document document : documents) {
			result.getDocuments().add(DtoAssembler.assemble(document));
		}
		
		return Response.ok().entity(jsonConverter.convertToPayload(result)).build();
	}
	
	@GET
	@Path("users/{userId}/providers/{externalNetworkId}/indexed")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response searchIndexed(@PathParam("userId") Long userId, @PathParam("externalNetworkId") Integer externalNetworkId, @QueryParam("q") String q, @QueryParam("page") Integer page) throws IOException {
		DocumentsDto result = new DocumentsDto(q);

		ExternalNetwork externalNetwork = ExternalNetwork.getNetworkById(externalNetworkId);
		
		List<Document> documents = ServiceFactory.getSearchService().searchIndexedDocuments(q, userId, externalNetwork);
		for(Document document : documents) {
			result.getDocuments().add(DtoAssembler.assemble(document));
		}
		
		return Response.ok().entity(jsonConverter.convertToPayload(result)).build();
	}
	
	/***
	 * This end point returns searches for document over both most popular data and user data
	 * @param userId
	 * @param q
	 * @param page
	 * @return
	 * @throws IOException
	 */
	@GET
	@Path("users/{userId}/indexed")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response searchIndexed(@PathParam("userId") Long userId, @QueryParam("q") String q, @QueryParam("page") Integer page) throws IOException {
		DocumentsDto result = new DocumentsDto(q);

		List<Document> documents = ServiceFactory.getSearchService().searchIndexedDocumentsWithinAllProviders(q, userId);
		for(Document document : documents) {
			result.getDocuments().add(DtoAssembler.assemble(document));
		}
		
		return Response.ok().entity(jsonConverter.convertToPayload(result)).build();
	}
	
	
	@GET
	@Path("users/{userId}/providers/{externalNetworkId}/live")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response searchLive(@PathParam("userId") Long userId, @PathParam("externalNetworkId") Integer externalNetworkId, @QueryParam("q") String q, @QueryParam("page") Integer page, @QueryParam("longitude") BigDecimal longitude, @QueryParam("latitude") BigDecimal latitude, @QueryParam("locator") String locator) throws IOException {
		DocumentsDto result = new DocumentsDto(q);

		ExternalNetwork externalNetwork = ExternalNetwork.getNetworkById(externalNetworkId);
		User user = ServiceFactory.getUserService().getUserById(userId);
		
		ExternalIdentity identity = ServiceFactory.getExternalIdentityService().findExternalIdentity(userId, externalNetwork);
		
		if(identity == null && externalNetwork != ExternalNetwork.Yelp)
			throw new IllegalArgumentException("User does not have an identity for this provider");
		
		ExternalNetworkApplication externalNetworkApplication = ServiceFactory.getApplicationService().getExAppByExternalIdentity(user.getCreatedBy(), identity);
		
		if(externalNetwork != ExternalNetwork.Yelp)
			ServiceFactory.getSocialService().checkValidityOfExternalIdentity(identity,  externalNetworkApplication); 
		
		List<Document> documents = ServiceFactory.getSearchService().searchLiveDocuments(q, user, externalNetwork, page ,longitude,latitude,locator,externalNetworkApplication);
		
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
	private void sendTrackAndSyncMessage(Long userId, String searchTerm, DocumentDto documentDto) throws IOException {
		
		// convert to domain entity and prepare to send to MQ
		Document document = DtoAssembler.assemble(documentDto, EngagementValidation.class);
		String dataType = document.getDataType();
		
		// create message content with strongly typed references to the actual domain entity (for easier de-serialization on the consumer end)
		UserEngagedDocument messageContent;
		if(dataType.equalsIgnoreCase(Activity.class.getSimpleName()))
			messageContent = new UserEngagedDocument(userId, (Activity)document.getData(), searchTerm);
		else if(dataType.equalsIgnoreCase(VideoContent.class.getSimpleName()))
			messageContent = new UserEngagedDocument(userId, (VideoContent)document.getData(), searchTerm);
		else if(dataType.equalsIgnoreCase(Message.class.getSimpleName()))
			messageContent = new UserEngagedDocument(userId, (Message)document.getData(), searchTerm);
		else
			throw new IllegalArgumentException("Unknown data type for document: " + dataType);
		
		// convert to raw bytes and send it off
		String message = MessageConverterFactory.getMessageConverter().serialize(new com.ubiquity.messaging.format.Message(messageContent));
		byte[] bytes = message.getBytes();
		
		// will ensure the domain entity gets saved to the store if it does not exist and indexed for faster search
		MessageQueueFactory.getCacheInvalidationQueueProducer().write(bytes);


	}

}


