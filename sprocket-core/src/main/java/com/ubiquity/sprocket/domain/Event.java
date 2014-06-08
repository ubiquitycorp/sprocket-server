package com.ubiquity.sprocket.domain;

import java.util.HashMap;
import java.util.Map;

public class Event {
	
	private EventType type;
	
	private Map<String, Object> properties = new HashMap<String, Object>();

	/***
	 * Parameterized constructor creates an event with required properties
	 * 
	 * @param type
	 */
	public Event(EventType type) {
		this.type = type;
	}
	
	public Map<String, Object> getProperties() {
		return properties;
	}

	public EventType getType() {
		return type;
	}

	@Override
	public String toString() {
		return "Event [type=" + type + ", properties=" + properties + "]";
	}
	
	

}
