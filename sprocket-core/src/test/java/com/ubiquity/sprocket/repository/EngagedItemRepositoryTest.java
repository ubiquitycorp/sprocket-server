package com.ubiquity.sprocket.repository;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.content.domain.VideoContent;
import com.ubiquity.content.repository.VideoContentRepository;
import com.ubiquity.content.repository.VideoContentRepositoryJpaImpl;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.factory.TestUserFactory;
import com.ubiquity.identity.repository.UserRepository;
import com.ubiquity.identity.repository.UserRepositoryJpaImpl;
import com.ubiquity.integration.factory.TestActivityFactory;
import com.ubiquity.integration.factory.TestVideoContentFactory;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.repository.ActivityRepository;
import com.ubiquity.social.repository.ActivityRepositoryJpaImpl;
import com.ubiquity.sprocket.domain.EngagedActivity;
import com.ubiquity.sprocket.domain.EngagedDocument;
import com.ubiquity.sprocket.domain.EngagedItem;
import com.ubiquity.sprocket.domain.EngagedVideo;

/***
 * Tests testing basic CRUD operations for an engaged item repository
 * 
 * @author chris
 *
 */
public class EngagedItemRepositoryTest {

	private Logger log = LoggerFactory.getLogger(getClass());

	private EngagedItemRepository engagedItemRepository;
	private UserRepository userRepository;
	private ActivityRepository activityRepository;
	private VideoContentRepository videoRepository;
	
	
	private User user;

	@After
	public void tearDown() throws Exception {
		EntityManagerSupport.closeEntityManager();
	}

	@Before
	public void setUp() throws Exception {

		engagedItemRepository = new EngagedItemRepositoryJpaImpl();
		videoRepository = new VideoContentRepositoryJpaImpl();
		activityRepository = new ActivityRepositoryJpaImpl();

		userRepository = new UserRepositoryJpaImpl();
		
		// create user and identity as we normally would
		user = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		
		EntityManagerSupport.beginTransaction();
		userRepository.create(user);
		EntityManagerSupport.commit();

		log.info("id {}", user.getUserId());
	}

	@Test
	public void testCreateEngagedActivity() throws Exception {
		Activity activity = TestActivityFactory.createActivityWithMininumRequirements(user, ExternalNetwork.Facebook, "http://my.link.com");
		
		// create the activity
		EntityManagerSupport.beginTransaction();
		activityRepository.create(activity);
		EntityManagerSupport.commit();
		
		EngagedActivity engagedActivity = new EngagedActivity(user, activity);
		EntityManagerSupport.beginTransaction();
		engagedItemRepository.create(engagedActivity);
		EntityManagerSupport.commit();
		
		EngagedItem item = engagedItemRepository.read(engagedActivity.getEngagedItemId());
		Assert.assertNotNull(item.getUser());
		Assert.assertTrue(item instanceof EngagedActivity);
	
	}
	
	@Test
	public void testCreateEngagedVideo() throws Exception {
		VideoContent video = TestVideoContentFactory.createVideoContentWithMininumRequiredFields(user, ExternalNetwork.YouTube);
		
		// create the activity
		EntityManagerSupport.beginTransaction();
		videoRepository.create(video);
		EntityManagerSupport.commit();
		
		EngagedVideo engagedVideo = new EngagedVideo(user, video);
		EntityManagerSupport.beginTransaction();
		engagedItemRepository.create(engagedVideo);
		EntityManagerSupport.commit();
		
		EngagedItem item = engagedItemRepository.read(engagedVideo.getEngagedItemId());
		Assert.assertNotNull(item.getUser());
		Assert.assertTrue(item instanceof EngagedVideo);
	
	}
	
	@Test
	public void testCreateEngagedDocument() throws Exception {
		VideoContent video = TestVideoContentFactory.createVideoContentWithMininumRequiredFields(user, ExternalNetwork.YouTube);
		
		// create the activity
		EntityManagerSupport.beginTransaction();
		videoRepository.create(video);
		EntityManagerSupport.commit();
		
		// user clicked on a video result
		EngagedDocument engagedDocument = new EngagedDocument(user, "karate", video);
		EntityManagerSupport.beginTransaction();
		engagedItemRepository.create(engagedDocument);
		EntityManagerSupport.commit();
		
		EngagedItem item = engagedItemRepository.read(engagedDocument.getEngagedItemId());
		Assert.assertNotNull(item.getUser());
		Assert.assertTrue(item instanceof EngagedDocument);
	
		EngagedDocument persisted = (EngagedDocument)item;
		Assert.assertNotNull(persisted.getVideoContent());
		
	}



}
