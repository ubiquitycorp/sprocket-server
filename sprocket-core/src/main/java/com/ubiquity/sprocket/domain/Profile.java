package com.ubiquity.sprocket.domain;

import java.util.LinkedList;
import java.util.List;

import com.ubiquity.identity.domain.User;
import com.ubiquity.integration.domain.AgeRange;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.Gender;
import com.ubiquity.integration.domain.Interest;
import com.ubiquity.location.domain.UserLocation;

/***
 * 
 * Domain entity encapsulating the known and derived attributes of a single user and their interests
 * 
 * @author chris
 *
 */
public class Profile {

	private String profileId;
	private User user;
	private Gender gender;
	private AgeRange ageRange;
	private List<Interest> interests = new LinkedList<Interest>();
	private List<String> searchHistory = new LinkedList<String>();
	private List<Profile> identities = new LinkedList<Profile>();
	private UserLocation location;
	private ExternalNetwork externalNetwork;
	private String externalIdentifier;

	public Profile() {
	}

	public Profile getIdentityForExternalNetwork(ExternalNetwork network) {
		for (Profile identity : identities) {
			if (identity.getExternalNetwork() == network)
				return identity;
		}
		return null;
	}

	public List<Profile> getIdentities() {
		return identities;
	}

	public ExternalNetwork getExternalNetwork() {
		return externalNetwork;
	}

	public String getExternalIdentifier() {
		return externalIdentifier;
	}

	public UserLocation getLocation() {
		return location;
	}

	/**
	 * Setter needed to return the record id after create
	 * 
	 * @param profileId
	 */
	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public void setAgeRage(AgeRange ageRange) {
		this.ageRange = ageRange;
	}

	public String getProfileId() {
		return profileId;
	}

	public User getUser() {
		return user;
	}

	public Gender getGender() {
		return gender;
	}

	public AgeRange getAgeRange() {
		return ageRange;
	}

	public List<String> getSearchHistory() {
		return searchHistory;
	}

	public List<Interest> getInterests() {
		return interests;
	}

	public static class Builder {
		private String profileId;
		private User user;
		private Gender gender;
		private AgeRange ageRange;
		private UserLocation location;
		private ExternalNetwork externalNetwork;
		private String externalIdentifier;

		public Builder profileId(String profileId) {
			this.profileId = profileId;
			return this;
		}

		public Builder user(User user) {
			this.user = user;
			return this;
		}

		public Builder gender(Gender gender) {
			this.gender = gender;
			return this;
		}

		public Builder ageRange(AgeRange ageRange) {
			this.ageRange = ageRange;
			return this;
		}

		public Builder location(UserLocation location) {
			this.location = location;
			return this;
		}

		public Builder externalNetwork(ExternalNetwork externalNetwork) {
			this.externalNetwork = externalNetwork;
			return this;
		}

		public Builder externalIdentifier(String externalIdentifier) {
			this.externalIdentifier = externalIdentifier;
			return this;
		}

		public Profile build() {
			return new Profile(this);
		}
	}

	private Profile(Builder builder) {
		this.profileId = builder.profileId;
		this.user = builder.user;
		this.gender = builder.gender;
		this.ageRange = builder.ageRange;
		this.location = builder.location;
		this.externalNetwork = builder.externalNetwork;
		this.externalIdentifier = builder.externalIdentifier;
	}
}
