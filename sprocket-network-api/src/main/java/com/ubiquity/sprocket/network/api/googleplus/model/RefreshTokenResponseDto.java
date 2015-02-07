package com.ubiquity.sprocket.network.api.googleplus.model;


public class RefreshTokenResponseDto {

	private String access_token;
	private Long expires_in;
	private String token_type;
	private String scope;
	
	public String getToken_type() {
		return token_type;
	}

	public Long getExpires_in() {
		return expires_in;
	}

	public String getAccess_token() {
		return access_token;
	}

	public String getScope() {
		return scope;
	}
	
	public static class Builder {
		private String access_token;
		private Long expires_in;
		private String token_type;
		private String scope;

		public Builder access_token(String access_token) {
			this.access_token = access_token;
			return this;
		}

		public Builder expires_in(Long expires_in) {
			this.expires_in = expires_in;
			return this;
		}

		public Builder token_type(String token_type) {
			this.token_type = token_type;
			return this;
		}

		public Builder scope(String scope) {
			this.scope = scope;
			return this;
		}

		public RefreshTokenResponseDto build() {
			return new RefreshTokenResponseDto(this);
		}
	}

	private RefreshTokenResponseDto(Builder builder) {
		this.access_token = builder.access_token;
		this.expires_in = builder.expires_in;
		this.token_type = builder.token_type;
		this.scope = builder.scope;

	}

}
