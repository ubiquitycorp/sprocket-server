package com.ubiquity.giftsender.api.endpoints;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.common.serialize.JsonConverter;
import com.niobium.repository.CollectionVariant;
import com.ubiquity.commerce.domain.Item;
import com.ubiquity.giftsender.api.dto.DtoAssembler;
import com.ubiquity.giftsender.api.dto.containers.ItemsDto;
import com.ubiquity.giftsender.api.dto.model.ItemDto;
import com.ubiquity.giftsender.service.ServiceFactory;

/***
 * 
 * @author peter.tadros
 * 
 */
@Path("/1.0/mobile/items")
public class ItemEndPoint {

	private Logger log = LoggerFactory.getLogger(getClass());
	private JsonConverter jsonConverter = JsonConverter.getInstance();
	
	
	@GET
	@Path("/{itemId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listItem(@HeaderParam("ETag") String etag, @PathParam("itemId") Long itemId) {
		Item item = ServiceFactory.getItemService().getByItemId(itemId);

		if(item == null)
			throw new IllegalArgumentException("Item does not exist");
		
		ItemDto itemDto = DtoAssembler.assembleItemDto(item);
		return Response.ok().entity(jsonConverter.convertToPayload(itemDto)).build();
	}
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response list(@HeaderParam("If-Modified-Since") Long ifModifiedSince) {
		log.debug("Listing items modified since: {}", ifModifiedSince);
		CollectionVariant<Item> variant = ServiceFactory.getItemService().findAllItems(ifModifiedSince);

		// Throw a 304 if if there is no variant (no change)
		if (variant == null)
			return Response.notModified().build();

		// Convert entire list to DTO
		ItemsDto result = new ItemsDto();
		for (Item item : variant.getCollection()) {
			ItemDto itemDto = DtoAssembler.assembleItemDto(item);
			result.getItems().add(itemDto);
		}

		return Response.ok().header("Last-Modified", variant.getLastModified())
				.entity(jsonConverter.convertToPayload(result)).build();
	}
	
	
}
