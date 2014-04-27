package com.ubiquity.identity.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "native_identity")
public class NativeIdentity extends Identity {

	@Column(name = "username")
	private String username;

	@Column(name = "password")
	private String password;

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	protected NativeIdentity() {}
	
	public static class Builder {
		private String username;
		private String password;
		private Boolean isActive;
		private Long lastUpdated;
		private User user;

		public Builder username(String username) {
			this.username = username;
			return this;
		}

		public Builder password(String password) {
			this.password = password;
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

		public Builder user(User user) {
			this.user = user;
			return this;
		}

		public NativeIdentity build() {
			return new NativeIdentity(this);
		}
	}

	private NativeIdentity(Builder builder) {
		this.username = builder.username;
		this.password = builder.password;
		super.user = builder.user;
		super.isActive = builder.isActive;
		super.lastUpdated = builder.lastUpdated;
	}
}
