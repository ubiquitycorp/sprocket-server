package com.ubiquity.sprocket.network.api.dto.model;

public class ExternalIdentity {
	
	private String identifier;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public static class Builder {
		private String identifier;
		
		public Builder identifier(String identifier) {
			this.identifier = identifier;
			return this;
		}
		public ExternalIdentity build() {
			return new ExternalIdentity(this);
		}
	}

	private ExternalIdentity(Builder builder) {
		this.identifier = builder.identifier;
	}
	
	
}
