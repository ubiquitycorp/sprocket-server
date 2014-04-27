package com.ubiquity.identity.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.ubiquity.media.domain.Image;

/***
 * Entity encapsulating a user in the giftsender system
 * 
 * @author chris
 *
 */
@Entity
@Table(name = "user")
public class User {

	@Id
	@GeneratedValue
	@Column(name = "user_id")
	private Long userId;

	@Column(name = "first_name", length = 100, nullable = true)
	private String firstName;

	@Column(name = "last_name", length = 100, nullable = true)
	private String lastName;
	
	@Column(name = "display_name", length = 100, nullable = false)
	private String displayName;

	@Column(name = "email", length = 100, nullable = true)
	private String email;
	
	@Column(name = "last_udpated", nullable = false)
	private Long lastUpdated;
	
	@Embedded
	private Image image;
	
	@OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = { CascadeType.ALL }, orphanRemoval = true)
	private Set<Identity> identities = new HashSet<Identity>();

	@Column(name = "client_platform", nullable = false)
	private ClientPlatform clientPlatform;
	/***
	 * Default constructor required by JPA
	 */
	protected User() {
	}

	public Set<Identity> getIdentities() {
		return identities;
	}

	public Long getUserId() {
		return userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getEmail() {
		return email;
	}

	public Long getLastUpdated() {
		return lastUpdated;
	}
	

	public void setLastUpdated(Long lastUpdated) {
		this.lastUpdated = lastUpdated;
	}


//	public Set<PaymentMethod> getPaymentMethods() {
//		return paymentMethods;
//	}

	public ClientPlatform getClientPlatform() {
		return clientPlatform;
	}

	public void setClientPlatform(ClientPlatform clientPlatform) {
		this.clientPlatform = clientPlatform;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public static class Builder {
		private Long userId;
		private String firstName;
		private String lastName;
		private String displayName;
		private String email;
		private Long lastUpdated;
		private ClientPlatform clientPlatform;
		private Image image;
		
		public Builder userId(Long userId) {
			this.userId = userId;
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
		
		public Builder displayName(String displayName) {
			this.displayName = displayName;
			return this;
		}
		
		public Builder email(String email) {
			this.email = email;
			return this;
		}

		public Builder lastUpdated(Long lastUpdated) {
			this.lastUpdated = lastUpdated;
			return this;
		}

		public Builder clientPlatform(ClientPlatform clientPlatform) {
			this.clientPlatform = clientPlatform;
			return this;
		}
		
		public Builder image(Image image) {
			this.image = image;
			return this;
		}

		public User build() {
			return new User(this);
		}
	}

	private User(Builder builder) {
		this.userId = builder.userId;
		this.firstName = builder.firstName;
		this.lastName = builder.lastName;
		this.displayName = builder.displayName;
		this.email = builder.email;
		this.lastUpdated = builder.lastUpdated;
		this.clientPlatform = builder.clientPlatform;
		this.image = builder.image;
	}
}
