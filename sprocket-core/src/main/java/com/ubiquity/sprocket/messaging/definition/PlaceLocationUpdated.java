package com.ubiquity.sprocket.messaging.definition;

import java.math.BigDecimal;

public class PlaceLocationUpdated {
	private Long placeId;

	private BigDecimal latitude;

	private BigDecimal longitude;

	private BigDecimal altitude;

	
	public Long getPlaceId() {
		return placeId;
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

	public static class Builder {
		private Long placeId;
		private BigDecimal latitude;
		private BigDecimal longitude;
		private BigDecimal altitude;

		public Builder placeId(Long placeId) {
			this.placeId = placeId;
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

		public PlaceLocationUpdated build() {
			return new PlaceLocationUpdated(this);
		}
	}

	private PlaceLocationUpdated(Builder builder) {
		this.placeId = builder.placeId;
		this.latitude = builder.latitude;
		this.longitude = builder.longitude;
		this.altitude = builder.altitude;
	}
}
