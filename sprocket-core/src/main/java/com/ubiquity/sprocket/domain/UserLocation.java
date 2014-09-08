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

	@Column(name = "horizontal_accuracy", nullable = true, scale = 12, precision = 8)
	private Double horizontalAccuracy;

	@Column(name = "vertical_accuracy", nullable = true, scale = 12, precision = 8)
	private Double verticalAccuracy;

	@Column(name = "timestamp", nullable = false)
	private Long timestamp;

	@Embedded
	private Location location;

	protected UserLocation() {
	}

	
	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	

	public void setLastUpdated(Long lastUpdated) {
		this.lastUpdated = lastUpdated;
	}


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

	public Double getHorizontalAccuracy() {
		return horizontalAccuracy;
	}

	public Double getVerticalAccuracy() {
		return verticalAccuracy;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public static class Builder {
		private User user;
		private Long lastUpdated;
		private Double horizontalAccuracy;
		private Double verticalAccuracy;
		private Long timestamp;
		private Location location;

		public Builder user(User user) {
			this.user = user;
			return this;
		}

		public Builder lastUpdated(Long lastUpdated) {
			this.lastUpdated = lastUpdated;
			return this;
		}

		public Builder horizontalAccuracy(Double horizontalAccuracy) {
			this.horizontalAccuracy = horizontalAccuracy;
			return this;
		}

		public Builder verticalAccuracy(Double verticalAccuracy) {
			this.verticalAccuracy = verticalAccuracy;
			return this;
		}

		public Builder timestamp(Long timestamp) {
			this.timestamp = timestamp;
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
		this.horizontalAccuracy = builder.horizontalAccuracy;
		this.verticalAccuracy = builder.verticalAccuracy;
		this.timestamp = builder.timestamp;
		this.location = builder.location;
	}
}
