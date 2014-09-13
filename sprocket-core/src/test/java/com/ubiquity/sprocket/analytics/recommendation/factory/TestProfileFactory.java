package com.ubiquity.sprocket.analytics.recommendation.factory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.factory.TestUserFactory;
import com.ubiquity.integration.factory.TestContactFactory;
import com.ubiquity.location.domain.Location;
import com.ubiquity.location.domain.UserLocation;
import com.ubiquity.social.domain.AgeRange;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.Gender;
import com.ubiquity.sprocket.analytics.recommendation.Profile;

public class TestProfileFactory {
	
	/***
	 * Creates a profile
	 * 
	 * @param network
	 * @param gender
	 * @param ageRange
	 * @return
	 */
	public static Profile createProfile(ExternalNetwork network, Gender gender, AgeRange ageRange, Double lat, Double lon) {
		User user = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		
		Contact.Builder contactBuilder = TestContactFactory.createContactBuilderWithMininumRequiredFieldsAndExternalNetwork(user, network);
		contactBuilder.gender(gender);
		contactBuilder.ageRange(ageRange);
		
		// build the profile with location
		Profile profile = new Profile(user, new UserLocation.Builder().location(
				new Location.Builder().latitude(new BigDecimal(lat)).longitude(new BigDecimal(lon)).build()).user(user).build());
		profile.getContacts().add(contactBuilder.build());
		
		return profile;
	}
	/***
	 * Returns a map of profiles that can be retrieved by "name"; these profiles are fit for various unit tests testing the
	 * recommendation engine logic (or operations that utilize that logic)
	 * 
	 * @return
	 */
	public static Map<String, Profile> createProfilesForRecommendationTest() {
		
		Map<String, Profile> profileMap = new HashMap<String, Profile>();
		
		User user = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		Contact.Builder contactBuilder = TestContactFactory.createContactBuilderWithMininumRequiredFieldsAndExternalNetwork(user, ExternalNetwork.Facebook);
		contactBuilder.gender(Gender.Male);
		contactBuilder.ageRange(new AgeRange(21, 35));
		
		
		
		// build the profile with location
		Profile profile = new Profile(user, new UserLocation.Builder().location(
				new Location.Builder().latitude(new BigDecimal(34.0522300)).longitude(new BigDecimal(-118.2436800)).build()).user(user).build());
		profile.getContacts().add(contactBuilder.build());
		profileMap.put("jack", profile);

		user = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		
		
		contactBuilder = TestContactFactory.createContactBuilderWithMininumRequiredFieldsAndExternalNetwork(user, ExternalNetwork.Facebook);
		contactBuilder.gender(Gender.Male);
		contactBuilder.ageRange(new AgeRange(21, 35));
		
		
		// build the profile with location
		profile = new Profile(user, new UserLocation.Builder().location(
				new Location.Builder().latitude(new BigDecimal(34.1234567)).longitude(new BigDecimal(-118.2412345)).build()).user(user).build());
		profile.getContacts().add(contactBuilder.build());
		profileMap.put("john", profile);

		user = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		
		contactBuilder = TestContactFactory.createContactBuilderWithMininumRequiredFieldsAndExternalNetwork(user, ExternalNetwork.Facebook);
		contactBuilder.gender(Gender.Male);
		contactBuilder.ageRange(new AgeRange(55, 65));

		// build the profile with location
		profile = new Profile(user, new UserLocation.Builder().location(
				new Location.Builder().latitude(new BigDecimal(34.1334567)).longitude(new BigDecimal(-118.2499999)).build()).user(user).build());
		profile.getContacts().add(contactBuilder.build());
		profileMap.put("joe", profile);


		user = TestUserFactory.createTestUserWithMinimumRequiredProperties();
	
		contactBuilder = TestContactFactory.createContactBuilderWithMininumRequiredFieldsAndExternalNetwork(user, ExternalNetwork.Facebook);
		contactBuilder.gender(Gender.Female);
		contactBuilder.ageRange(new AgeRange(55, 65));
		
		
		profile = new Profile(user, new UserLocation.Builder().location(
				new Location.Builder().latitude(new BigDecimal(-34.6131500)).longitude(new BigDecimal(-58.3772300)).build()).user(user).build());
		profile.getContacts().add(contactBuilder.build());
		profileMap.put("jill", profile);

		user = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		
		contactBuilder = TestContactFactory.createContactBuilderWithMininumRequiredFieldsAndExternalNetwork(user, ExternalNetwork.Facebook);
		contactBuilder.gender(Gender.Female);
		contactBuilder.ageRange(new AgeRange(55, 65));
		
		
		profile = new Profile(user, new UserLocation.Builder().location(
				new Location.Builder().latitude(new BigDecimal(-34.6231500)).longitude(new BigDecimal(-58.3872300)).build()).user(user).build());
		profile.getContacts().add(contactBuilder.build());
		profileMap.put("jane", profile);
		
		return profileMap;
		
	}

}
