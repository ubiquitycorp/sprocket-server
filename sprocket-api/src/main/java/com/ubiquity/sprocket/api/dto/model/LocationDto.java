package com.ubiquity.sprocket.api.dto.model;

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
	private Double latitude;
	@NotNull
	private Double longitude;
	
	private Double altitude;
	
	private Double verticalAccuracy;
	
	private Double horizontalAccuracy;

	public Long getTimestamp() {
		return timestamp;
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

	public Double getVerticalAccuracy() {
		return verticalAccuracy;
	}

	public Double getHorizontalAccuracy() {
		return horizontalAccuracy;
	}
}
