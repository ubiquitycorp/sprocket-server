package com.ubiquity.sprocket.api.dto.model.developer;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import com.ubiquity.sprocket.api.validation.AuthenticationValidation;
import com.ubiquity.sprocket.api.validation.RegistrationValidation;

/***
 * 
 * @author peter.tadros
 *
 */
public class DeveloperDto {

	private Long developerId;

	private String apiKey;
	
	@NotNull(groups = { RegistrationValidation.class,
			AuthenticationValidation.class})
	@Size(min = 3, max = 80, groups = { RegistrationValidation.class,
			AuthenticationValidation.class })
	private String username;

	@NotNull(groups = { RegistrationValidation.class})
	@Size(min = 6, max = 20, groups = { RegistrationValidation.class,
			AuthenticationValidation.class })
	private String password;

	@Size(min = 3, max = 100, groups = RegistrationValidation.class)
	private String displayName;
	
	@NotNull(groups = RegistrationValidation.class)
	@Email(groups = RegistrationValidation.class)
	private String email;

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
	
	public Long getDeveloperId() {
		return developerId;
	}

	public String getApiKey() {
		return apiKey;
	}

	public static class Builder {
		private Long developerId;
		private String apiKey;

		public Builder developerId(Long developerId) {
			this.developerId = developerId;
			return this;
		}

		public Builder apiKey(String apiKey) {
			this.apiKey = apiKey;
			return this;
		}

		public DeveloperDto build() {
			return new DeveloperDto(this);
		}
	}
	
	private DeveloperDto(Builder builder) {
		this.developerId = builder.developerId;
		this.apiKey = builder.apiKey;
	}
}
