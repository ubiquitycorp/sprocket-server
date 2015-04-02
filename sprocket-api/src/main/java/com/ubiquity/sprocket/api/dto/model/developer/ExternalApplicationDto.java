package com.ubiquity.sprocket.api.dto.model.developer;

import java.util.LinkedList;
import java.util.List;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.integration.domain.ExternalNetwork;

public class ExternalApplicationDto {

	private Long externalApplicationId;

	@NotNull
	private Integer externalNetworkId;

	@NotNull
	private List<Integer> clientPlatformIds = new LinkedList<Integer>();;

	@NotNull
	private String consumerKey;

	private String consumerSecret;

	private String apiKey;

	private String token;

	private String tokenSecret;

	private String userAgent;

	private String redirectUrl;

	private Long lastUpdated;

	private Long createdAt;

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

	public Long getLastUpdated() {
		return lastUpdated;
	}

	public Long getCreatedAt() {
		return createdAt;
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
		private Long lastUpdated;
		private Long createdAt;

		public Builder externalApplicationId(Long externalApplicationId) {
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

		public Builder createdAt(Long createdAt) {
			this.createdAt = createdAt;
			return this;
		}

		public Builder lastUpdated(Long lastUpdated) {
			this.lastUpdated = lastUpdated;
			return this;
		}

		public ExternalApplicationDto build() {
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
		this.createdAt = builder.createdAt;
		this.lastUpdated = builder.lastUpdated;
	}
	@AssertTrue
	public boolean validate() {
		if (externalNetworkId == ExternalNetwork.Twitter.ordinal()) {
			if (consumerSecret == null)
				throw new IllegalArgumentException(
						"consumerSecret couldn't be null.");
			else if (redirectUrl == null)
				throw new IllegalArgumentException(
						"redirectUrl couldn't be null.");

		} else if (externalNetworkId == ExternalNetwork.Facebook.ordinal()) {
			if (clientPlatformIds.contains(ClientPlatform.WEB)
					&& consumerSecret == null)
				throw new IllegalArgumentException(
						"consumerSecret couldn't be null.");
		} else if (externalNetworkId == ExternalNetwork.LinkedIn.ordinal()) {
			if (consumerSecret == null)
				throw new IllegalArgumentException(
						"consumerSecret couldn't be null.");
		} else if (externalNetworkId == ExternalNetwork.GooglePlus.ordinal()) {
			if (consumerSecret == null)
				throw new IllegalArgumentException(
						"consumerSecret couldn't be null.");
		} else if (externalNetworkId == ExternalNetwork.Vimeo.ordinal()) {
			if (consumerSecret == null)
				throw new IllegalArgumentException(
						"consumerSecret couldn't be null.");
		} else if (externalNetworkId == ExternalNetwork.Yelp.ordinal()) {
			if (consumerSecret == null)
				throw new IllegalArgumentException(
						"consumerSecret couldn't be null.");
			else if (token == null)
				throw new IllegalArgumentException(
						"token couldn't be null.");
			else if (tokenSecret == null)
				throw new IllegalArgumentException(
						"tokenSecret couldn't be null.");
		} else if (externalNetworkId == ExternalNetwork.Tumblr.ordinal()) {
			if (consumerSecret == null)
				throw new IllegalArgumentException(
						"consumerSecret couldn't be null.");
			else if (redirectUrl == null)
				throw new IllegalArgumentException(
						"redirectUrl couldn't be null.");
		} else if (externalNetworkId == ExternalNetwork.Reddit.ordinal()) {
			if (redirectUrl == null)
				throw new IllegalArgumentException(
						"redirectUrl couldn't be null.");
			else if (userAgent == null)
				throw new IllegalArgumentException(
						"userAgent couldn't be null.");
			if (clientPlatformIds.contains(ClientPlatform.WEB)
					&& consumerSecret == null)
				throw new IllegalArgumentException(
						"consumerSecret couldn't be null.");
		} else {
			throw new IllegalArgumentException(
					"External network number is not supported.");
		}
		return true;

	}
}
