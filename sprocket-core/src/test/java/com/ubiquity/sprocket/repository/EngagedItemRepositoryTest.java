package com.ubiquity.sprocket.repository;

import java.util.List;
import java.util.UUID;

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
import com.ubiquity.media.domain.Video;
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

	private String soccerMoms, millenials;

	private User mom, beber, jayz;

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

		soccerMoms = UUID.randomUUID().toString();
		millenials = UUID.randomUUID().toString();

		// create mom and beber and identity as we normally would
		mom = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		mom.getGroups().add(soccerMoms);

		beber = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		beber.getGroups().add(millenials);
		
		jayz = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		jayz.getGroups().add(millenials);

		EntityManagerSupport.beginTransaction();
		userRepository.create(mom);
		userRepository.create(beber);
		userRepository.create(jayz);
		EntityManagerSupport.commit();

		log.info("id {}", mom.getUserId());
	}

	@Test
	public void testCreateEngagedActivity() throws Exception {
		Activity activity = TestActivityFactory.createActivityWithMininumRequirements(mom, ExternalNetwork.Facebook, "http://my.link.com");

		// create the activity
		EntityManagerSupport.beginTransaction();
		activityRepository.create(activity);
		EntityManagerSupport.commit();

		EngagedActivity engagedActivity = new EngagedActivity(mom, activity);
		EntityManagerSupport.beginTransaction();
		engagedItemRepository.create(engagedActivity);
		EntityManagerSupport.commit();

		EngagedItem item = engagedItemRepository.read(engagedActivity.getEngagedItemId());
		Assert.assertNotNull(item.getUser());
		Assert.assertTrue(item instanceof EngagedActivity);

	}

	@Test
	public void testCreateEngagedVideo() throws Exception {
		EngagedVideo engagedVideo = createAndPersistEngagedVideo(mom);

		EngagedItem item = engagedItemRepository.read(engagedVideo.getEngagedItemId());
		Assert.assertNotNull(item.getUser());
		Assert.assertTrue(item instanceof EngagedVideo);

	}

	@Test
	public void testFindMean() throws Exception {

		// mom watches a video
		createAndPersistEngagedVideo(mom);

		// beber watches a video
		EngagedVideo beberVideo = createAndPersistEngagedVideo(beber);

		List<EngagedItem> items = engagedItemRepository.findMeanByGroup(soccerMoms);
		Assert.assertFalse(items.isEmpty());
		
		// make sure beber video is not in this return list to test basic filtering by group
		Boolean beberFound = Boolean.FALSE;
		for(EngagedItem item : items) {
			EngagedVideo persistedVideo = (EngagedVideo)item;
			if(persistedVideo.getVideoContent().getVideoContentId().equals(beberVideo.getVideoContent().getVideoContentId())) {
				beberFound = Boolean.TRUE;
				break;
			}
		}
		Assert.assertFalse(beberFound);
		
		// jayz watches a video
		createAndPersistEngagedVideo(jayz);
		
		// now jayz watches the beber video
		createAndPersistEngagedVideo(jayz, beberVideo.getVideoContent());

		List<EngagedItem> mean = engagedItemRepository.findMeanByGroup(millenials);
		Assert.assertFalse(mean.isEmpty());
		
		
		

	}

	@Test
	public void testCreateEngagedDocument() throws Exception {
		VideoContent video = TestVideoContentFactory.createVideoContentWithMininumRequiredFields(mom, ExternalNetwork.YouTube);

		// create the activity
		EntityManagerSupport.beginTransaction();
		videoRepository.create(video);
		EntityManagerSupport.commit();

		// user clicked on a video result
		EngagedDocument engagedDocument = new EngagedDocument(mom, "karate", video);
		EntityManagerSupport.beginTransaction();
		engagedItemRepository.create(engagedDocument);
		EntityManagerSupport.commit();

		EngagedItem item = engagedItemRepository.read(engagedDocument.getEngagedItemId());
		Assert.assertNotNull(item.getUser());
		Assert.assertTrue(item instanceof EngagedDocument);

		EngagedDocument persisted = (EngagedDocument)item;
		Assert.assertNotNull(persisted.getVideoContent());

	}


	private EngagedVideo createAndPersistEngagedVideo(User u, VideoContent video) {
		// create the video
		EntityManagerSupport.beginTransaction();
		videoRepository.create(video);
		EntityManagerSupport.commit();

		EngagedVideo engagedVideo = new EngagedVideo(u, video);
		EntityManagerSupport.beginTransaction();
		engagedItemRepository.create(engagedVideo);
		EntityManagerSupport.commit();

		return engagedVideo;
	}

	private EngagedVideo createAndPersistEngagedVideo(User u) {

		VideoContent video = TestVideoContentFactory.createVideoContentWithMininumRequiredFields(mom, ExternalNetwork.YouTube);
		return createAndPersistEngagedVideo(u, video);

	}
}
