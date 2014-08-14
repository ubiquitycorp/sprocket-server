package com.ubiquity.sprocket.service;

import org.apache.commons.configuration.Configuration;

public class ErrorsConfigurationService {
	Configuration configuration;
	
	public ErrorsConfigurationService(Configuration configuration) {
		this.configuration = configuration;
	}
	
	public String getErrorMessage(String errorName){
		return configuration.getString(errorName);
	}
}
