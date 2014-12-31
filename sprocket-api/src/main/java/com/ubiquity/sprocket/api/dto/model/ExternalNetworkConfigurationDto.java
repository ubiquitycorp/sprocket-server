package com.ubiquity.sprocket.api.dto.model;

import java.util.HashMap;
import java.util.Map;

import com.ubiquity.integration.domain.ExternalNetwork;

public class ExternalNetworkConfigurationDto {

	private ExternalNetwork providerName;
	private Integer providerId;
	private Boolean isActive;
	private Map<String, Object> rules = null;

	public ExternalNetworkConfigurationDto(ExternalNetwork externalNetwork) {
		this.providerName = externalNetwork;
		this.providerId = ExternalNetwork.ordinalOrDefault(externalNetwork);
		rules = new HashMap<String, Object>();
	}
	
	public ExternalNetworkConfigurationDto(ExternalNetwork externalNetwork, Map<String, Object> rules) {
		this.providerName = externalNetwork;
		this.providerId = ExternalNetwork.ordinalOrDefault(externalNetwork);
		this.rules = rules;
	}

	public Map<String, Object> getRules() {
		return rules;
	}

	public ExternalNetwork getExternalNetwork() {
		return providerName;
	}

	public Integer getExternalNetworkId() {
		return providerId;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

}
