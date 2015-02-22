package com.ubiquity.sprocket.api.dto.model.developer;

import javax.validation.constraints.NotNull;

import com.ubiquity.identity.domain.ClientPlatform;

public class ExternalApplicationDto {

	@NotNull
	private Integer externalNetwork;

	@NotNull
	private ClientPlatform clientPlatform;

	@NotNull
	private String consumerKey;

	@NotNull
	private String consumerSecret;

	private String apiKey;

	private String token;

	private String tokenSecret;

	private String userAgent;

	private String redirectURL;

	public Integer getExternalNetwork() {
		return externalNetwork;
	}

	public ClientPlatform getClientPlatform() {
		return clientPlatform;
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
		private Integer externalNetwork;
		private ClientPlatform clientPlatform;
		private String consumerKey;
		private String consumerSecret;
		private String apiKey;
		private String token;
		private String tokenSecret;
		private String userAgent;
		private String redirectURL;

		public Builder externalNetwork(Integer externalNetwork) {
			this.externalNetwork = externalNetwork;
			return this;
		}

		public Builder clientPlatform(ClientPlatform clientPlatform) {
			this.clientPlatform = clientPlatform;
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
		this.externalNetwork = builder.externalNetwork;
		this.clientPlatform = builder.clientPlatform;
		this.consumerKey = builder.consumerKey;
		this.consumerSecret = builder.consumerSecret;
		this.apiKey = builder.apiKey;
		this.token = builder.token;
		this.tokenSecret = builder.tokenSecret;
		this.userAgent = builder.userAgent;
		this.redirectURL = builder.redirectURL;
	}
}
