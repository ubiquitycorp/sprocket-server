package com.ubiquity.sprocket.repository;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.lily.LilyRepositoryFactory;
import com.ubiquity.identity.factory.TestUserFactory;
import com.ubiquity.integration.domain.AgeRange;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.Gender;
import com.ubiquity.sprocket.analytics.recommendation.factory.TestProfileFactory;
import com.ubiquity.sprocket.domain.Profile;

public class ProfileRepositoryTest {
	
	private ProfileRepository profileRepository;
	private Profile profile;
	private Logger log = LoggerFactory.getLogger(getClass());
	private static String namespace;
	
	@BeforeClass
	public static void setUpServices() throws ConfigurationException {
		Configuration config = new PropertiesConfiguration("test.properties");
		namespace = config.getString("hbase.sprocket.namespace");
		LilyRepositoryFactory.initialize(config);
	}
	
	@Before
	public void setUp() {
		profileRepository = new ProfileRepositoryLilyImpl(namespace, LilyRepositoryFactory.createRepository());
		profile = TestProfileFactory.createProfileAndIdentity(TestUserFactory.createUserProxy(),
					ExternalNetwork.Facebook, Gender.Female, new AgeRange(24, 35), 34.2323232, -118.0822322);
		profile.getSearchHistory().add("San Jose Sharks");
		profile.getSearchHistory().add("Sharks");
		profile.getSearchHistory().add("Bars San Jose");
	

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
		Assert.assertTrue(persisted.getSearchHistory().size() == 3);
		
		Assert.assertTrue(persisted.getIdentities().size() == 1);
		Profile identity = profile.getIdentities().get(0);
		Assert.assertNotNull(identity.getExternalIdentifier());
		Assert.assertEquals(identity.getExternalNetwork(), ExternalNetwork.Facebook);

	}
	
	@Test
	public void testUpdateProfile() {
//		Profile persisted = profileRepository.read(profile.getProfileId());
//		persisted.setGender(Gender.Female); // change to female from unspecified
//		profileRepository.update(persisted);
//		
//		// read back to see that changes took place
//		persisted = profileRepository.read(profile.getProfileId());
//		Assert.assertEquals(Gender.Female, persisted.getGender());
	}

}
