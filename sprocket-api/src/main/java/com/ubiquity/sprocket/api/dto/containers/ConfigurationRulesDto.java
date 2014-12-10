package com.ubiquity.sprocket.api.dto.containers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ubiquity.sprocket.api.dto.model.ExternalNetworkConfigurationDto;

public class ConfigurationRulesDto {
	private Map<String, Object> generalRules = new HashMap<String, Object>();
	private List<ExternalNetworkConfigurationDto> providers = new LinkedList<ExternalNetworkConfigurationDto>();
	
	public Map<String, Object> getGeneralRules() {
		return generalRules;
	}

	public List<ExternalNetworkConfigurationDto> getProviders() {
		return providers;
	}


}
