package com.ubiquity.sprocket.network.api.dto.model;


public class Address {
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
	protected Address() {
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getCountry() {
		return country;
	}

	public String getDisplayPhone() {
		return displayPhone;
	}

	public void setDisplayPhone(String displayPhone) {
		this.displayPhone = displayPhone;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStateOrRegion() {
		return stateOrRegion;
	}

	public void setStateOrRegion(String stateOrRegion) {
		this.stateOrRegion = stateOrRegion;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	@Override
	public String toString() {
		return "Address [streetName=" + streetName + ", unitName=" + unitName
				+ ", city=" + city + ", stateOrRegion=" + stateOrRegion
				+ ", postalCode=" + postalCode + ", country=" + country + "]";
	}

	/***
	 * Returns a string that will form a unique adddress locator compatible with most geo search engines
	 * 
	 * @return string (i.e., 3535 Griffith Park Blvd
	 */
	public String asLocatorString() {
		StringBuilder builder = new StringBuilder();
		if (streetName != null)
			builder.append(streetName).append(", ");
		if (city != null)
			builder.append(city);
		if (stateOrRegion != null)
			builder.append(", ").append(stateOrRegion);
		if (postalCode != null)
			builder.append(" ").append(postalCode);
		if (country != null)
			builder.append(", ").append(country);

		return builder.toString();
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

		public Builder country(String country) {
			this.country = country;
			return this;
		}

		public Builder displayPhone(String displayPhone) {
			this.displayPhone = displayPhone;
			return this;
		}

		public Address build() {
			return new Address(this);
		}
	}

	private Address(Builder builder) {
		this.streetName = builder.streetName;
		this.unitName = builder.unitName;
		this.city = builder.city;
		this.stateOrRegion = builder.stateOrRegion;
		this.postalCode = builder.postalCode;
		this.country = builder.country;
		this.displayPhone = builder.displayPhone;
	}
}
