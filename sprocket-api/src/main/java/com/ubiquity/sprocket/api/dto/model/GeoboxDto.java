package com.ubiquity.sprocket.api.dto.model;

public class GeoboxDto {
	private LocationDto center;

	private LocationDto upperRight;

	private LocationDto upperLeft;

	private LocationDto lowerRight;

	private LocationDto lowerLeft;

	protected GeoboxDto() {}
	
	public LocationDto getCenter() {
		return center;
	}

	public LocationDto getUpperRight() {
		return upperRight;
	}

	public LocationDto getUpperLeft() {
		return upperLeft;
	}

	public LocationDto getLowerRight() {
		return lowerRight;
	}

	public LocationDto getLowerLeft() {
		return lowerLeft;
	}
	

	public static class Builder {
		private LocationDto center;
		private LocationDto upperRight;
		private LocationDto upperLeft;
		private LocationDto lowerRight;
		private LocationDto lowerLeft;

		public Builder center(LocationDto center) {
			this.center = center;
			return this;
		}

		public Builder upperRight(LocationDto upperRight) {
			this.upperRight = upperRight;
			return this;
		}

		public Builder upperLeft(LocationDto upperLeft) {
			this.upperLeft = upperLeft;
			return this;
		}

		public Builder lowerRight(LocationDto lowerRight) {
			this.lowerRight = lowerRight;
			return this;
		}

		public Builder lowerLeft(LocationDto lowerLeft) {
			this.lowerLeft = lowerLeft;
			return this;
		}

		public GeoboxDto build() {
			return new GeoboxDto(this);
		}
	}

	private GeoboxDto(Builder builder) {
		this.center = builder.center;
		this.upperRight = builder.upperRight;
		this.upperLeft = builder.upperLeft;
		this.lowerRight = builder.lowerRight;
		this.lowerLeft = builder.lowerLeft;
	}

	@Override
	public String toString() {
		return "Geobox [center=" + center + ", upperRight=" + upperRight
				+ ", upperLeft=" + upperLeft + ", lowerRight=" + lowerRight
				+ ", lowerLeft=" + lowerLeft + "]";
	}
	
	
}
