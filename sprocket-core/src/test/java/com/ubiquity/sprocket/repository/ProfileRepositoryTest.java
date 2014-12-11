package com.ubiquity.sprocket.repository;

import java.util.List;
import java.util.UUID;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.integration.domain.AgeRange;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.Gender;
import com.ubiquity.sprocket.analytics.recommendation.factory.TestProfileFactory;
import com.ubiquity.sprocket.domain.Profile;

public class ProfileRepositoryTest {

	private ProfileRepository profileRepository;
	private Profile profile;

	private Logger log = LoggerFactory.getLogger(getClass());


	@BeforeClass
	public static void setUpServices() throws ConfigurationException {
		Configuration config = new PropertiesConfiguration("test.properties");
		HBaseTableConnectionFactory.initialize(config);
	}

	@Before
	public void setUp() {
		profileRepository = new ProfileRepositoryHBaseImpl();

		profile = TestProfileFactory
				.createProfileAndIdentity(Math.abs(new java.util.Random().nextLong()),
						ExternalNetwork.Facebook, Gender.Female, new AgeRange(24, 35), 34.2323232, -118.0822322);

		profileRepository.create(profile);
	}

	
	@Test
	public void testCreateProfile() {

		log.info("reading record using id: {}", profile.getProfileId());
		
		

		// read back in to validate the write completed
		Profile persisted = profileRepository.read(profile.getProfileId());
		Assert.assertNotNull(persisted.getProfileId());
		Assert.assertNotNull(persisted.getGender());
		Assert.assertNotNull(persisted.getAgeRange());
		Assert.assertEquals(persisted.getGender(), profile.getGender());

	}

	@Test
	public void testUpdateProfile() {

		// check gender, because we've learned more about this profile
		Profile persisted = profileRepository.read(profile.getProfileId());
		persisted.setGender(Gender.Female); // change to female from unspecified
		// also add membership
		persisted.setGroupMembership(UUID.randomUUID().toString());
		profileRepository.update(persisted);

		// read back to see that changes took place
		persisted = profileRepository.read(profile.getProfileId());
		Assert.assertEquals(Gender.Female, persisted.getGender());
		Assert.assertNotNull(persisted.getGroupMembership());
	}
	
	@Test
	public void testAddSearchTerm() {
		String one = UUID.randomUUID().toString();
		String two = UUID.randomUUID().toString();
		String three = UUID.randomUUID().toString();

		
		profileRepository.addToSearchHistory(profile.getProfileId(), two);
		profileRepository.addToSearchHistory(profile.getProfileId(), three);
		
		profileRepository.addToSearchHistory(profile.getProfileId(), UUID.randomUUID().toString());
		profileRepository.addToSearchHistory(profile.getProfileId(), UUID.randomUUID().toString());
		profileRepository.addToSearchHistory(profile.getProfileId(), UUID.randomUUID().toString());
		profileRepository.addToSearchHistory(profile.getProfileId(), UUID.randomUUID().toString());
		profileRepository.addToSearchHistory(profile.getProfileId(), UUID.randomUUID().toString());
		profileRepository.addToSearchHistory(profile.getProfileId(), UUID.randomUUID().toString());
		profileRepository.addToSearchHistory(profile.getProfileId(), UUID.randomUUID().toString());
		
		profileRepository.addToSearchHistory(profile.getProfileId(), one);
		profileRepository.addToSearchHistory(profile.getProfileId(), one);

		
		// assert most searched on returns 
		List<String> results = profileRepository.findMostSearchedOn(profile.getProfileId(), 5);
		Assert.assertEquals(5, results.size());
		
		// make sure "one", which was added last, is still the first one out and the most popular
		Assert.assertTrue(results.get(0).equals(one));
		
		
	}

}
