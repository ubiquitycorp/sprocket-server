package com.ubiquity.sprocket.network.api.dto.model;


public class Rating {

	private Double min;

	private Double max;

	private Double rating;
	
	private Integer numRatings;

	/***
	 * Required by JPA
	 */
	protected Rating() {}

	public Double getMin() {
		return min;
	}

	public Double getMax() {
		return max;
	}

	public Double getRating() {
		return rating;
	}

	public Integer getNumRatings() {
		return numRatings;
	}

	public static class Builder {
		private Double min;
		private Double max;
		private Double rating;
		private Integer numRatings;

		public Builder min(Double min) {
			this.min = min;
			return this;
		}

		public Builder max(Double max) {
			this.max = max;
			return this;
		}

		public Builder rating(Double rating) {
			this.rating = rating;
			return this;
		}

		public Builder numRatings(Integer numRatings) {
			this.numRatings = numRatings;
			return this;
		}

		public Rating build() {
			return new Rating(this);
		}
	}

	private Rating(Builder builder) {
		this.min = builder.min;
		this.max = builder.max;
		this.rating = builder.rating;
		this.numRatings = builder.numRatings;
	}
}
