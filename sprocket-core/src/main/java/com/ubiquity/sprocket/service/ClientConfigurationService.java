package com.ubiquity.sprocket.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;

import com.ubiquity.integration.domain.ExternalNetwork;
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
		List<com.ubiquity.sprocket.domain.Configuration> configurations = configRepository
				.findConfigurationByType(ConfigurationType.SERVICE);

		for (com.ubiquity.sprocket.domain.Configuration config : configurations) {
			services.put(config.getName(), config.getValue());
		}

		configurations = configRepository
				.findConfigurationByType(ConfigurationType.RULE);

		rules.addAll(configurations);
	}

	public String getValueByKeyAndExternalNetwork(String key,
			ExternalNetwork network) {

		for (com.ubiquity.sprocket.domain.Configuration config : rules) {
			if (config.getExternalNetwork() != null
					&& network != null
					&& config.getExternalNetwork().equals(network)
					&& config.getName().equals(key)) {
				return config.getValue();
			} else if (config.getName().equals(key))
			{
				return config.getValue();
			}
		}
		return null;
	}

	public Boolean getValue(String key, ExternalNetwork network) {
		Boolean isEnabled = Boolean
				.parseBoolean(getValueByKeyAndExternalNetwork(key, network));
		return isEnabled;
	}

}
