package com.ubiquity.sprocket.domain.factory;

import com.ubiquity.identity.domain.User;
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
	
	

}
