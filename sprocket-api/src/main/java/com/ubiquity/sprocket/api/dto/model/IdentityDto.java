package com.ubiquity.sprocket.api.dto.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.ubiquity.sprocket.api.validation.ActivationValidation;
import com.ubiquity.sprocket.api.validation.RegistrationValidation;

/***
 * 
 * @author chris
 *
 */
public class IdentityDto {

	@NotNull(groups = RegistrationValidation.class)
	@Size(min = 3, max = 80, groups = RegistrationValidation.class)
	private String username;

	@NotNull(groups = RegistrationValidation.class)
	@Size(min = 6, max = 20, groups = RegistrationValidation.class)
	private String password;

	@NotNull(groups = RegistrationValidation.class)
	@Size(min = 3, max = 100, groups = RegistrationValidation.class)
	private String displayName;

	@NotNull(groups = {RegistrationValidation.class, ActivationValidation.class})
	private Integer clientPlatformId;

	@NotNull(groups = ActivationValidation.class)
	@Size(min = 10, max = 255, groups = ActivationValidation.class)
	private String accessToken;

	@NotNull(groups = ActivationValidation.class)
	private Integer identityProviderId;

	private String providerIdentifier;

	@Size(min = 10, max = 255, groups = ActivationValidation.class)
	private String secretToken;

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getDisplayName() {
		return displayName;
	}

	public Integer getClientPlatformId() {
		return clientPlatformId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public Integer getIdentityProviderId() {
		return identityProviderId;
	}

	public String getSecretToken() {
		return secretToken;
	}

	public String getProviderIdentifier() {
		return providerIdentifier;
	}

	public static class Builder {
		private String username;
		private String password;
		private String displayName;
		private Integer clientPlatformId;
		private String accessToken;
		private Integer identityProviderId;
		private String providerIdentifier;
		private String secretToken;

		public Builder username(String username) {
			this.username = username;
			return this;
		}

		public Builder password(String password) {
			this.password = password;
			return this;
		}

		public Builder displayName(String displayName) {
			this.displayName = displayName;
			return this;
		}

		public Builder clientPlatformId(Integer clientPlatformId) {
			this.clientPlatformId = clientPlatformId;
			return this;
		}

		public Builder accessToken(String accessToken) {
			this.accessToken = accessToken;
			return this;
		}

		public Builder identityProviderId(Integer identityProviderId) {
			this.identityProviderId = identityProviderId;
			return this;
		}

		public Builder providerIdentifier(String providerIdentifier) {
			this.providerIdentifier = providerIdentifier;
			return this;
		}

		public Builder secretToken(String secretToken) {
			this.secretToken = secretToken;
			return this;
		}

		public IdentityDto build() {
			return new IdentityDto(this);
		}
	}

	private IdentityDto(Builder builder) {
		this.username = builder.username;
		this.password = builder.password;
		this.displayName = builder.displayName;
		this.clientPlatformId = builder.clientPlatformId;
		this.accessToken = builder.accessToken;
		this.identityProviderId = builder.identityProviderId;
		this.providerIdentifier = builder.providerIdentifier;
		this.secretToken = builder.secretToken;
	}
}
