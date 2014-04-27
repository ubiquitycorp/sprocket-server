package com.ubiquity.sprocket.api.dto.model;

import java.util.LinkedList;
import java.util.List;

public class AccountDto {

	private Long userId;
	private String apiKey;
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

	public static class Builder {
		private Long userId;
		private String apiKey;
		private List<IdentityDto> identities;

		public Builder userId(Long userId) {
			this.userId = userId;
			return this;
		}

		public Builder apiKey(String apiKey) {
			this.apiKey = apiKey;
			return this;
		}

		public Builder identities(List<IdentityDto> identities) {
			this.identities = identities;
			return this;
		}

		public AccountDto build() {
			return new AccountDto(this);
		}
	}

	private AccountDto(Builder builder) {
		this.userId = builder.userId;
		this.apiKey = builder.apiKey;
		this.identities = builder.identities;
	}
}
