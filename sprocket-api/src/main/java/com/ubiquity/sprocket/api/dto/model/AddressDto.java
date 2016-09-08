package com.ubiquity.sprocket.api.dto.model;


public class AddressDto {
	private String streetName;

	private String unitName;

	private String city;

	private String stateOrRegion;

	private String postalCode;

	private String country;
	
	private String displayPhone;

	/***
	 * Required by JPA
	 */
	protected AddressDto() {
	}

	public String getUnitName() {
		return unitName;
	}


	public String getCountry() {
		return country;
	}


	public String getStreetName() {
		return streetName;
	}


	public String getCity() {
		return city;
	}


	public String getStateOrRegion() {
		return stateOrRegion;
	}


	public String getPostalCode() {
		return postalCode;
	}

	public String getDisplayPhone() {
		return displayPhone;
	}
	public static class Builder {
		private String streetName;
		private String unitName;
		private String city;
		private String stateOrRegion;
		private String postalCode;
		private String country;
		private String displayPhone;

		public Builder streetName(String streetName) {
			this.streetName = streetName;
			return this;
		}

		public Builder unitName(String unitName) {
			this.unitName = unitName;
			return this;
		}

		public Builder city(String city) {
			this.city = city;
			return this;
		}

		public Builder stateOrRegion(String stateOrRegion) {
			this.stateOrRegion = stateOrRegion;
			return this;
		}

		public Builder postalCode(String postalCode) {
			this.postalCode = postalCode;
			return this;
		}
		
		public Builder displayPhone(String displayPhone) {
			this.displayPhone = displayPhone;
			return this;
		}

		public Builder country(String country) {
			this.country = country;
			return this;
		}

		public AddressDto build() {
			return new AddressDto(this);
		}
	}

	private AddressDto(Builder builder) {
		this.streetName = builder.streetName;
		this.unitName = builder.unitName;
		this.city = builder.city;
		this.stateOrRegion = builder.stateOrRegion;
		this.postalCode = builder.postalCode;
		this.country = builder.country;
		this.displayPhone = builder.displayPhone;
	}
}
