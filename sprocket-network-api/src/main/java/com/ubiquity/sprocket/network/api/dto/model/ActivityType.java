package com.ubiquity.sprocket.network.api.dto.model;


public enum ActivityType {
	
	VIDEO, PHOTO, LINK, STATUS, AUDIO , EMBEDEDHTML;
	
	public static ActivityType getActivityTypeFromId(Integer id) {
		if(id < ActivityType.values().length && id >= 0) {
			return ActivityType.values()[id];
		}
		throw new IllegalArgumentException("No such Activity Type for id: " + id);
	}
}
