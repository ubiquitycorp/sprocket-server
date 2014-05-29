package com.ubiquity.identity.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/***
 * Class encapsulating identifying oauth identity
 * 
 * @author chris
 *
 */
@Entity
@Table(name = "external_identity")
public class ExternalIdentity extends Identity {

	/***
	 * External identifier provided
	 */
	@Column(name = "identifier", nullable = false)
	private String identifier;

	/**
	 * Provider, such as Facebook, Google
	 */
	@Column(name = "identity_provider", nullable = true)
	private Integer identityProvider;

	/**
	 * OAuth access / refresh tokens
	 */
	@Column(name = "access_token", nullable = true)
	private String accessToken;

	@Column(name = "secret_token", nullable = true)
	private String secretToken;

	@Column(name = "refresh_token", nullable = true)
	private String refreshToken;

	/***
	 * Optional email field, used for lookup, merging
	 */
	@Column(name = "email", nullable = true)
	private String email;

	/***
	 * Required by JPA
	 */
	protected ExternalIdentity() {}
	
	public String getIdentifier() {
		return identifier;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getSecretToken() {
		return secretToken;
	}

	public void setSecretToken(String secretToken) {
		this.secretToken = secretToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public Integer getIdentityProvider() {
		return identityProvider;
	}

	public void setIdentityProvider(Integer identityProviderType) {
		this.identityProvider = identityProviderType;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public static class Builder {
		private String identifier;
		private Integer identityProvider;
		private String accessToken;
		private String secretToken;
		private String refreshToken;
		private String email;
		private Long lastUpdated;
		private Boolean isActive;
		private User user;

		public Builder identifier(String identifier) {
			this.identifier = identifier;
			return this;
		}

		public Builder user(User user) {
			this.user = user;
			return this;
		}
		
		public Builder identityProvider(Integer identityProvider) {
			this.identityProvider = identityProvider;
			return this;
		}

		public Builder accessToken(String accessToken) {
			this.accessToken = accessToken;
			return this;
		}
		
		public Builder isActive(Boolean isActive) {
			this.isActive = isActive;
			return this;
		}

		public Builder secretToken(String secretToken) {
			this.secretToken = secretToken;
			return this;
		}

		public Builder refreshToken(String refreshToken) {
			this.refreshToken = refreshToken;
			return this;
		}
		
		public Builder lastUpdated(Long lastUpdated) {
			this.lastUpdated = lastUpdated;
			return this;
		}

		public Builder email(String email) {
			this.email = email;
			return this;
		}

		public ExternalIdentity build() {
			return new ExternalIdentity(this);
		}
	}

	private ExternalIdentity(Builder builder) {
		this.identifier = builder.identifier;
		this.identityProvider = builder.identityProvider;
		this.accessToken = builder.accessToken;
		this.secretToken = builder.secretToken;
		this.refreshToken = builder.refreshToken;
		this.email = builder.email;
		this.lastUpdated = builder.lastUpdated;
		this.isActive = builder.isActive;
		this.user = builder.user;
	}
}
