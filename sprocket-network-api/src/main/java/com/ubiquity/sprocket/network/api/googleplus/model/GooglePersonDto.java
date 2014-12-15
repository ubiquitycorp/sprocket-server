package com.ubiquity.sprocket.network.api.googleplus.model;

import com.ubiquity.sprocket.network.api.dto.model.Image;


public class GooglePersonDto {

	private String displayName;
	
	private String firstName;
    
	private String lastName;
	
	private GoogleEmail[] emails;

	private Image image;
    
	private String url;

	private String id;

	public String getDisplayName() {
		return displayName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public GoogleEmail[] getEmail() {
		return emails;
	}

	public Image getImage() {
		return image;
	}

	public String getUrl() {
		return url;
	}

	public String getId() {
		return id;
	}
	
	public static class Builder {
		private String displayName;
		
		private String firstName;
	    
		private String lastName;

		private Image image;
	    
		private String url;

		private String id;

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder displayName(String displayName) {
			this.displayName = displayName;
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

		public Builder image(Image image) {
			this.image = image;
			return this;
		}

		public Builder url(String url) {
			this.url = url;
			return this;
		}

		public GooglePersonDto build() {
			return new GooglePersonDto(this);
		}
	}

	private GooglePersonDto(Builder builder) {
		this.id = builder.id;
		this.displayName = builder.displayName;
		this.firstName = builder.firstName;
		this.lastName = builder.lastName;
		this.image = builder.image;
		this.url = builder.url;
		this.emails = new GoogleEmail[1];

	}

    
}
