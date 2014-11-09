package com.ubiquity.sprocket.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;

import com.ubiquity.sprocket.domain.ConfigurationType;
import com.ubiquity.sprocket.repository.ConfigurationRepository;
import com.ubiquity.sprocket.repository.ConfigurationRepositoryJpaImpl;

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
		
		ConfigurationRepository configRepository = new ConfigurationRepositoryJpaImpl();
		List<com.ubiquity.sprocket.domain.Configuration> configurations = configRepository.findConfigurationByType(ConfigurationType.SERVICE);
		
		for (com.ubiquity.sprocket.domain.Configuration configuration2 : configurations) {
			services.put(configuration2.getName(), configuration2.getValue());
		}

		configurations = configRepository.findConfigurationByType(ConfigurationType.RULE);
		
		for (com.ubiquity.sprocket.domain.Configuration configuration2 : configurations) {
			rules.put(configuration2.getName(), configuration2.getValue());
		}
	}

}
