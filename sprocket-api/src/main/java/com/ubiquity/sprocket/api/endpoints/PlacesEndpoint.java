package com.ubiquity.sprocket.api.endpoints;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
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
import com.niobium.repository.CollectionVariant;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.location.domain.Place;
import com.ubiquity.location.domain.UserLocation;
import com.ubiquity.sprocket.api.DtoAssembler;
import com.ubiquity.sprocket.api.dto.containers.PlacesDto;
import com.ubiquity.sprocket.api.dto.model.GeoboxDto;
import com.ubiquity.sprocket.api.dto.model.PlaceDto;
import com.ubiquity.sprocket.api.interceptors.Secure;
import com.ubiquity.sprocket.api.validation.PlaceLocationUpdateValidation;
import com.ubiquity.sprocket.messaging.MessageConverterFactory;
import com.ubiquity.sprocket.messaging.MessageQueueFactory;
import com.ubiquity.sprocket.messaging.definition.PlaceLocationUpdated;
import com.ubiquity.sprocket.messaging.definition.UserFavoritePlace;
import com.ubiquity.sprocket.service.ServiceFactory;

@Path("/1.0/places")
public class PlacesEndpoint {
	private Logger log = LoggerFactory.getLogger(getClass());
	private JsonConverter jsonConverter = JsonConverter.getInstance();

	@GET
	@Path("/users/{userId}/regions/{region}/neighborhoods")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response neighborhoods(@PathParam("userId") Long userId,
			@PathParam("region") String region,
			@HeaderParam("delta") Boolean delta,
			@HeaderParam("If-Modified-Since") Long ifModifiedSince)
			throws IOException {
		PlacesDto results = new PlacesDto();

		CollectionVariant<Place> variant = ServiceFactory.getLocationService()
				.getAllCitiesAndNeighborhoods(region, ifModifiedSince, delta);

		// Throw a 304 if if there is no variant (no change)
		if (variant == null)
			return Response.notModified().build();

		for (Place place : variant.getCollection()) {
			results.getPlaces().add(
					DtoAssembler.assembleCityOrNeighborhood(place));
		}

		return Response.ok().header("Last-Modified", variant.getLastModified())
				.entity(jsonConverter.convertToPayload(results)).build();
	}

	@GET
	@Path("/users/{userId}/providers/{externalNetworkId}/places/{placeId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response placesByInterestIdAndNeighborhood(
			@PathParam("userId") Long userId,
			@PathParam("externalNetworkId") Integer socialProviderId,
			@PathParam("placeId") Long placeId,
			@QueryParam("interestId") List<Long> interestId) throws IOException {

		PlacesDto results = new PlacesDto();
		ExternalNetwork externalNetwork = ExternalNetwork
				.getNetworkById(socialProviderId);
		List<Place> places = ServiceFactory.getLocationService()
				.findPlacesByInterestId(placeId, interestId, externalNetwork);

		for (Place place : places) {
			results.getPlaces().add(DtoAssembler.assemble(place));
		}

		return Response.ok()
		// .header("Last-Modified", places.lastModified)
				.entity(jsonConverter.convertToPayload(results)).build();
	}

	@GET
	@Path("/users/{userId}/providers/{externalNetworkId}/places/current")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response placesByInterestIdAndCurrent(
			@PathParam("userId") Long userId,
			@PathParam("externalNetworkId") Integer socialNetworkId,
			@QueryParam("interestId") List<Long> interestId) throws IOException {
		UserLocation userLocation = ServiceFactory.getLocationService()
				.getLocation(userId);
		if (userLocation == null) {
			Long lastModified = ServiceFactory.getLocationService()
					.checkUpdateLocationInProgress(userId);
			if (lastModified == null)
				throw new IllegalArgumentException(
						"User location is not available");
			else {
				userLocation = ServiceFactory.getLocationService()
						.getLocation(userId);
				if (userLocation == null)
					return Response.notModified().build();

			}
		}

		PlacesDto results = new PlacesDto();
		ExternalNetwork externalNetwork = ExternalNetwork
				.getNetworkById(socialNetworkId);
		List<Place> places = ServiceFactory.getLocationService()
				.findPlacesByInterestId(
						userLocation.getNearestPlace().getPlaceId(),
						interestId, externalNetwork);

		for (Place place : places) {
			results.getPlaces().add(DtoAssembler.assemble(place));
		}

		return Response.ok()
		// .header("Last-Modified", places.lastModified)
				.entity(jsonConverter.convertToPayload(results)).build();
	}

	@GET
	@Path("/users/{userId}/providers/{providerId}/favorites")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response favoritesByOwnerAndProvider(
			@PathParam("userId") Long userId,
			@PathParam("providerId") Integer socialProviderId,
			@QueryParam("interestId") Long interestId,
			@HeaderParam("delta") Boolean delta,
			@HeaderParam("If-Modified-Since") Long ifModifiedSince)
			throws IOException {

		PlacesDto results = new PlacesDto();
		ExternalNetwork externalNetwork = ExternalNetwork
				.getNetworkById(socialProviderId);
		CollectionVariant<Place> places = ServiceFactory.getFavoriteService()
				.getFavoritePlacesByOwnerIdandProvider(userId, externalNetwork,
						ifModifiedSince, delta);
		if (places == null)
			return Response.notModified().build();
		for (Place place : places.getCollection()) {
			results.getPlaces().add(DtoAssembler.assemble(place));
		}

		return Response.ok().header("Last-Modified", places.getLastModified())
				.entity(jsonConverter.convertToPayload(results)).build();
	}

	@GET
	@Path("/users/{userId}/providers/{providerId}/favorites/places/{placeId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response favoritesByOwnerAndProviderAndNeighborhood(
			@PathParam("userId") Long userId,
			@PathParam("providerId") Integer socialProviderId,
			@PathParam("placeId") Long placeId,
			@QueryParam("interestId") Long interestId,
			@HeaderParam("delta") Boolean delta,
			@HeaderParam("If-Modified-Since") Long ifModifiedSince)
			throws IOException {

		PlacesDto results = new PlacesDto();
		ExternalNetwork externalNetwork = ExternalNetwork
				.getNetworkById(socialProviderId);
		CollectionVariant<Place> places = ServiceFactory.getFavoriteService()
				.getFavoritePlacesByOwnerIdandProviderAndPlaceId(userId,
						externalNetwork, placeId, ifModifiedSince, delta);
		if (places == null)
			return Response.notModified().build();
		for (Place place : places.getCollection()) {
			results.getPlaces().add(DtoAssembler.assemble(place));
		}

		return Response.ok().header("Last-Modified", places.getLastModified())
				.entity(jsonConverter.convertToPayload(results)).build();
	}

	@GET
	@Path("/users/{userId}/providers/{providerId}/favorites/places/current")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response favoritesByOwnerAndProviderAndCurrent(
			@PathParam("userId") Long userId,
			@PathParam("providerId") Integer socialProviderId,
			@QueryParam("interestId") Long interestId,
			@HeaderParam("delta") Boolean delta,
			@HeaderParam("If-Modified-Since") Long ifModifiedSince)
			throws IOException {
		UserLocation userLocation = ServiceFactory.getLocationService()
				.getLocation(userId);
		if (userLocation == null)
			throw new IllegalArgumentException("User location is not available");

		PlacesDto results = new PlacesDto();
		ExternalNetwork externalNetwork = ExternalNetwork
				.getNetworkById(socialProviderId);
		CollectionVariant<Place> places = ServiceFactory.getFavoriteService()
				.getFavoritePlacesByOwnerIdandProviderAndPlaceId(userId,
						externalNetwork,
						userLocation.getNearestPlace().getPlaceId(),
						ifModifiedSince, delta);
		if (places == null)
			return Response.notModified().build();
		for (Place place : places.getCollection()) {
			results.getPlaces().add(DtoAssembler.assemble(place));
		}

		return Response.ok().header("Last-Modified", places.getLastModified())
				.entity(jsonConverter.convertToPayload(results)).build();
	}

	@POST
	@Path("/users/{userId}/favorites")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response postfavorites(@PathParam("userId") Long userId,
			InputStream payload) throws IOException {
		PlacesDto placesDto = jsonConverter.convertFromPayload(payload,
				PlacesDto.class);

		for (PlaceDto placeDto : placesDto.getPlaces()) {
			log.debug("tracking activity {}", placeDto);
			sendTrackAndSyncMessage(userId, placeDto);
		}
		return Response.ok().build();
	}

	@POST
	@Path("/{placeId}/users/{userId}/location")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response postUpdatePlace(@PathParam("userId") Long userId,
			@PathParam("placeId") Long placeId, InputStream payload)
			throws IOException {
		GeoboxDto geoDto = jsonConverter.convertFromPayload(payload,
				GeoboxDto.class, PlaceLocationUpdateValidation.class);

		Place place = ServiceFactory.getLocationService().getPlaceByID(placeId);
		if (place != null)
			if (place.getExternalNetwork() != null)
				sendTrackAndSyncMessage(placeId, geoDto);
			else
				throw new IllegalArgumentException(
						"Not allowed to update location for city or neighborhood");
		return Response.ok().build();
	}

	/**
	 * Drops a message for tracking this event
	 * 
	 * @param userId
	 * @param activityDto
	 * @throws IOException
	 */
	private void sendTrackAndSyncMessage(Long userId, PlaceDto placeDto)
			throws IOException {

		Place place = DtoAssembler.assemble(placeDto);

		UserFavoritePlace messageContent = new UserFavoritePlace(userId, place);
		String message = MessageConverterFactory.getMessageConverter()
				.serialize(
						new com.ubiquity.messaging.format.Message(
								messageContent));
		byte[] bytes = message.getBytes();
		MessageQueueFactory.getCacheInvalidationQueueProducer().write(bytes);
	}

	private void sendTrackAndSyncMessage(long placeId, GeoboxDto geoboxDto)
			throws IOException {

		PlaceLocationUpdated messageContent = new PlaceLocationUpdated.Builder()
				.placeId(placeId).geobox(DtoAssembler.assemble(geoboxDto))
				.build();
		String message = MessageConverterFactory.getMessageConverter()
				.serialize(
						new com.ubiquity.messaging.format.Message(
								messageContent));
		byte[] bytes = message.getBytes();
		MessageQueueFactory.getLocationQueueProducer().write(bytes);
	}
}
