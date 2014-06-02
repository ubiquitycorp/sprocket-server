package com.ubiquity.sprocket.api.dto.containers;

import java.util.HashMap;
import java.util.Map;

/***
 * Contains information and rules client needs to run, but may change before another release
 * 
 * @author chris
 *
 */
public class ClientConfigurationDto {

	private Map<String, Object> rules = new HashMap<String, Object>();
	private Map<String, Object> services = new HashMap<String, Object>();

	/**
	 * Rules for application behavior
	 * 
	 * @return
	 */
	public Map<String, Object> getRules() {
		return rules;
	}

	/**
	 * Map of services, such as a CDN or a chat server
	 * @return
	 */
	public Map<String, Object> getServices() {
		return services;
	}

	public void setRules(Map<String, Object> rules) {
		this.rules = rules;
	}

	public void setServices(Map<String, Object> services) {
		this.services = services;
	}

	

	
}
