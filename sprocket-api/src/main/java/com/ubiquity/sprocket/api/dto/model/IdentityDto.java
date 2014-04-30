package com.ubiquity.sprocket.api.dto.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.ubiquity.sprocket.api.validation.ActivationValidation;
import com.ubiquity.sprocket.api.validation.AuthenticationValidation;
import com.ubiquity.sprocket.api.validation.RegistrationValidation;

/***
 * 
 * @author chris
 *
 */
public class IdentityDto {

	@NotNull(groups = {RegistrationValidation.class, AuthenticationValidation.class})
	@Size(min = 3, max = 80, groups = {RegistrationValidation.class, AuthenticationValidation.class})
	private String username;

	@NotNull(groups = {RegistrationValidation.class, AuthenticationValidation.class})
	@Size(min = 6, max = 20, groups = {RegistrationValidation.class, AuthenticationValidation.class})
	private String password;

	@NotNull(groups = RegistrationValidation.class)
	@Size(min = 3, max = 100, groups = RegistrationValidation.class)
	private String displayName;

	@NotNull(groups = { RegistrationValidation.class,
			ActivationValidation.class, AuthenticationValidation.class })
	private Integer clientPlatformId;

	@NotNull(groups = ActivationValidation.class)
	@Size(min = 10, max = 255, groups = ActivationValidation.class)
	private String accessToken;

	@NotNull(groups = ActivationValidation.class)
	private Integer socialIdentityProviderId;
	
	private Integer contentIdentityProviderId;

	@Size(min = 10, max = 255, groups = ActivationValidation.class)
	private String secretToken;
	
	private String identifier;


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

	public Integer getSocialIdentityProviderId() {
		return socialIdentityProviderId;
	}

	public String getSecretToken() {
		return secretToken;
	}

	public String getIdentifier() {
		return identifier;
	}

	
	public Integer getContentIdentityProviderId() {
		return contentIdentityProviderId;
	}


	public static class Builder {
		private String username;
		private String password;
		private String displayName;
		private Integer clientPlatformId;
		private String accessToken;
		private Integer identityProviderId;
		private String identifier;
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

		public Builder identifier(String identifier) {
			this.identifier = identifier;
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
		this.socialIdentityProviderId = builder.identityProviderId;
		this.identifier = builder.identifier;
		this.secretToken = builder.secretToken;
	}
}
