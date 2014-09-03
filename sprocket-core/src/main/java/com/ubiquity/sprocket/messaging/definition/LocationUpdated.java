package com.ubiquity.sprocket.messaging.definition;

/**
 * A message indicating an authentication event
 * @author chris
 *
 */
public class LocationUpdated {

	private Long userId;

	private Long lastUpdated;

	private Double latitude;

	private Double longitude;

	private Double altitude;

	private Integer accuracy;

	public Long getUserId() {
		return userId;
	}

	public Long getLastUpdated() {
		return lastUpdated;
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

	public static class Builder {
		private Long userId;
		private Long lastUpdated;
		private Double latitude;
		private Double longitude;
		private Double altitude;
		private Integer accuracy;

		public Builder userId(Long userId) {
			this.userId = userId;
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
