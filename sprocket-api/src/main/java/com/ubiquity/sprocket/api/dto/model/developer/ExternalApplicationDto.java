package com.ubiquity.sprocket.api.dto.model.developer;

import javax.validation.constraints.NotNull;

public class ExternalApplicationDto {

	@NotNull
	private Integer externalNetworkId;

	@NotNull
	private Integer clientPlatformId;

	@NotNull
	private String consumerKey;

	@NotNull
	private String consumerSecret;

	private String apiKey;

	private String token;

	private String tokenSecret;

	private String userAgent;

	private String redirectURL;

	public Integer getExternalNetworkId() {
		return externalNetworkId;
	}

	public Integer getClientPlatformId() {
		return clientPlatformId;
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

	public String getRedirectURL() {
		return redirectURL;
	}

	public static class Builder {
		private Integer externalNetworkId;
		private Integer clientPlatformId;
		private String consumerKey;
		private String consumerSecret;
		private String apiKey;
		private String token;
		private String tokenSecret;
		private String userAgent;
		private String redirectURL;

		public Builder externalNetwork(Integer externalNetwork) {
			this.externalNetworkId = externalNetwork;
			return this;
		}

		public Builder clientPlatformId(Integer clientPlatformId) {
			this.clientPlatformId = clientPlatformId;
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

		public Builder redirectURL(String redirectURL) {
			this.redirectURL = redirectURL;
			return this;
		}
	}

	private ExternalApplicationDto(Builder builder) {
		this.externalNetworkId = builder.externalNetworkId;
		this.clientPlatformId = builder.clientPlatformId;
		this.consumerKey = builder.consumerKey;
		this.consumerSecret = builder.consumerSecret;
		this.apiKey = builder.apiKey;
		this.token = builder.token;
		this.tokenSecret = builder.tokenSecret;
		this.userAgent = builder.userAgent;
		this.redirectURL = builder.redirectURL;
	}
}
