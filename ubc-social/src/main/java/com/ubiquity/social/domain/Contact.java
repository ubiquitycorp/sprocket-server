package com.ubiquity.social.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.ubiquity.identity.domain.User;
import com.ubiquity.media.domain.Image;

@Entity
@Table(name = "contact")
public class Contact {

	@Id
	@GeneratedValue
	@Column(name = "contact_id")
	private Long contactId;

	@Column(name = "display_name", length = 100)
	private String displayName;

	@Column(name = "first_name", length = 100)
	private String firstName;

	@Column(name = "last_name", length = 100)
	private String lastName;

	@Column(name = "email", length = 100)
	private String email;

	@Embedded
	private Image image;

	@Lob
	@Column(name = "profile_url")
	private String profileUrl;

	@Column(name = "last_udpated", nullable = false)
	private Long lastUpdated;

	@ManyToOne
	@JoinColumn(name = "owner_id", nullable = false)
	private User owner;

	@OneToOne(cascade = CascadeType.ALL)
	private SocialIdentity socialIdentity;

	
	/***
	 * Default constructor required by JPA
	 */
	public Contact() {
	}

	
	public Long getContactId() {
		return contactId;
	}

	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Long getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Long lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
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

	public SocialIdentity getSocialIdentity() {
		return socialIdentity;
	}

	public static class Builder {
		private Long contactId;
		private String displayName;
		private String firstName;
		private String lastName;
		private String email;
		private Image image;
		private String profileUrl;
		private Long lastUpdated;
		private User owner;
		private SocialIdentity socialIdentity;

		public Builder contactId(Long contactId) {
			this.contactId = contactId;
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

		public Builder owner(User owner) {
			this.owner = owner;
			return this;
		}

		public Builder socialIdentity(SocialIdentity socialIdentity) {
			this.socialIdentity = socialIdentity;
			return this;
		}

		public Contact build() {
			return new Contact(this);
		}
	}

	private Contact(Builder builder) {
		this.contactId = builder.contactId;
		this.displayName = builder.displayName;
		this.firstName = builder.firstName;
		this.lastName = builder.lastName;
		this.email = builder.email;
		this.image = builder.image;
		this.profileUrl = builder.profileUrl;
		this.lastUpdated = builder.lastUpdated;
		this.owner = builder.owner;
		this.socialIdentity = builder.socialIdentity;
	}
}