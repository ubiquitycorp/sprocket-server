package com.ubiquity.sprocket.api.dto.containers;

import java.util.LinkedList;
import java.util.List;

import com.ubiquity.sprocket.api.dto.model.social.EventDto;

public class EventsDto {
	
	private List<EventDto> events = new LinkedList<EventDto>();

	public List<EventDto> getEvents() {
		return events;
	}

}
