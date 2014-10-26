package com.ubiquity.sprocket.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;

public class ClientConfigurationService {

	
	private Map<String, Object> rules = new HashMap<String, Object>();
	private Map<String, Object> services = new HashMap<String, Object>();
	
	public Map<String, Object> getRules() {
		return rules;
	}
	public Map<String, Object> getServices() {
		return services;
	}
	
	public ClientConfigurationService(Configuration configuration) {
		services.put("message.service.host", configuration.getProperty("xmpp.host"));
		services.put("message.service.port", configuration.getProperty("xmpp.port"));
		services.put("message.service.protocol", "xmpp");
		
		rules.put("http.timeout.seconds", configuration.getProperty("client.http.timeout.seconds"));

	}

}
