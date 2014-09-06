package com.ubiquity.sprocket.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ubiquity.identity.domain.User;

@Entity
@Table(name = "user_location")
public class UserLocation implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "location_id")
	private Long locationId;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "last_updated", nullable = false)
	private Long lastUpdated;
	
	@Column(name = "accuracy", nullable = true)
	private Integer accuracy;
	
	@Embedded
	private Location location;

	public User getUser() {
		return user;
	}

	public Long getLocationId() {
		return locationId;
	}

	public Long getLastUpdated() {
		return lastUpdated;
	}

	public Location getLocation() {
		return location;
	}

	public static class Builder {
		private User user;
		private Long lastUpdated;
		private Location location;
		private Integer accuracy;

		public Builder user(User user) {
			this.user = user;
			return this;
		}

		public Builder lastUpdated(Long lastUpdated) {
			this.lastUpdated = lastUpdated;
			return this;
		}

		public Builder accuracy(Integer accuracy) {
			this.accuracy = accuracy;
			return this;
		}
		
		public Builder location(Location location) {
			this.location = location;
			return this;
		}

		public UserLocation build() {
			return new UserLocation(this);
		}
	}

	private UserLocation(Builder builder) {
		this.user = builder.user;
		this.lastUpdated = builder.lastUpdated;
		this.location = builder.location;
		this.accuracy = builder.accuracy;
	}
}
