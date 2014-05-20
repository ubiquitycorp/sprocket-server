package com.ubiquity.identity.repository;

import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.NativeIdentity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.repository.ActivityRepository;
import com.ubiquity.social.repository.ActivityRepositoryJpaImpl;

/***
 * Tests testing basic CRUD operations for a user repository
 * 
 * @author chris
 *
 */
public class ActivityRepositoryTest {

	private ActivityRepository activityRepository;
	private Activity activity;
	private User owner;

	@After
	public void tearDown() throws Exception {
		EntityManagerSupport.closeEntityManager();
	}

	@Before
	public void setUp() throws Exception {

		activityRepository = new ActivityRepositoryJpaImpl();

		UserRepository userRepository = new UserRepositoryJpaImpl();
		owner = new User.Builder()
				.lastUpdated(System.currentTimeMillis())
				.firstName(UUID.randomUUID().toString())
				.lastName(UUID.randomUUID().toString())
				.email(UUID.randomUUID().toString())
				.clientPlatform(ClientPlatform.Android)
				.displayName(UUID.randomUUID().toString())
				.build();
		userRepository.create(owner);
		
		NativeIdentity identity = new NativeIdentity.Builder()
			.isActive(Boolean.TRUE)
			.lastUpdated(System.currentTimeMillis())
			.user(owner)
			.username(UUID.randomUUID().toString())
			.password(UUID.randomUUID().toString())
			.build();
		owner.getIdentities().add(identity);
		
		EntityManagerSupport.beginTransaction();
		userRepository.create(owner);
		EntityManagerSupport.commit();
//		
//		// now create a contact - who isn't necessarily a user
//		sender = new Contact.Builder().lastUpdated(System.currentTimeMillis())
//						.displayName("Jill").firstName("Jill").lastName("Jackson")
//						.email("jill@mail.com").profileUrl("http://jills.profile.link")
//						.image(new Image("http://jills.image.url"))
//						.socialIdentity(new ExternalIdentity.Builder()
//							.identifier(UUID.randomUUID().toString())
//							.isActive(Boolean.TRUE)
//							.lastUpdated(System.currentTimeMillis())
//							.socialProvider(SocialProvider.Facebook)
//							.build())
//						.owner(owner)
//						
//						.build();
			
//		ContactRepository contactRepository = new ContactRepositoryJpaImpl();
//		EntityManagerSupport.beginTransaction();
//		contactRepository.create(sender);
//		EntityManagerSupport.commit();
		
		// now create a message
		activity  = new Activity.Builder()
			.title(UUID.randomUUID().toString())
			.body(UUID.randomUUID().toString())
			.type("post")
			.creationDate(System.currentTimeMillis())
			.lastUpdated(System.currentTimeMillis())
//			.sender(sender)
			.owner(owner)
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


}
