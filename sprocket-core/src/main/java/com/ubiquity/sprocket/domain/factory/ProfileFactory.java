package com.ubiquity.sprocket.domain.factory;

import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.integration.domain.Contact;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.sprocket.domain.Profile;
import com.ubiquity.sprocket.domain.ProfilePK;

public class ProfileFactory {


	/***
	 * Creates a profile from a user.
	 * 
	 * @param user
	 * @return
	 */
	public static Profile createProfile(User user) {
		Profile profile = new Profile.Builder()
		.profileId(new ProfilePK(user.getUserId()))
		.userId(user.getUserId()).build();
		return profile;
	}

	/***
	 * Creates an identity for this profile
	 * 
	 * @param identity
	 * @return
	 */
	public static Profile createProfile(Long userId, Contact contact) {

		ExternalIdentity identity = contact.getExternalIdentity();
		ExternalNetwork network = ExternalNetwork.getNetworkById(identity.getExternalNetwork());
		return new Profile.Builder()
			.profileId(new ProfilePK(network, userId))
			.gender(contact.getGender())
			.userId(userId)
			.externalIdentifier(identity.getIdentifier())
			.externalNetwork(network)
			.ageRange(contact.getAgeRange()).build();
	}



}
