package com.ubiquity.sprocket.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;

import com.ubiquity.sprocket.domain.ConfigurationType;
import com.ubiquity.sprocket.repository.ConfigurationRepository;
import com.ubiquity.sprocket.repository.ConfigurationRepositoryJpaImpl;

public class ClientConfigurationService {

	
	private List<com.ubiquity.sprocket.domain.Configuration> rules = new LinkedList<com.ubiquity.sprocket.domain.Configuration>();
	private Map<String, Object> services = new HashMap<String, Object>();
	
	public List<com.ubiquity.sprocket.domain.Configuration> getRules() {
		return rules;
	}
	public Map<String, Object> getServices() {
		return services;
	}
	
	public ClientConfigurationService(Configuration configuration) {
		
		ConfigurationRepository configRepository = new ConfigurationRepositoryJpaImpl();
		List<com.ubiquity.sprocket.domain.Configuration> configurations = configRepository.findConfigurationByType(ConfigurationType.SERVICE);
		
		for (com.ubiquity.sprocket.domain.Configuration configuration2 : configurations) {
			services.put(configuration2.getName(), configuration2.getValue());
		}

		configurations = configRepository.findConfigurationByType(ConfigurationType.RULE);
		
		rules.addAll(configurations);
	}

}
