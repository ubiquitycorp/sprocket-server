package com.ubiquity.sprocket.api.dto.model;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import com.ubiquity.sprocket.api.validation.PlaceLocationUpdateValidation;
import com.ubiquity.sprocket.api.validation.UserLocationUpdateValidation;
/***
 * 
 * @author peter.tadros
 *
 */
public class LocationDto {

	@NotNull(groups = { UserLocationUpdateValidation.class})
	private Long timestamp;
	
	@NotNull(groups = { UserLocationUpdateValidation.class,PlaceLocationUpdateValidation.class})
	private BigDecimal latitude;
	
	@NotNull(groups = { UserLocationUpdateValidation.class,PlaceLocationUpdateValidation.class})
	private BigDecimal longitude;
	
	private BigDecimal altitude;
	
	private Double verticalAccuracy;
	
	private Double horizontalAccuracy;

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

	public Double getVerticalAccuracy() {
		return verticalAccuracy;
	}

	public Double getHorizontalAccuracy() {
		return horizontalAccuracy;
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

		public LocationDto build() {
			return new LocationDto(this);
		}
	}
	private LocationDto(Builder builder) {
		this.latitude = builder.latitude;
		this.longitude = builder.longitude;
		this.altitude = builder.altitude;
	}

}
