package com.ubiquity.sprocket.messaging.definition;

import java.util.HashMap;
import java.util.Map;

public class EventTracked {
	
	private Integer eventTypeId;
	private Map<String, Object> properties = new HashMap<String, Object>();
	
	public Integer getEventTypeId() {
		return eventTypeId;
	}
	public Map<String, Object> getProperties() {
		return properties;
	}
	
	public EventTracked(Integer eventTypeId) {
		this.eventTypeId = eventTypeId;
	}

}
