package com.ubiquity.sprocket.api.dto.model.user;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import com.ubiquity.sprocket.api.validation.ActivationValidation;
import com.ubiquity.sprocket.api.validation.AuthenticationValidation;
import com.ubiquity.sprocket.api.validation.AuthorizationValidation;
import com.ubiquity.sprocket.api.validation.EngagementValidation;
import com.ubiquity.sprocket.api.validation.RegistrationValidation;
import com.ubiquity.sprocket.api.validation.ResetValidation;

/***
 * 
 * @author chris
 *
 */
public class IdentityDto {

	@NotNull(groups = { RegistrationValidation.class,
			AuthenticationValidation.class, ResetValidation.class })
	@Size(min = 3, max = 80, groups = { RegistrationValidation.class,
			AuthenticationValidation.class, ResetValidation.class })
	private String username;

	@NotNull(groups = { RegistrationValidation.class,
			AuthenticationValidation.class })
	@Size(min = 6, max = 20, groups = { RegistrationValidation.class,
			AuthenticationValidation.class })
	private String password;

	@Size(min = 3, max = 100, groups = RegistrationValidation.class)
	private String displayName;
	
	@NotNull(groups = RegistrationValidation.class)
	@Email(groups = RegistrationValidation.class)
	private String email;

	@NotNull(groups = { RegistrationValidation.class,
			ActivationValidation.class, AuthenticationValidation.class, AuthorizationValidation.class })
	private Integer clientPlatformId;

	@NotNull(groups = ActivationValidation.class)
	@Size(min = 10, groups = ActivationValidation.class)
	private String accessToken;

	@NotNull(groups = {ActivationValidation.class, AuthorizationValidation.class, EngagementValidation.class})
	private Integer externalNetworkId;

	@Size(min = 10, max = 350, groups = ActivationValidation.class)
	private String secretToken;
	
	@Size(min = 10, groups = ActivationValidation.class)
	private String refreshToken;

	@NotNull(groups = { EngagementValidation.class })
	private String identifier;
	
	@NotNull(groups = AuthorizationValidation.class)
	private String code;

	@Size(min = 10, max = 255, groups = AuthorizationValidation.class)
	private String redirectUrl;
	
	@Size(min = 10, max = 255, groups = AuthorizationValidation.class)
	private String oauthToken;
	
	@Size(min = 10, max = 255, groups = AuthorizationValidation.class)
	private String oauthTokenSecret;
	
	@Size(min = 10, max = 255, groups = AuthorizationValidation.class)
	private String oauthVerifier;
	
	
	private Long expiresIn;
	
	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getEmail() {
		return email;
	}

	public Integer getClientPlatformId() {
		return clientPlatformId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getSecretToken() {
		return secretToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public String getIdentifier() {
		return identifier;
	}

	public Integer getExternalNetworkId() {
		return externalNetworkId;
	}

	public String getCode() {
		return code;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}
	
	public String getOauthToken() {
		return oauthToken;
	}
	
	public String getOauthTokenSecret() {
		return oauthTokenSecret;
	}

	public String getOauthVerifier() {
		return oauthVerifier;
	}

	public Long getExpiresIn() {
		return expiresIn;
	}



	public static class Builder {
		private String username;
		private String password;
		private String displayName;
		private String email;
		private Integer clientPlatformId;
		private String accessToken;
		private Integer externalNetworkId;
		private String secretToken;
		private String refreshToken;
		private String identifier;
		private String code;
		public String redirectUrl;
		public Long expiresIn;
		
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
		
		public Builder email(String email) {
			this.email = email;
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

		public Builder externalNetworkId(Integer externalNetworkId) {
			this.externalNetworkId = externalNetworkId;
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

		public Builder identifier(String identifier) {
			this.identifier = identifier;
			return this;
		}
		
		public Builder code(String code) {
			this.code = code;
			return this;
		}
		
		public Builder redirectUrl(String redirectUrl) {
			this.redirectUrl = redirectUrl;
			return this;
		}
		
		public Builder expiresIn(Long expiresIn) {
			this.expiresIn = expiresIn;
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
		this.email = builder.email;
		this.clientPlatformId = builder.clientPlatformId;
		this.accessToken = builder.accessToken;
		this.externalNetworkId = builder.externalNetworkId;
		this.secretToken = builder.secretToken;
		this.refreshToken = builder.refreshToken;
		this.identifier = builder.identifier;
		this.code = builder.code;
		this.redirectUrl = builder.redirectUrl;
		this.expiresIn = builder.expiresIn;
	}
}
