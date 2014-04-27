package com.ubiquity.social.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.ubiquity.identity.domain.Identity;
import com.ubiquity.identity.domain.User;

/***
 * Embeddable class encapsulating identifying information provided by an external social network
 * 
 * @author chris
 *
 */
@Entity
@Table(name = "social_identity")
public class SocialIdentity extends Identity {

	@Column(name = "identifier", nullable = false)
	private String identifier;

	@Column(name = "social_provider_type", nullable = false)
	private SocialProviderType socialProviderType;

	@Column(name = "access_token", nullable = true)
	private String accessToken;

	@Column(name = "secret_token", nullable = true)
	private String secretToken;

	@Column(name = "refresh_token", nullable = true)
	private String refreshToken;

	@Column(name = "email", nullable = true)
	private String email;

	protected SocialIdentity() {
		super();
	}

	public String getIdentifier() {
		return identifier;
	}

	public SocialProviderType getSocialProviderType() {
		return socialProviderType;
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

	public void setSocialProviderType(SocialProviderType socialProviderType) {
		this.socialProviderType = socialProviderType;
	}



	public static class Builder {
		private String identifier;
		private SocialProviderType socialProviderType;
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
		
		public Builder isActive(Boolean isActive) {
			this.isActive = isActive;
			return this;
		}
		
		public Builder lastUpdated(Long lastUpdated) {
			this.lastUpdated = lastUpdated;
			return this;
		}

		public Builder socialProviderType(SocialProviderType socialProviderType) {
			this.socialProviderType = socialProviderType;
			return this;
		}

		public Builder accessToken(String accessToken) {
			this.accessToken = accessToken;
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

		public Builder email(String email) {
			this.email = email;
			return this;
		}

		public SocialIdentity build() {
			return new SocialIdentity(this);
		}
		
		public Builder user(User user) {
			this.user = user;
			return this;
		}
	}

	private SocialIdentity(Builder builder) {
		this.identifier = builder.identifier;
		this.socialProviderType = builder.socialProviderType;
		this.accessToken = builder.accessToken;
		this.secretToken = builder.secretToken;
		this.refreshToken = builder.refreshToken;
		this.email = builder.email;	
		super.isActive = builder.isActive;
		super.lastUpdated = builder.lastUpdated;
		super.user = builder.user;
		
	}
}
