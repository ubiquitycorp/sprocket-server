package com.ubiquity.sprocket.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import org.apache.commons.lang3.Range;

@Embeddable
public class Geobox {

	@Embedded
	@Column(name = "center", nullable = false)
	private Location center;

	@Embedded
	@Column(name = "upper_right", nullable = true)
	private Location upperRight;

	@Embedded
	@Column(name = "upper_left", nullable = true)
	private Location upperLeft;

	@Embedded
	@Column(name = "lower_right", nullable = true)
	private Location lowerRight;

	@Embedded
	@Column(name = "lower_left", nullable = true)
	private Location lowerLeft;

	protected Geobox() {}
	
	public Location getCenter() {
		return center;
	}

	public Location getUpperRight() {
		return upperRight;
	}

	public Location getUpperLeft() {
		return upperLeft;
	}

	public Location getLowerRight() {
		return lowerRight;
	}

	public Location getLowerLeft() {
		return lowerLeft;
	}
	
	public boolean isWithin(Location location) {
		Range<BigDecimal> latRange = Range.between(lowerLeft.getLatitude(), upperRight.getLatitude());
		Range<BigDecimal> lonRange = Range.between(lowerLeft.getLongitude(), upperRight.getLongitude());
		return latRange.contains(location.getLatitude()) && lonRange.contains(location.getLongitude());
	}

	public static class Builder {
		private Location center;
		private Location upperRight;
		private Location upperLeft;
		private Location lowerRight;
		private Location lowerLeft;

		public Builder center(Location center) {
			this.center = center;
			return this;
		}

		public Builder upperRight(Location upperRight) {
			this.upperRight = upperRight;
			return this;
		}

		public Builder upperLeft(Location upperLeft) {
			this.upperLeft = upperLeft;
			return this;
		}

		public Builder lowerRight(Location lowerRight) {
			this.lowerRight = lowerRight;
			return this;
		}

		public Builder lowerLeft(Location lowerLeft) {
			this.lowerLeft = lowerLeft;
			return this;
		}

		public Geobox build() {
			return new Geobox(this);
		}
	}

	private Geobox(Builder builder) {
		this.center = builder.center;
		this.upperRight = builder.upperRight;
		this.upperLeft = builder.upperLeft;
		this.lowerRight = builder.lowerRight;
		this.lowerLeft = builder.lowerLeft;
	}
}
