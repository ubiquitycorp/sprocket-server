package com.ubiquity.sprocket.messaging.definition;

import java.math.BigDecimal;

/**
 * A message indicating an authentication event
 * @author chris
 *
 */
public class LocationUpdated {

	private Long userId;

	private Long lastUpdated;

	private BigDecimal latitude;

	private BigDecimal longitude;

	private BigDecimal altitude;

	private Integer accuracy;

	public Long getUserId() {
		return userId;
	}

	public Long getLastUpdated() {
		return lastUpdated;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public BigDecimal getAltitude() {
		return altitude;
	}

	public Integer getAccuracy() {
		return accuracy;
	}

	public static class Builder {
		private Long userId;
		private Long lastUpdated;
		private BigDecimal latitude;
		private BigDecimal longitude;
		private BigDecimal altitude;
		private Integer accuracy;

		public Builder userId(Long userId) {
			this.userId = userId;
			return this;
		}

		public Builder lastUpdated(Long lastUpdated) {
			this.lastUpdated = lastUpdated;
			return this;
		}

		public Builder latitude(BigDecimal latitude) {
			this.latitude = latitude;
			return this;
		}

		public Builder longitude(BigDecimal longitude) {
			this.longitude = longitude;
			return this;
		}

		public Builder altitude(BigDecimal altitude) {
			this.altitude = altitude;
			return this;
		}

		public Builder accuracy(Integer accuracy) {
			this.accuracy = accuracy;
			return this;
		}

		public LocationUpdated build() {
			return new LocationUpdated(this);
		}
	}

	private LocationUpdated(Builder builder) {
		this.userId = builder.userId;
		this.lastUpdated = builder.lastUpdated;
		this.latitude = builder.latitude;
		this.longitude = builder.longitude;
		this.altitude = builder.altitude;
		this.accuracy = builder.accuracy;
	}
}
