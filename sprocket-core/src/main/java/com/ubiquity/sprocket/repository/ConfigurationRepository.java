package com.ubiquity.sprocket.repository;

import java.util.List;

import com.niobium.repository.Repository;
import com.ubiquity.sprocket.domain.Configuration;
import com.ubiquity.sprocket.domain.ConfigurationType;


public interface ConfigurationRepository extends Repository <Long, Configuration>  {
	
	/**
	 * Returns configurations
	 * 
	 * @param group
	 * @return List of configurations
	 */
	List<Configuration> findConfigurationByType(ConfigurationType type);

}
