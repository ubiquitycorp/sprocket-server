package com.ubiquity.sprocket.api.dto.model;

import java.util.HashMap;
import java.util.Map;

import com.ubiquity.integration.domain.ExternalNetwork;

public class ExternalNetworkConfigurationDto {
	
	private ExternalNetwork providerName ;
	private Integer providerId ;
	private Boolean isActive ;
	private Map<String, Object> rules = new HashMap<String, Object>();
	
	public ExternalNetworkConfigurationDto(ExternalNetwork providerName ,Integer providerId){
		this.providerId= providerId;
		this.providerName = providerName;
	}
	public Map<String, Object> getRules() {
		return rules;
	}

	public ExternalNetwork getProviderName() {
		return providerName;
	}

	public Integer getProviderId() {
		return providerId;
	}

	public Boolean getIsActive() {
		return isActive;
	}
	
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	
	
}
