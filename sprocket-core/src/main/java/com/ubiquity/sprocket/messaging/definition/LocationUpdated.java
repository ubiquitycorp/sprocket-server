package com.ubiquity.sprocket.messaging.definition;

import java.math.BigDecimal;

/**
 * A message indicating an authentication event
 * @author chris
 *
 */
public class LocationUpdated {

	private Long userId;

	private Long timestamp;

	private BigDecimal latitude;

	private BigDecimal longitude;

	private BigDecimal altitude;

	private Double horizontalAccuracy;

	private Double verticalAccuracy;

	public Long getUserId() {
		return userId;
	}

	public Long getTimestamp() {
		return timestamp;
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

	public Double getHorizontalAccuracy() {
		return horizontalAccuracy;
	}

	public Double getVerticalAccuracy() {
		return verticalAccuracy;
	}

	public static class Builder {
		private Long userId;
		private Long timestamp;
		private BigDecimal latitude;
		private BigDecimal longitude;
		private BigDecimal altitude;
		private Double horizontalAccuracy;
		private Double verticalAccuracy;

		public Builder userId(Long userId) {
			this.userId = userId;
			return this;
		}

		public Builder timestamp(Long timestamp) {
			this.timestamp = timestamp;
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

		public Builder horizontalAccuracy(Double horizontalAccuracy) {
			this.horizontalAccuracy = horizontalAccuracy;
			return this;
		}

		public Builder verticalAccuracy(Double verticalAccuracy) {
			this.verticalAccuracy = verticalAccuracy;
			return this;
		}

		public LocationUpdated build() {
			return new LocationUpdated(this);
		}
	}

	private LocationUpdated(Builder builder) {
		this.userId = builder.userId;
		this.timestamp = builder.timestamp;
		this.latitude = builder.latitude;
		this.longitude = builder.longitude;
		this.altitude = builder.altitude;
		this.horizontalAccuracy = builder.horizontalAccuracy;
		this.verticalAccuracy = builder.verticalAccuracy;
	}
}
