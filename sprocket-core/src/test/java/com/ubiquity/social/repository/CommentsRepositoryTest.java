package com.ubiquity.social.repository;

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
import com.ubiquity.integration.domain.Comment;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.factory.TestActivityFactory;
import com.ubiquity.integration.repository.ActivityRepository;
import com.ubiquity.integration.repository.ActivityRepositoryJpaImpl;

public class CommentsRepositoryTest {
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
		
		owner = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		
		EntityManagerSupport.beginTransaction();
		userRepository.create(owner);
		EntityManagerSupport.commit();
		
		// now create activities based on content
		activity = TestActivityFactory.createActivityWithMininumRequirements(owner, ExternalNetwork.Facebook);
		activity.getComments().addAll(TestActivityFactory.createCommentListWithMininumRequirements(activity));
		EntityManagerSupport.beginTransaction();
		activityRepository.create(activity);
		EntityManagerSupport.commit();
	}

	@Test
	public void testCreateActivity() throws Exception {
		//check on activity
		Activity persisted = activityRepository.read(activity.getActivityId());
		Assert.assertNotNull(persisted.getActivityId());
		Assert.assertEquals(activity.getTitle(), persisted.getTitle());
		Assert.assertEquals(activity.getBody(), persisted.getBody());
		//check on top level comments size
		Assert.assertEquals(activity.getComments().size(), persisted.getComments().size());
		
		Comment activityComment = activity.getComments().iterator().next();
		Comment persistedComment = persisted.getComments().iterator().next();
		//check on top level  values
		Assert.assertEquals(activityComment.getBody(), persistedComment.getBody());
		// check on top level reply count 
		Assert.assertEquals(activityComment.getReplies().size(), persistedComment.getReplies().size());
		
	}
	
}
