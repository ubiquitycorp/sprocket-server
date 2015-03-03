package com.ubiquity.sprocket.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.ubiquity.identity.domain.Application;
import com.ubiquity.identity.domain.User;
/***
 * 
 * @author peter.tadros
 *
 */
@Entity
public class SprocketUser extends User {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "external_identifier", nullable = true, length = 50)
	private String externalIdentifier;

	@ManyToOne
	@JoinColumn(name = "app_id", nullable = true)
	private Application createdBy;

	public String getExternalIdentifier() {
		return externalIdentifier;
	}

	public Application getCreatedBy() {
		return createdBy;
	}

	public static class Builder extends User.Builder {
		private String externalIdentifier;
		private Application createdBy;

		public Builder externalIdentifier(String externalIdentifier) {
			this.externalIdentifier = externalIdentifier;
			return this;
		}

		public Builder createdBy(Application createdBy) {
			this.createdBy = createdBy;
			return this;
		}

		public SprocketUser build() {
			return new SprocketUser(this);
		}
	}

	private SprocketUser(Builder builder) {
		super(builder);
		this.externalIdentifier = builder.externalIdentifier;
		this.createdBy = builder.createdBy;
	}
}
