package com.ubiquity.sprocket.network.api.googleplus.model;


public class GoogleEmail{
	private String value;
	private String type;
	
	public String getValue() {
		return value;
	}
	public String getType() {
		return type;
	}
	public static class Builder {
		private String value;
		private String type;

		public Builder type(String type) {
			this.type = type;
			return this;
		}

		public Builder value(String value) {
			this.value = value;
			return this;
		}

		public GoogleEmail build() {
			return new GoogleEmail(this);
		}
	}

	private GoogleEmail(Builder builder) {
		this.value = builder.value;
		this.type = builder.type;
	}
}