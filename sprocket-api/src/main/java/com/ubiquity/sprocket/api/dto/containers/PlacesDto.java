package com.ubiquity.sprocket.api.dto.containers;

import java.util.LinkedList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.ubiquity.sprocket.api.dto.model.PlaceDto;
import com.ubiquity.sprocket.api.validation.EngagementValidation;

public class PlacesDto {
	@Size(min = 1, max = 1000, groups = {EngagementValidation.class })
	@NotNull(groups = {EngagementValidation.class })
	private List<PlaceDto> places = new LinkedList<PlaceDto>();

	public List<PlaceDto> getPlaces() {
		return places;
	}
}
