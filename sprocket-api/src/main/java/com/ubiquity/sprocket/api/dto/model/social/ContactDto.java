package com.ubiquity.sprocket.api.dto.model.social;

import javax.validation.constraints.NotNull;

import com.ubiquity.sprocket.api.dto.model.user.IdentityDto;
import com.ubiquity.sprocket.api.validation.EngagementValidation;

public class ContactDto {

	private Long contactId;
	private Boolean active;
	private String firstName;
	private String lastName;
	private String email;
	private String profileUrl;
	private String imageUrl;
	private String etag;

	@NotNull(groups = { EngagementValidation.class })
	private String displayName;
	
	@NotNull(groups = { EngagementValidation.class })
	private IdentityDto identity;
	
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

	public String getDisplayName() {
		return displayName;
	}

	public IdentityDto getIdentity() {
		return identity;
	}

	public static class Builder {
		private Long contactId;
		private Boolean active;
		private String displayName;
		private String firstName;
		private String lastName;
		private String email;
		private String profileUrl;
		private String imageUrl;
		private String etag;
		private IdentityDto identity;

		public Builder contactId(Long contactId) {
			this.contactId = contactId;
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

		public Builder identity(IdentityDto identity) {
			this.identity = identity;
			return this;
		}

		public ContactDto build() {
			return new ContactDto(this);
		}
	}

	private ContactDto(Builder builder) {
		this.contactId = builder.contactId;
		this.active = builder.active;
		this.displayName = builder.displayName;
		this.firstName = builder.firstName;
		this.lastName = builder.lastName;
		this.email = builder.email;
		this.profileUrl = builder.profileUrl;
		this.imageUrl = builder.imageUrl;
		this.etag = builder.etag;
		this.identity = builder.identity;
	}
}
