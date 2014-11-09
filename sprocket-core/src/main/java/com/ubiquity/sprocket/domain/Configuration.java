package com.ubiquity.sprocket.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "configuration")
public class Configuration implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name = "configuration_id")
	private Long configurationId;
	
	@Column(name = "name", nullable = false, length = 200)
	private String name;
	
	@Column(name = "value", nullable = false, length = 200)
	private String value;
	
	@Column(name = "is_active", nullable = false)
	private Boolean isActive;
	
	@Column(name = "last_updated", nullable = false)
	private Long lastUpdated;
	
	@Column(name = "configuration_type", nullable = false)
	private ConfigurationType configurationType;
	
	/**
	 * Default constructor required by JPA
	 */
	protected Configuration() {
	}

	public Long getConfigurationId() {
		return configurationId;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public Long getLastUpdated() {
		return lastUpdated;
	}
	
	public ConfigurationType getConfigurationType() {
		return configurationType;
	}

	public static class Builder {
		private Long configurationId;
		private String name;
		private String value;
		private Boolean isActive;
		private Long lastUpdated;
		private ConfigurationType configurationType;
		
		public Builder configurationId(Long configurationId) {
			this.configurationId = configurationId;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder value(String value) {
			this.value = value;
			return this;
		}

		public Builder lastUpdated(Long lastUpdated) {
			this.lastUpdated = lastUpdated;
			return this;
		}
		
		public Builder configurationType(ConfigurationType configurationType) {
			this.configurationType = configurationType;
			return this;
		}
		
		public Builder isActive(boolean isActive) {
			this.isActive = isActive;
			return this;
		}
	
		public Configuration build() {
			return new Configuration(this);
		}
	}
	
	private Configuration(Builder builder) {
		this.configurationId = builder.configurationId;
		this.name = builder.name;
		this.value = builder.value;
		this.isActive = builder.isActive;
		this.configurationType = builder.configurationType;
		this.lastUpdated = builder.lastUpdated;
	}
}
