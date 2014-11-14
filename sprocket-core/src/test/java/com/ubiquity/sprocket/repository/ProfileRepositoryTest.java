package com.ubiquity.sprocket.repository;

import org.junit.Assert;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.lily.LilyRepositoryFactory;
import com.ubiquity.integration.domain.AgeRange;
import com.ubiquity.integration.domain.Gender;
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
		profile = new Profile.Builder()
			.gender(Gender.NotSpecified)
			.ageRage(new AgeRange(22, 28))
			.build();
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
	}
	
	@Test
	public void testUpdateProfile() {
		Profile persisted = profileRepository.read(profile.getProfileId());
		persisted.setGender(Gender.Female); // change to female from unspecified
		profileRepository.update(persisted);
		
		// read back to see that changes took place
		persisted = profileRepository.read(profile.getProfileId());
		Assert.assertEquals(Gender.Female, persisted.getGender());
	}

}
