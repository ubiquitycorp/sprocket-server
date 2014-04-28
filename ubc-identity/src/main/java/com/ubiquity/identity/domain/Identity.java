package com.ubiquity.identity.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "identity")
@Inheritance(strategy = InheritanceType.JOINED)
public class Identity {

	@Id
	@GeneratedValue
	@Column(name = "identity_id")
	private Long identityId;

	@Column(name = "is_active", nullable = false)
	protected Boolean isActive;

	@Column(name = "last_udpated", nullable = false)
	protected Long lastUpdated;

	@ManyToOne
	@JoinColumn(name = "user_id")
	protected User user;

	protected Identity() {
	}

	
	public void setIdentityId(Long identityId) {
		this.identityId = identityId;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public Long getIdentityId() {
		return identityId;
	}


	public User getUser() {
		return user;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Long getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Long lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public static class Builder {
		private Long identityId;
		private Boolean isActive;
		private Long lastUpdated;
		private User user;

		public Builder identityId(Long identityId) {
			this.identityId = identityId;
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
	

		public Identity build() {
			return new Identity(this);
		}
	}

	private Identity(Builder builder) {
		this.identityId = builder.identityId;
		this.isActive = builder.isActive;
		this.lastUpdated = builder.lastUpdated;
		this.user = builder.user;
	}
	
	
}
