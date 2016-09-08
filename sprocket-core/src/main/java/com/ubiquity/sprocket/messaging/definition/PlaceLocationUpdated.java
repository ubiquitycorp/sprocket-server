package com.ubiquity.sprocket.messaging.definition;

import com.ubiquity.location.domain.Geobox;


public class PlaceLocationUpdated {
	private Long placeId;

	private Geobox geobox ;

	public Long getPlaceId() {
		return placeId;
	}

	public Geobox getGeobox() {
		return geobox;
	}

	public static class Builder {
		private Long placeId;
		private Geobox geobox;

		public Builder placeId(Long placeId) {
			this.placeId = placeId;
			return this;
		}

		public Builder geobox(Geobox geobox) {
			this.geobox = geobox;
			return this;
		}

		public PlaceLocationUpdated build() {
			return new PlaceLocationUpdated(this);
		}
	}

	private PlaceLocationUpdated(Builder builder) {
		this.placeId = builder.placeId;
		this.geobox = builder.geobox;
	}
}
