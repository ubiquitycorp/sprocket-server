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
import com.ubiquity.sprocket.domain.UserEngagement;

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
		// set the place id because we identify engaged records by it
		business.setPlaceId(Math.abs(new java.util.Random().nextLong()));
		contentRepository = new ContentRepositoryLilyImpl(namespace, LilyRepositoryFactory.createRepository());

		activity = new Content.Builder()
		.activity(TestActivityFactory.createActivityWithMininumRequirements(owner, ExternalNetwork.Facebook))
		.build();
		video = new Content.Builder()
		.videoContent(TestVideoContentFactory.createVideoContentWithMininumRequiredFields(owner, ExternalNetwork.Facebook))
		.build();

		place = new Content.Builder()
		.place(business)
		.build();

		contentRepository.create(activity);
		contentRepository.create(video);
		contentRepository.create(place);
	}

	@Test
	public void testUpdateContentEngagement() {
		activity.getUserEngagement().add(new UserEngagement(TestUserFactory.createUserProxy(), System.currentTimeMillis()));
		activity.getUserEngagement().add(new UserEngagement(TestUserFactory.createUserProxy(), System.currentTimeMillis()));
		activity.getUserEngagement().add(new UserEngagement(TestUserFactory.createUserProxy(), System.currentTimeMillis()));

		contentRepository.update(activity);
		
		// now get back from db and see that they have been serialized
		Content persisted = contentRepository.read(activity.getContentId());
		Assert.assertTrue(persisted.getUserEngagement().size() == 3);
		
		log.info("updated content with user engagment content {}", persisted);
		
		persisted.getUserEngagement().add(new UserEngagement(TestUserFactory.createUserProxy(), System.currentTimeMillis()));
		contentRepository.update(persisted);
		
		persisted = contentRepository.read(activity.getContentId());
		Assert.assertTrue(persisted.getUserEngagement().size() == 4);
	}
	@Test
	public void testCreateContent() {
		// read back in to validate the write completed
		Content persisted = contentRepository.read(activity.getContentId());
		Assert.assertNotNull(persisted.getActivity());
		Assert.assertNotNull(persisted.getActivity().getTitle());
		Assert.assertNotNull(persisted.getActivity().getBody());
		Assert.assertNotNull(persisted.getActivity().getExternalIdentifier());
		Assert.assertNotNull(persisted.getActivity().getExternalNetwork());
		Assert.assertNotNull(persisted.getActivity().getOwner());

		persisted = contentRepository.read(video.getContentId());
		Assert.assertNotNull(persisted.getVideoContent());
		Assert.assertNotNull(persisted.getVideoContent().getTitle());
		Assert.assertNotNull(persisted.getVideoContent().getDescription());
		Assert.assertNotNull(persisted.getVideoContent().getVideo().getItemKey());
		Assert.assertNotNull(persisted.getVideoContent().getExternalNetwork());
		Assert.assertNotNull(persisted.getVideoContent().getOwner());

		persisted = contentRepository.read(place.getContentId());
		Assert.assertNotNull(persisted.getPlace());
		Assert.assertNotNull(persisted.getPlace().getName());
		Assert.assertNotNull(persisted.getPlace().getDescription());
		Assert.assertNotNull(persisted.getPlace().getExternalIdentifier());
		Assert.assertNotNull(persisted.getPlace().getExternalNetwork());


	}
}
