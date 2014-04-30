package com.ubiquity.social.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.ubiquity.identity.domain.Identity;
import com.ubiquity.identity.domain.User;

/***
 * Class encapsulating identifying information provided by an external network
 * 
 * @author chris
 *
 */
@Entity
@Table(name = "external_identity")
public class ExternalIdentity extends Identity {

	@Column(name = "identifier", nullable = false)
	private String identifier;

	@Column(name = "social_provider_type", nullable = true)
	private SocialProvider socialProvider;

	@Column(name = "content_provider_type", nullable = true)
	private ContentProvider contentProvider;

	@Column(name = "access_token", nullable = true)
	private String accessToken;

	@Column(name = "secret_token", nullable = true)
	private String secretToken;

	@Column(name = "refresh_token", nullable = true)
	private String refreshToken;

	@Column(name = "email", nullable = true)
	private String email;

	protected ExternalIdentity() {
		super();
	}

	public ContentProvider getContentProvider() {
		return contentProvider;
	}

	public String getIdentifier() {
		return identifier;
	}

	public SocialProvider getSocialProvider() {
		return socialProvider;
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

	public void setSocialProviderType(SocialProvider socialProviderType) {
		this.socialProvider = socialProviderType;
	}

	public void setContentProviderType(ContentProvider contentProviderType) {
		this.contentProvider = contentProviderType;
	}

	public static class Builder {
		private String identifier;
		private SocialProvider socialProviderType;
		private ContentProvider contentProviderType;
		private String accessToken;
		private String secretToken;
		private String refreshToken;
		private String email;
		private Boolean isActive;
		private Long lastUpdated;
		private User user;

		public Builder identifier(String identifier) {
			this.identifier = identifier;
			return this;
		}

		public Builder socialProvider(SocialProvider socialProviderType) {
			this.socialProviderType = socialProviderType;
			return this;
		}

		public Builder contentProvider(
				ContentProvider contentProviderType) {
			this.contentProviderType = contentProviderType;
			return this;
		}

		public Builder user(User user) {
			this.user = user;
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

		public Builder email(String email) {
			this.email = email;
			return this;
		}
		
		public Builder lastUpdated(Long lastUpdated) {
			this.lastUpdated = lastUpdated;
			return this;
		}

		public ExternalIdentity build() {
			return new ExternalIdentity(this);
		}
	}

	private ExternalIdentity(Builder builder) {
		this.identifier = builder.identifier;
		this.socialProvider = builder.socialProviderType;
		this.contentProvider = builder.contentProviderType;
		this.accessToken = builder.accessToken;
		this.secretToken = builder.secretToken;
		this.refreshToken = builder.refreshToken;
		this.email = builder.email;
		super.isActive = builder.isActive;
		super.lastUpdated = builder.lastUpdated;
		super.user = builder.user;
	}
}
