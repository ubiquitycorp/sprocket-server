package com.ubiquity.social.repository;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.factory.TestUserFactory;
import com.ubiquity.identity.repository.UserRepository;
import com.ubiquity.identity.repository.UserRepositoryJpaImpl;
import com.ubiquity.integration.domain.Activity;
import com.ubiquity.integration.domain.ActivityType;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.Interest;
import com.ubiquity.integration.factory.TestActivityFactory;
import com.ubiquity.integration.repository.ActivityRepository;
import com.ubiquity.integration.repository.ActivityRepositoryJpaImpl;
import com.ubiquity.integration.repository.InterestRepository;
import com.ubiquity.integration.repository.InterestRepositoryJpaImpl;
import com.ubiquity.media.domain.Image;
import com.ubiquity.media.domain.Video;

/***
 * Tests testing basic CRUD operations for a user repository
 * 
 * @author chris
 *
 */
public class ActivityRepositoryTest {

	private ActivityRepository activityRepository;
	private UserRepository userRepository;
	private InterestRepository interestRepository;
	private Interest sports;
	
	private Activity statusActivity, photoActivity, videoActivity, linkActivity;
	private User owner;

	@After
	public void tearDown() throws Exception {
		EntityManagerSupport.closeEntityManager();
	}

	@Before
	public void setUp() throws Exception {

		activityRepository = new ActivityRepositoryJpaImpl();
		userRepository = new UserRepositoryJpaImpl();
		interestRepository = new InterestRepositoryJpaImpl();
		
		owner = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		
		EntityManagerSupport.beginTransaction();
		userRepository.create(owner);
		EntityManagerSupport.commit();
		
		// now create activities based on content
		statusActivity = TestActivityFactory.createActivityWithMininumRequirements(owner, ExternalNetwork.Facebook);
		videoActivity = TestActivityFactory.createActivityWithMininumRequirements(owner, ExternalNetwork.Facebook, new Video.Builder().url("http://my.video.url").build());
		photoActivity = TestActivityFactory.createActivityWithMininumRequirements(owner, ExternalNetwork.Facebook, new Image("http://my.image.url"));
		linkActivity = TestActivityFactory.createActivityWithMininumRequirements(owner, ExternalNetwork.Facebook, "http://my.link.url");

		sports = new Interest("Sports", null);

		EntityManagerSupport.beginTransaction();
		activityRepository.create(statusActivity);
		activityRepository.create(videoActivity);
		activityRepository.create(photoActivity);
		activityRepository.create(linkActivity);
		interestRepository.create(sports);
		EntityManagerSupport.commit();
	}

	@Test
	public void testCreateStatus() throws Exception {
		Activity persisted = activityRepository.read(statusActivity.getActivityId());
		Assert.assertNotNull(persisted.getActivityId());
		Assert.assertEquals(statusActivity.getTitle(), persisted.getTitle());
		Assert.assertEquals(statusActivity.getBody(), persisted.getBody());
	}
	
	@Test
	public void testCreateVideo() throws Exception {
		
		Activity persisted = activityRepository.read(videoActivity.getActivityId());
		Assert.assertNotNull(persisted.getActivityId());
		Assert.assertNotNull(persisted.getVideo());
		Assert.assertEquals(persisted.getActivityType(), ActivityType.VIDEO);

	}
	
	@Test
	public void testAddVideoWithInterests() throws Exception {
		
		Activity withInterests = TestActivityFactory.createActivityWithMininumRequirements(owner, ExternalNetwork.Facebook, new Video.Builder().url("http://my.video.url").build());
		// add some tags, to make sure they aren't being persisted
		withInterests.getTags().add("#goirish"); 
		withInterests.getInterests().add(sports);
		
		EntityManagerSupport.beginTransaction();
		activityRepository.create(withInterests);
		EntityManagerSupport.commit();
		
		// read it back
		Activity persisted = activityRepository.read(withInterests.getActivityId());
		Assert.assertFalse(persisted.getInterests().isEmpty());

	}
	
	
	@Test
	public void testCreatePhoto() throws Exception {
		
		Activity persisted = activityRepository.read(photoActivity.getActivityId());
		Assert.assertNotNull(persisted.getActivityId());
		Assert.assertNotNull(persisted.getImage());
		Assert.assertEquals(persisted.getActivityType(), ActivityType.PHOTO);

	}
	
	@Test
	public void testCreateLink() throws Exception {
		
		Activity persisted = activityRepository.read(linkActivity.getActivityId());
		Assert.assertNotNull(persisted.getActivityId());
		Assert.assertNotNull(persisted.getLink());
		Assert.assertEquals(persisted.getActivityType(), ActivityType.LINK);

	}
	
	
	@Test
	public void testFindByOwner() throws Exception {
		List<Activity> allActivities = activityRepository.findByOwnerId(owner.getUserId());
		Assert.assertFalse(allActivities.isEmpty());
		Activity persisted = allActivities.get(0);
		Assert.assertTrue(persisted.getActivityId().longValue() == statusActivity.getActivityId().longValue());
	}
	
	@Test
	public void testFindByExternalIdentifier() throws Exception {
		Activity persisted = activityRepository.getByExternalIdentifierAndSocialNetwork(statusActivity.getExternalIdentifier(), owner.getUserId(), ExternalNetwork.Facebook);
		Assert.assertNotNull(persisted);
		Assert.assertTrue(persisted.getActivityId().longValue() == statusActivity.getActivityId().longValue());
		
		// query by different user id
		persisted = activityRepository.getByExternalIdentifierAndSocialNetwork(statusActivity.getExternalIdentifier(), new java.util.Random().nextLong(), ExternalNetwork.Facebook);		
		Assert.assertNull(persisted);

		// query by same id, different network
		persisted = activityRepository.getByExternalIdentifierAndSocialNetwork(statusActivity.getExternalIdentifier(), owner.getUserId(), ExternalNetwork.Facebook);
		Assert.assertNotNull(persisted);
		
		// create one with a null id (i.e. public), everything else the same
		Activity publicStatusActivity = TestActivityFactory.createActivityWithMininumRequirements(null, ExternalNetwork.Facebook);
		EntityManagerSupport.beginTransaction();
		activityRepository.create(publicStatusActivity);
		EntityManagerSupport.commit();

		
		persisted = activityRepository.getByExternalIdentifierAndNetwork(publicStatusActivity.getExternalIdentifier(), ExternalNetwork.Facebook);
		Assert.assertNotNull(persisted);
		

	}

}
