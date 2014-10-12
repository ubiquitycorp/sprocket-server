package com.ubiquity.sprocket.api.dto.containers;

import java.util.HashMap;
import java.util.Map;

public class ConfigurationDto {
	
	private Map<String, Object> rules = new HashMap<String, Object>();
	private Map<String, Object> services = new HashMap<String, Object>();
	
	public Map<String, Object> getRules() {
		return rules;
	}
	public Map<String, Object> getServices() {
		return services;
	}

}
