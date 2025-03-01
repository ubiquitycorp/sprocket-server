package com.ubiquity.sprocket.analytics.recommendation.factory;

import java.math.BigDecimal;
import java.util.UUID;

import com.ubiquity.integration.domain.AgeRange;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.Gender;
import com.ubiquity.location.domain.Location;
import com.ubiquity.location.domain.UserLocation;
import com.ubiquity.sprocket.domain.Profile;
import com.ubiquity.sprocket.domain.ProfilePK;

public class TestProfileFactory {

	/***
	 * Creates a profile with a specified user and populates some of the profile fields based on this contact info
	 * 
	 * @param user
	 * @param network
	 * @param gender
	 * @param ageRange
	 * @param lat
	 * @param lon
	 * @return
	 */
	public static Profile createProfileAndIdentity(Long userId, ExternalNetwork network, Gender gender, AgeRange ageRange, Double lat, Double lon) {		
		
		
		Profile profile = new Profile.Builder()
		.profileId(new ProfilePK(userId))
		.gender(gender)
		.userId(userId)
		.ageRange(ageRange)
		.location(new UserLocation.Builder().location(
				new Location.Builder().latitude(new BigDecimal(lat)).longitude(new BigDecimal(lon)).build()).user(null).build()).build();

		// build the profile with location
		Profile identity = new Profile.Builder()
		.profileId(new ProfilePK(network, userId))
		.gender(gender)
		.userId(userId)
		.externalIdentifier(UUID.randomUUID().toString())
		.externalNetwork(network)
		.ageRange(ageRange)
		.location(new UserLocation.Builder().location(
				new Location.Builder().latitude(new BigDecimal(lat)).longitude(new BigDecimal(lon)).build()).user(null).build()).build();
		// add to list of contacts
		profile.getIdentities().add(identity);

		return profile;
	}

}
