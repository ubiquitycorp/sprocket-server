package com.ubiquity.sprocket.api.dto.model.developer;

import java.util.LinkedList;
import java.util.List;

import javax.validation.constraints.NotNull;

public class ExternalApplicationDto {
	
	private Long externalApplicationId;
	
	@NotNull
	private Integer externalNetworkId;

	@NotNull
	private List<Integer> clientPlatformIds = new LinkedList<Integer>(); ;

	@NotNull
	private String consumerKey;

	@NotNull
	private String consumerSecret;

	private String apiKey;

	private String token;

	private String tokenSecret;

	private String userAgent;

	private String redirectUrl;

	public Integer getExternalNetworkId() {
		return externalNetworkId;
	}

	public List<Integer> getClientPlatformIds() {
		return clientPlatformIds;
	}

	public String getConsumerKey() {
		return consumerKey;
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}

	public String getApiKey() {
		return apiKey;
	}

	public String getToken() {
		return token;
	}

	public String getTokenSecret() {
		return tokenSecret;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public Long getExternalApplicationId() {
		return externalApplicationId;
	}

	public void setExternalApplicationId(Long externalApplicationId) {
		this.externalApplicationId = externalApplicationId;
	}



	public static class Builder {
		private Long externalApplicationId;
		private Integer externalNetworkId;
		private String consumerKey;
		private String consumerSecret;
		private String apiKey;
		private String token;
		private String tokenSecret;
		private String userAgent;
		private String redirectUrl;
		
		public Builder externalApplicationId(Long externalApplicationId){
			this.externalApplicationId = externalApplicationId;
			return this;
		}
		public Builder externalNetwork(Integer externalNetwork) {
			this.externalNetworkId = externalNetwork;
			return this;
		}

		public Builder consumerKey(String consumerKey) {
			this.consumerKey = consumerKey;
			return this;
		}

		public Builder consumerSecret(String consumerSecret) {
			this.consumerSecret = consumerSecret;
			return this;
		}

		public Builder apiKey(String apiKey) {
			this.apiKey = apiKey;
			return this;
		}

		public Builder token(String token) {
			this.token = token;
			return this;
		}

		public Builder tokenSecret(String tokenSecret) {
			this.tokenSecret = tokenSecret;
			return this;
		}

		public Builder userAgent(String userAgent) {
			this.userAgent = userAgent;
			return this;
		}

		public Builder redirectUrl(String redirectUrl) {
			this.redirectUrl = redirectUrl;
			return this;
		}
		public ExternalApplicationDto build(){
			return new ExternalApplicationDto(this);
		}
	}

	private ExternalApplicationDto(Builder builder) {
		this.externalApplicationId = builder.externalApplicationId;
		this.externalNetworkId = builder.externalNetworkId;
		this.consumerKey = builder.consumerKey;
		this.consumerSecret = builder.consumerSecret;
		this.apiKey = builder.apiKey;
		this.token = builder.token;
		this.tokenSecret = builder.tokenSecret;
		this.userAgent = builder.userAgent;
		this.redirectUrl = builder.redirectUrl;
	}
}
