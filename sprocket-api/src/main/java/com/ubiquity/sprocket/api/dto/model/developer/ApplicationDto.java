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
		
		public Builder appId (Long appId)
		{
			this.appId = appId;
			return this;
		}
		
		public Builder appKey(String appKey)
		{
			this.appKey = appKey;
			return this;
		}
		
		public Builder appSecret(String appSecret)
		{
			this.appSecret = appSecret;
			return this;
		}
		
		public ApplicationDto build()
		{
			return new ApplicationDto(this);
		}
	}
	
	private ApplicationDto(Builder builder)
	{
		this.appId = builder.appId;
		this.appKey = builder.appKey;
		this.appSecret = builder.appSecret;
	}
	
}
