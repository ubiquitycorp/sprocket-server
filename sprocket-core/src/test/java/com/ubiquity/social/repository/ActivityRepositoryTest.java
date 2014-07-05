package com.ubiquity.social.repository;

import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.factory.UserFactory;
import com.ubiquity.identity.repository.UserRepository;
import com.ubiquity.identity.repository.UserRepositoryJpaImpl;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.domain.SocialNetwork;

/***
 * Tests testing basic CRUD operations for a user repository
 * 
 * @author chris
 *
 */
public class ActivityRepositoryTest {

	private ActivityRepository activityRepository;
	private UserRepository userRepository;
	
	private Activity activity;
	private User owner;

	@After
	public void tearDown() throws Exception {
		EntityManagerSupport.closeEntityManager();
	}

	@Before
	public void setUp() throws Exception {

		activityRepository = new ActivityRepositoryJpaImpl();
		userRepository = new UserRepositoryJpaImpl();
		
		owner = UserFactory.createTestUserWithMinimumRequiredProperties();
		
		EntityManagerSupport.beginTransaction();
		userRepository.create(owner);
		EntityManagerSupport.commit();
		
		// now create a message
		activity  = new Activity.Builder()
			.title(UUID.randomUUID().toString())
			.body(UUID.randomUUID().toString())
			.type("post")
			.creationDate(System.currentTimeMillis())
			.lastUpdated(System.currentTimeMillis())
			.externalIdentifier(UUID.randomUUID().toString())
			.owner(owner)
			.socialNetwork(SocialNetwork.Facebook)
			.build();
		
		EntityManagerSupport.beginTransaction();
		activityRepository.create(activity);
		EntityManagerSupport.commit();
	}

	@Test
	public void testCreate() throws Exception {
		Activity persisted = activityRepository.read(activity.getActivityId());
		Assert.assertNotNull(persisted.getActivityId());
		Assert.assertEquals(activity.getTitle(), persisted.getTitle());
		Assert.assertEquals(activity.getBody(), persisted.getBody());
	}
	
	@Test
	public void testFindByOwner() throws Exception {
		List<Activity> allActivities = activityRepository.findByOwnerId(owner.getUserId());
		Assert.assertFalse(allActivities.isEmpty());
		Activity persisted = allActivities.get(0);
		Assert.assertTrue(persisted.getActivityId().longValue() == activity.getActivityId().longValue());
	}
	
	@Test
	public void testFindByExternalIdentifier() throws Exception {
		Activity persisted = activityRepository.getByExternalIdentifierAndSocialNetwork(activity.getExternalIdentifier(), owner.getUserId(), SocialNetwork.Facebook);
		Assert.assertNotNull(persisted);
		Assert.assertTrue(persisted.getActivityId().longValue() == activity.getActivityId().longValue());
		
		// query by different user id
		persisted = activityRepository.getByExternalIdentifierAndSocialNetwork(activity.getExternalIdentifier(), new java.util.Random().nextLong(), SocialNetwork.Facebook);		
		Assert.assertNull(persisted);

		// query by same id, different network
		persisted = activityRepository.getByExternalIdentifierAndSocialNetwork(activity.getExternalIdentifier(), owner.getUserId(), SocialNetwork.Facebook);
		Assert.assertNotNull(persisted);

	}

}
