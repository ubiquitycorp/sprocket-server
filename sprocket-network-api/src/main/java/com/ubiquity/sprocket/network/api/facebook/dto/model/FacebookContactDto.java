package com.ubiquity.sprocket.network.api.facebook.dto.model;

import com.google.gson.annotations.SerializedName;

public class FacebookContactDto {

	private String id;

	private String name;

	private String gender;

	@SerializedName("age_range")
	private FacebookAgeRangeDto ageRange;

	private FacebookPageDto location;

	@SerializedName("first_name")
	private String firstName;

	private String birthday;

	@SerializedName("last_name")
	private String lastName;

	private String email;

	private String link;

	public String getId() {
		return id;
	}

	public FacebookPageDto getLocation() {
		return location;
	}

	public String getBirthday() {
		return birthday;
	}

	public String getGender() {
		return gender;
	}

	public FacebookAgeRangeDto getAgeRange() {
		return ageRange;
	}

	public String getName() {
		return name;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public String getLink() {
		return link;
	}

	public static class Builder {
		private String id;
		private String name;
		private String gender;
		private String firstName;
		private String lastName;
		private String email;
		private String link;
		private FacebookAgeRangeDto ageRange;

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder displayName(String name) {
			this.name = name;
			return this;
		}

		public Builder firstName(String firstName) {
			this.firstName = firstName;
			return this;
		}

		public Builder lastName(String lastName) {
			this.lastName = lastName;
			return this;
		}

		public Builder gender(String gender) {
			this.gender = gender;
			return this;
		}

		public Builder email(String email) {
			this.email = email;
			return this;
		}

		public Builder link(String link) {
			this.link = link;
			return this;
		}

		public Builder ageRange(FacebookAgeRangeDto ageRange) {
			this.ageRange = ageRange;
			return this;
		}

		public FacebookContactDto build() {
			return new FacebookContactDto(this);
		}
	}

	private FacebookContactDto(Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.firstName = builder.firstName;
		this.lastName = builder.lastName;
		this.email = builder.email;
		this.gender = builder.gender;
		this.link = builder.link;
		this.ageRange = builder.ageRange;
	}

}
