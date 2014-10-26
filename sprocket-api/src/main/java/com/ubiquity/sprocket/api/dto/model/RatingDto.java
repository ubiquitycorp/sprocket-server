package com.ubiquity.sprocket.api.dto.model;


public class RatingDto {
	
	private Double min;

	private Double max;

	private Double rating;

	private Integer numRatings;


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

		public RatingDto build() {
			return new RatingDto(this);
		}
	}

	private RatingDto(Builder builder) {
		this.min = builder.min;
		this.max = builder.max;
		this.rating = builder.rating;
		this.numRatings = builder.numRatings;
	}
}
