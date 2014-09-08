package com.ubiquity.sprocket.api.dto.model;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;
/***
 * 
 * @author peter.tadros
 *
 */
public class LocationDto {

	@NotNull
	private Long timestamp;
	
	@NotNull
	private BigDecimal latitude;
	
	@NotNull
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
}
