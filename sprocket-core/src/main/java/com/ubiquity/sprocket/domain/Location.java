package com.ubiquity.sprocket.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Location {

	@Column(name = "latitude", nullable = false)
	private BigDecimal latitude;

	@Column(name = "longitude", nullable = false)
	private BigDecimal longitude;

	private BigDecimal altitude;

	/**
	 * Required by JPA
	 */
	protected Location() {}
	
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
		private BigDecimal latitude;
		private BigDecimal longitude;
		private BigDecimal altitude;

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

		public Location build() {
			return new Location(this);
		}
	}

	private Location(Builder builder) {
		this.latitude = builder.latitude;
		this.longitude = builder.longitude;
		this.altitude = builder.altitude;
	}
}
