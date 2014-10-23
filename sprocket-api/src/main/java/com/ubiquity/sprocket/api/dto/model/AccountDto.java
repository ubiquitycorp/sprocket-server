package com.ubiquity.sprocket.api.dto.model;

import java.util.LinkedList;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.ubiquity.sprocket.api.validation.MessageServiceAuthenticationValidation;

public class AccountDto {

	@NotNull(groups = { MessageServiceAuthenticationValidation.class})
	private Long userId;
	
	private String apiKey;
	
	@NotNull(groups = { MessageServiceAuthenticationValidation.class})
	private String authToken;

	private List<IdentityDto> identities = new LinkedList<IdentityDto>();

	public Long getUserId() {
		return userId;
	}

	public String getApiKey() {
		return apiKey;
	}

	public List<IdentityDto> getIdentities() {
		return identities;
	}

	public String getAuthToken() {
		return authToken;
	}

	public static class Builder {
		private Long userId;
		private String apiKey;
		private String authToken;

		public Builder userId(Long userId) {
			this.userId = userId;
			return this;
		}

		public Builder apiKey(String apiKey) {
			this.apiKey = apiKey;
			return this;
		}

		public Builder authToken(String authToken) {
			this.authToken = authToken;
			return this;
		}

		public AccountDto build() {
			return new AccountDto(this);
		}
	}

	private AccountDto(Builder builder) {
		this.userId = builder.userId;
		this.apiKey = builder.apiKey;
		this.authToken = builder.authToken;
	}
}
