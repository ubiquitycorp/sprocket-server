package com.ubiquity.sprocket.api.dto.containers;

import java.util.HashMap;
import java.util.Map;

public class ConfigurationDto {
	
	private ConfigurationRulesDto rules ;
	private Map<String, Object> services = new HashMap<String, Object>();
	
	public ConfigurationRulesDto getRules() {
		return rules;
	}
	public Map<String, Object> getServices() {
		return services;
	}
	public void setRules(ConfigurationRulesDto rules) {
		this.rules = rules;
	}
	

}
