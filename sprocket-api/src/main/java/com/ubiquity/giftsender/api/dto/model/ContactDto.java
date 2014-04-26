package com.ubiquity.giftsender.api.dto.model;

public class ContactDto {

	private Long contactId;
	private Long ownerId;
	private Boolean active;
	private String displayName;
	private Integer socialProviderType;
	private String firstName;
	private String lastName;
	private String email;
	private String profileUrl;
	private String imageUrl;
	private String etag;
	private String socialProviderIdenitifer;

	public String getEtag() {
		return etag;
	}

	public Boolean getActive() {
		return active;
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

	public String getProfileUrl() {
		return profileUrl;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public Long getContactId() {
		return contactId;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public Integer getSocialProviderType() {
		return socialProviderType;
	}

	public String getSocialProviderIdenitifer() {
		return socialProviderIdenitifer;
	}



	public static class Builder {
		private Long contactId;
		private Long ownerId;
		private Boolean active;
		private String displayName;
		private Integer socialProviderType;
		private String firstName;
		private String lastName;
		private String email;
		private String profileUrl;
		private String imageUrl;
		private String etag;
		private String socialProviderIdenitifer;

		public Builder contactId(Long contactId) {
			this.contactId = contactId;
			return this;
		}

		public Builder ownerId(Long ownerId) {
			this.ownerId = ownerId;
			return this;
		}

		public Builder active(Boolean active) {
			this.active = active;
			return this;
		}

		public Builder displayName(String displayName) {
			this.displayName = displayName;
			return this;
		}

		public Builder socialProviderType(Integer socialProviderType) {
			this.socialProviderType = socialProviderType;
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

		public Builder email(String email) {
			this.email = email;
			return this;
		}

		public Builder profileUrl(String profileUrl) {
			this.profileUrl = profileUrl;
			return this;
		}

		public Builder imageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
			return this;
		}

		public Builder etag(String etag) {
			this.etag = etag;
			return this;
		}
		
		public Builder socialProviderIdenitifer(String socialProviderIdenitifer) {
			this.socialProviderIdenitifer = socialProviderIdenitifer;
			return this;
		}

		public ContactDto build() {
			return new ContactDto(this);
		}
	}

	private ContactDto(Builder builder) {
		this.contactId = builder.contactId;
		this.ownerId = builder.ownerId;
		this.active = builder.active;
		this.displayName = builder.displayName;
		this.socialProviderType = builder.socialProviderType;
		this.firstName = builder.firstName;
		this.lastName = builder.lastName;
		this.email = builder.email;
		this.profileUrl = builder.profileUrl;
		this.imageUrl = builder.imageUrl;
		this.etag = builder.etag;
		this.socialProviderIdenitifer = builder.socialProviderIdenitifer;
	}
}
