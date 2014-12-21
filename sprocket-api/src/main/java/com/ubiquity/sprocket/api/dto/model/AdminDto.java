package com.ubiquity.sprocket.api.dto.model;

import javax.validation.constraints.Size;

import com.esotericsoftware.kryo.NotNull;
import com.ubiquity.identity.domain.Admin;
import com.ubiquity.sprocket.api.dto.model.IdentityDto.Builder;
import com.ubiquity.sprocket.api.validation.AuthenticationValidation;

public class AdminDto {

	private Long adminId;

	private String apiKey;
	@NotNull
	@Size(min = 3, max = 80, groups = { AuthenticationValidation.class })
	private String username;

	@NotNull
	@Size(min = 6, max = 20, groups = { AuthenticationValidation.class })
	private String password;

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public static class Builder {
		private Long adminId;
		private String username;
		private String password;
		private String apiKey;

		public Builder adminId(Long adminId) {
			this.adminId = adminId;
			return this;
		}

		public Builder username(String username) {
			this.username = username;
			return this;
		}

		public Builder password(String password) {
			this.password = password;
			return this;
		}

		public Builder apiKey(String apiKey) {
			this.apiKey = apiKey;
			return this;
		}

		public AdminDto build() {
			return new AdminDto(this);
		}
	}
	
	private AdminDto(Builder builder) {
		this.adminId = builder.adminId;
		this.apiKey = builder.apiKey;
		this.username = builder.username;
		this.password = builder.password;
	}
}
