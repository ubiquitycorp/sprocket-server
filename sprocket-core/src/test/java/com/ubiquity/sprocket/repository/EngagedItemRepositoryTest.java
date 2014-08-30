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
import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.repository.ActivityRepository;
import com.ubiquity.social.repository.ActivityRepositoryJpaImpl;
import com.ubiquity.sprocket.domain.EngagedActivity;
import com.ubiquity.sprocket.domain.EngagedDocument;
import com.ubiquity.sprocket.domain.EngagedItem;
import com.ubiquity.sprocket.domain.EngagedVideo;
import com.ubiquity.sprocket.domain.GroupMembership;

/***
 * Tests testing basic CRUD operations for an engaged item repository
 * 
 * @author chris
 *
 */
public class EngagedItemRepositoryTest {

	private Logger log = LoggerFactory.getLogger(getClass());

	private EngagedItemRepository engagedItemRepository;
	private EngagedVideoRepository engagedVideoRepository;
	private EngagedActivityRepository engagedActivityRepository;
	private EngagedDocumentRepository engagedDocumentRepository;
	private GroupMembershipRepository groupMembershipRepository;
	
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
		engagedVideoRepository = new EngagedVideoRepositoryJpaImpl();
		engagedActivityRepository = new EngagedActivityRepositoryJpaImpl();
		engagedDocumentRepository = new EngagedDocumentRepositoryJpaImpl();
		groupMembershipRepository = new GroupMembershipRepositoryJpaImpl();

		videoRepository = new VideoContentRepositoryJpaImpl();
		activityRepository = new ActivityRepositoryJpaImpl();

		userRepository = new UserRepositoryJpaImpl();

		// create identifiers for groups
		soccerMoms = UUID.randomUUID().toString();
		millenials = UUID.randomUUID().toString();

		// create mom and beber and identity as we normally would
		mom = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		beber = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		jayz = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		

		EntityManagerSupport.beginTransaction();
		userRepository.create(mom);
		userRepository.create(beber);
		userRepository.create(jayz);
		
		groupMembershipRepository.create(new GroupMembership(null, mom, soccerMoms));
		groupMembershipRepository.create(new GroupMembership(null, beber, millenials));
		groupMembershipRepository.create(new GroupMembership(null, jayz, millenials));

		EntityManagerSupport.commit();

		log.info("id {}", mom.getUserId());
	}

	@Test
	public void testCreateEngagedActivity() throws Exception {


		EngagedActivity engagedActivity = createAndPersistEngagedActivity(mom);

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
	public void testFindMeanActivities() throws Exception {
		// mom clicks on a post
		createAndPersistEngagedActivity(mom);

		// beber clicks
		EngagedActivity beberActivity = createAndPersistEngagedActivity(beber);

		List<EngagedActivity> meanActivities = engagedActivityRepository.findMeanByGroup(soccerMoms);
		Assert.assertFalse(meanActivities.isEmpty());

		// make sure beber video is not in this return list to test basic filtering by group
		Boolean beberFound = Boolean.FALSE;
		for(EngagedActivity persisted : meanActivities) {
			if(persisted.getActivity().getActivityId().equals(beberActivity.getActivity().getActivityId())) {
				beberFound = Boolean.TRUE;
				break;
			}
		}
		Assert.assertFalse(beberFound);

		// jayz clicks on a post
		createAndPersistEngagedActivity(jayz);

		// now jayz click on the same post
		createAndPersistEngagedActivity(jayz, beberActivity.getActivity());


		meanActivities = engagedActivityRepository.findMeanByGroup(millenials);
		Assert.assertFalse(meanActivities.isEmpty());

		// the first video should be the video watched by both
		EngagedActivity meanActivity = meanActivities.get(0);
		Assert.assertTrue(meanActivity.getActivity().getActivityId().equals(meanActivity.getActivity().getActivityId()));

	}


	@Test
	public void testFindMeanVideos() throws Exception {

		// mom watches a video
		createAndPersistEngagedVideo(mom);

		// beber watches a video
		EngagedVideo beberVideo = createAndPersistEngagedVideo(beber);

		List<EngagedVideo> meanVideos = engagedVideoRepository.findMeanByGroup(soccerMoms);
		Assert.assertFalse(meanVideos.isEmpty());

		// make sure beber video is not in this return list to test basic filtering by group
		Boolean beberFound = Boolean.FALSE;
		for(EngagedVideo persisted : meanVideos) {
			if(persisted.getVideoContent().getVideoContentId().equals(beberVideo.getVideoContent().getVideoContentId())) {
				beberFound = Boolean.TRUE;
				break;
			}
		}
		Assert.assertFalse(beberFound);

		// jayz watches a video
		createAndPersistEngagedVideo(jayz);

		// now jayz watches the beber video
		createAndPersistEngagedVideo(jayz, beberVideo.getVideoContent());


		meanVideos = engagedVideoRepository.findMeanByGroup(millenials);
		Assert.assertFalse(meanVideos.isEmpty());

		// the first video should be the video watched by both
		EngagedVideo meanVideo = meanVideos.get(0);
		Assert.assertTrue(meanVideo.getVideoContent().getVideoContentId().equals(beberVideo.getVideoContent().getVideoContentId()));

	}

	@Test
	public void testFindMeanDocuments() throws Exception {

		// mom watches a video
		createAndPersistEngagedVideoDocument(mom);

		// beber clicks on a video search result
		EngagedDocument beberDoc = createAndPersistEngagedVideoDocument(beber);

		List<EngagedDocument> meanDocs = engagedDocumentRepository.findMeanByGroup(soccerMoms);
		Assert.assertFalse(meanDocs.isEmpty());

		// make sure beber video is not in this return list to test basic filtering by group
		Boolean beberFound = Boolean.FALSE;
		for(EngagedDocument persisted : meanDocs) {
			if(persisted.getVideoContent().getVideoContentId().equals(beberDoc.getVideoContent().getVideoContentId())) {
				beberFound = Boolean.TRUE;
				break;
			}
		}
		Assert.assertFalse(beberFound);

		// jayz watches a video
		createAndPersistEngagedVideoDocument(jayz);

		// now jayz watches the beber video
		createAndPersistEngagedDocument(jayz, beberDoc.getVideoContent());


		meanDocs = engagedDocumentRepository.findMeanByGroup(millenials);
		Assert.assertFalse(meanDocs.isEmpty());

		// the first video should be the video watched by both
		EngagedDocument meanDoc = meanDocs.get(0);
		Assert.assertTrue(meanDoc.getVideoContent().getVideoContentId().equals(beberDoc.getVideoContent().getVideoContentId()));
		
		// now create a document of a different type that beber is all over because everything beber touches turns to gold
		beberDoc = createAndPersistEngagedActivityDocument(beber);
		
		// now jayz watches the beber video
		createAndPersistEngagedDocument(jayz, beberDoc.getActivity());
		
		// beber a few more times, making this data type the most popular search result
		createAndPersistEngagedDocument(beber, beberDoc.getActivity());
		createAndPersistEngagedDocument(beber, beberDoc.getActivity());
		createAndPersistEngagedDocument(beber, beberDoc.getActivity());
		
		meanDocs = engagedDocumentRepository.findMeanByGroup(millenials);
		Assert.assertFalse(meanDocs.isEmpty());

		// the first video should be the video watched by both
		meanDoc = meanDocs.get(0);
		Assert.assertTrue(meanDoc.getDocumentDataType().equals(Activity.class.getSimpleName()));
		Assert.assertTrue(meanDoc.getActivity().getActivityId().equals(beberDoc.getActivity().getActivityId()));
		


		

	}

	@Test
	public void testCreateEngagedDocument() throws Exception {
		VideoContent video = TestVideoContentFactory.createVideoContentWithMininumRequiredFields(mom, ExternalNetwork.YouTube);

		// create the activity
		EntityManagerSupport.beginTransaction();
		videoRepository.create(video);
		EntityManagerSupport.commit();

		// user clicked on a video result
		EngagedDocument engagedDocument = createAndPersistEngagedDocument(mom, video);

		EngagedItem item = engagedItemRepository.read(engagedDocument.getEngagedItemId());
		Assert.assertNotNull(item.getUser());
		Assert.assertTrue(item instanceof EngagedDocument);

		EngagedDocument persisted = (EngagedDocument)item;
		Assert.assertNotNull(persisted.getVideoContent());

	}


	private EngagedActivity createAndPersistEngagedActivity(User u, Activity activity) {

		// create the activity
		EntityManagerSupport.beginTransaction();
		activityRepository.create(activity);
		EntityManagerSupport.commit();

		EngagedActivity engagedActivity = new EngagedActivity(u, activity);
		EntityManagerSupport.beginTransaction();
		engagedItemRepository.create(engagedActivity);
		EntityManagerSupport.commit();

		return engagedActivity;

	}

	private EngagedDocument createAndPersistEngagedActivityDocument(User u) {
		Activity activity = TestActivityFactory.createActivityWithMininumRequirements(u, ExternalNetwork.Facebook, "http://my.link.com");
		return createAndPersistEngagedDocument(u, activity);

	}
	
	private EngagedDocument createAndPersistEngagedDocument(User u, Activity activity) {

		// create the video
		EntityManagerSupport.beginTransaction();
		activityRepository.create(activity);
		EntityManagerSupport.commit();

		EngagedDocument engagedDocument = new EngagedDocument(u, UUID.randomUUID().toString(), activity);
		EntityManagerSupport.beginTransaction();
		engagedItemRepository.create(engagedDocument);
		EntityManagerSupport.commit();
		return engagedDocument;
	}
	
	private EngagedDocument createAndPersistEngagedDocument(User u, VideoContent video) {

		// create the video
		EntityManagerSupport.beginTransaction();
		videoRepository.create(video);
		EntityManagerSupport.commit();

		EngagedDocument engagedDocument = new EngagedDocument(u, UUID.randomUUID().toString(), video);
		EntityManagerSupport.beginTransaction();
		engagedItemRepository.create(engagedDocument);
		EntityManagerSupport.commit();
		return engagedDocument;
	}

	private EngagedDocument createAndPersistEngagedVideoDocument(User u) {

		VideoContent video = TestVideoContentFactory.createVideoContentWithMininumRequiredFields(u, ExternalNetwork.YouTube);
		return createAndPersistEngagedDocument(u, video);

	}

	private EngagedActivity createAndPersistEngagedActivity(User u) {
		Activity activity = TestActivityFactory.createActivityWithMininumRequirements(u, ExternalNetwork.Facebook, "http://my.link.com");
		return createAndPersistEngagedActivity(u, activity);
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
