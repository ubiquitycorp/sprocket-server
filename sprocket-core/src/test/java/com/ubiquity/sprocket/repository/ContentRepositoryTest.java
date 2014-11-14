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
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.factory.TestUserFactory;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.factory.TestActivityFactory;
import com.ubiquity.integration.factory.TestPlaceFactory;
import com.ubiquity.integration.factory.TestVideoContentFactory;
import com.ubiquity.location.domain.Place;
import com.ubiquity.sprocket.domain.Content;

public class ContentRepositoryTest {
	
	private ContentRepository contentRepository;
	private Content activity, place, video;
	
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
		User owner = TestUserFactory.createUserProxy();
		
		Place business = TestPlaceFactory.createLosAngelesAndNeighborhoodsAndBusiness()
				.getChildren().iterator().next().getChildren().iterator().next();
		contentRepository = new ContentRepositoryLilyImpl(namespace, LilyRepositoryFactory.createRepository());
		
		activity = new Content.Builder()
			.activity(TestActivityFactory.createActivityWithMininumRequirements(null, ExternalNetwork.Facebook))
			.build();
		video = new Content.Builder()
			.video(TestVideoContentFactory.createVideoContentWithMininumRequiredFields(owner, ExternalNetwork.Facebook))
			.build();

		place = new Content.Builder()
			.place(business)
			.build();
		
		contentRepository.create(activity);
		contentRepository.create(video);
		contentRepository.create(place);
	}
	
	@Test
	public void testCreateProfile() {
		
		//log.info("reading record using id: {}", profile.getProfileId());
		
//		// read back in to validate the write completed
		Content persisted = contentRepository.read(activity.getContentId());
		Assert.assertNotNull(persisted.getActivity());
		Assert.assertNotNull(persisted.getActivity().getTitle());
		Assert.assertNotNull(persisted.getActivity().getBody());
		Assert.assertNotNull(persisted.getActivity().getExternalIdentifier());
		Assert.assertNotNull(persisted.getActivity().getExternalNetwork());
		Assert.assertNotNull(persisted.getActivity().getOwner());



//		Assert.assertNotNull(persisted.getProfileId());
//		Assert.assertNotNull(persisted.getGender());
//		Assert.assertNotNull(persisted.getAgeRange());
//		Assert.assertEquals(persisted.getGender(), profile.getGender());
//		Assert.assertTrue(persisted.getSearchHistory().size() == 3);
	}
	
//	@Test
//	public void testUpdateProfile() {
//		Profile persisted = profileRepository.read(profile.getProfileId());
//		persisted.setGender(Gender.Female); // change to female from unspecified
//		profileRepository.update(persisted);
//		
//		// read back to see that changes took place
//		persisted = profileRepository.read(profile.getProfileId());
//		Assert.assertEquals(Gender.Female, persisted.getGender());
//	}

}
