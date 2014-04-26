package com.ubiquity.giftsender.api.dto.containers;

import java.util.LinkedList;
import java.util.List;

import com.ubiquity.giftsender.api.dto.model.EventDto;

public class EventsDto {
	
	private List<EventDto> events = new LinkedList<EventDto>();

	public List<EventDto> getEvents() {
		return events;
	}

}
