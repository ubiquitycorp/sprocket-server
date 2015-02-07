package com.ubiquity.sprocket.network.api.dto.model;

public class Contact {

	private String displayName;

	private String firstName;

	private String lastName;

	private String email;

	private Gender gender;

	private Image image;
	
	private String profileUrl;

	private Long lastUpdated;
	
	
	private ExternalIdentity externalIdentity;

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setLastUpdated(Long lastUpdated) {
		this.lastUpdated = lastUpdated;
	}


	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}
	
	public Long getLastUpdated() {
		return lastUpdated;
	}


	public void setEmail(String email) {
		this.email = email;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public String getProfileUrl() {
		return profileUrl;
	}

	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}

	public Gender getGender() {
		return gender;
	}
	
	public ExternalIdentity getExternalIdentity() {
		return externalIdentity;
	}



	public static class Builder {
		private String displayName;
		private String firstName;
		private String lastName;
		private String email;
		private Gender gender;
		private Image image;
		private String profileUrl;
		private Long lastUpdated;
		private ExternalIdentity externalIdentity;

		public Builder displayName(String displayName) {
			this.displayName = displayName;
			return this;
		}
		
		public Builder firstName(String firstName) {
			this.firstName = firstName;
			return this;
		}
		
		public Builder gender(Gender gender){
			this.gender = gender;
			return this;
		}

		public Builder lastName(String lastName) {
			this.lastName = lastName;
			return this;
		}

		public Builder email(String email) {
			this.email = email;
			return this;
		}

		public Builder image(Image image) {
			this.image = image;
			return this;
		}

		public Builder profileUrl(String profileUrl) {
			this.profileUrl = profileUrl;
			return this;
		}

		public Builder lastUpdated(Long lastUpdated) {
			this.lastUpdated = lastUpdated;
			return this;
		}
		
		public Builder externalIdentity(ExternalIdentity externalIdentity){
			this.externalIdentity= externalIdentity;
			return this;
		}

		public Contact build() {
			return new Contact(this);
		}
	}

	private Contact(Builder builder) {
		this.displayName = builder.displayName;
		this.firstName = builder.firstName;
		this.lastName = builder.lastName;
		this.email = builder.email;
		this.gender = builder.gender;
		this.image = builder.image;
		this.profileUrl = builder.profileUrl;
		this.lastUpdated = builder.lastUpdated;
		this.externalIdentity = builder.externalIdentity;
	}
	
}