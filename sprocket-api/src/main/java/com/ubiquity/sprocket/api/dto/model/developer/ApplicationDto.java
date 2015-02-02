package com.ubiquity.sprocket.api.dto.model.developer;

import javax.validation.constraints.NotNull;

/**
 * 
 * @author shimaa
 * 
 */
public class ApplicationDto {

	private Long appId;
	private String appKey;
	private String appSecret;

	@NotNull
	private String name;
	@NotNull
	private String description;

	private Long createdAt;

	private Long lastUpdated;

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Long getAppId() {
		return appId;
	}

	public String getAppKey() {
		return appKey;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public static class Builder {

		private Long appId;
		private String appKey;
		private String appSecret;
		private String name;
		private String description;
		private Long createdAt;
		private Long lastUpdated;

		public Builder appId(Long appId) {
			this.appId = appId;
			return this;
		}

		public Builder appKey(String appKey) {
			this.appKey = appKey;
			return this;
		}

		public Builder appSecret(String appSecret) {
			this.appSecret = appSecret;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder description(String description) {
			this.description = description;
			return this;
		}

		public Builder createdAt(Long createdAt) {
			this.createdAt = createdAt;
			return this;
		}

		public Builder lastUpdated(Long lastUpdated) {
			this.lastUpdated = lastUpdated;
			return this;
		}

		public ApplicationDto build() {
			return new ApplicationDto(this);
		}
	}

	private ApplicationDto(Builder builder) {
		this.appId = builder.appId;
		this.appKey = builder.appKey;
		this.appSecret = builder.appSecret;
		this.name = builder.name;
		this.description = builder.description;
		this.createdAt = builder.createdAt;
		this.lastUpdated = builder.lastUpdated;
	}

}
