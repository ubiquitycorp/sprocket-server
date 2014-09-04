package com.ubiquity.sprocket.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ubiquity.identity.domain.User;

@Entity
@Table(name = "location")
public class Location implements Serializable {

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

	@Column(name = "latitude", nullable = false)
	private Double latitude;

	@Column(name = "longitude", nullable = false)
	private Double longitude;

	private Double altitude;

	private Integer accuracy;

	public User getUser() {
		return user;
	}

	public Double getLatitude() {
		return latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public Double getAltitude() {
		return altitude;
	}

	public Integer getAccuracy() {
		return accuracy;
	}

	public Long getLocationId() {
		return locationId;
	}

	public Long getLastUpdated() {
		return lastUpdated;
	}

	public static class Builder {
		private Long locationId;
		private User user;
		private Long lastUpdated;
		private Double latitude;
		private Double longitude;
		private Double altitude;
		private Integer accuracy;

		public Builder locationId(Long locationId) {
			this.locationId = locationId;
			return this;
		}

		public Builder user(User user) {
			this.user = user;
			return this;
		}

		public Builder lastUpdated(Long lastUpdated) {
			this.lastUpdated = lastUpdated;
			return this;
		}

		public Builder latitude(Double latitude) {
			this.latitude = latitude;
			return this;
		}

		public Builder longitude(Double longitude) {
			this.longitude = longitude;
			return this;
		}

		public Builder altitude(Double altitude) {
			this.altitude = altitude;
			return this;
		}

		public Builder accuracy(Integer accuracy) {
			this.accuracy = accuracy;
			return this;
		}

		public Location build() {
			return new Location(this);
		}
	}

	private Location(Builder builder) {
		this.locationId = builder.locationId;
		this.user = builder.user;
		this.lastUpdated = builder.lastUpdated;
		this.latitude = builder.latitude;
		this.longitude = builder.longitude;
		this.altitude = builder.altitude;
		this.accuracy = builder.accuracy;
	}

	@Override
	public String toString() {
		return "Location [locationId=" + locationId + ", user=" + user
				+ ", lastUpdated=" + lastUpdated + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", altitude=" + altitude
				+ ", accuracy=" + accuracy + "]";
	}
	
	
}
