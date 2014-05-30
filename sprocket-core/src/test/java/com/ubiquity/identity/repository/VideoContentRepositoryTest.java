package com.ubiquity.identity.repository;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.factory.UserFactory;
import com.ubiquity.media.domain.Image;
import com.ubiquity.media.domain.Video;
import com.ubiquity.social.domain.VideoContent;
import com.ubiquity.social.repository.VideoContentRepository;
import com.ubiquity.social.repository.VideoContentRepositoryJpaImpl;

/***
 * Tests testing basic CRUD operations for a user repository
 * 
 * @author chris
 *
 */
public class VideoContentRepositoryTest {

	private VideoContentRepository videoContentRepository;
	private UserRepository userRepository;
	private VideoContent videoContent;
	private User owner;

	@After
	public void tearDown() throws Exception {
		EntityManagerSupport.closeEntityManager();
	}

	@Before
	public void setUp() throws Exception {

		videoContentRepository = new VideoContentRepositoryJpaImpl();
		userRepository = new UserRepositoryJpaImpl();
		
		owner = UserFactory.createTestUserWithMinimumRequiredProperties();
		persistUser(owner);
		
		videoContent = createTestVideoWithMinimumRequiredProperties(owner);
		persistVideoContent(videoContent);
		
	}

	@Test
	public void testCreate() throws Exception {
		VideoContent persisted = videoContentRepository.read(videoContent.getVideoContentId());
		Assert.assertNotNull(persisted.getVideoContentId());
		Assert.assertEquals(videoContent.getTitle(), persisted.getTitle());
		Assert.assertEquals(videoContent.getDescription(), persisted.getDescription());
		Assert.assertEquals(videoContent.getOwner().getUserId(), persisted.getOwner().getUserId());
		Assert.assertEquals(videoContent.getThumb().getUrl(), persisted.getThumb().getUrl());
		Assert.assertEquals(videoContent.getVideo().getItemKey(), persisted.getVideo().getItemKey());
	}
	
	@Test
	public void testFindByOwner() throws Exception {
		List<VideoContent> allVideos = videoContentRepository.findByOwnerId(owner.getUserId());
		Assert.assertFalse(allVideos.isEmpty());
		VideoContent persisted = allVideos.get(0);
		Assert.assertTrue(persisted.getVideoContentId().longValue() == videoContent.getVideoContentId().longValue());
	}
	
	@Test
	public void testFindByOwnerAndItemKey() throws Exception {
		List<VideoContent> allVideos = videoContentRepository.findByOwnerIdAndItemKey(owner.getUserId(), videoContent.getVideo().getItemKey());
		Assert.assertFalse(allVideos.isEmpty());
		VideoContent persisted = allVideos.get(0);
		Assert.assertTrue(persisted.getVideoContentId().longValue() == videoContent.getVideoContentId().longValue());
	}

	@Test
	public void testDeleteWhereIdsNotEqualToList() throws Exception {
		
		// create another video for the main user
		VideoContent anotherVideo = createTestVideoWithMinimumRequiredProperties(owner);
		persistVideoContent(anotherVideo);
		
		
		// create a video for another user
		User anotherUser = UserFactory.createTestUserWithMinimumRequiredProperties();
		persistUser(anotherUser);
		VideoContent videoThatShouldRemain = createTestVideoWithMinimumRequiredProperties(anotherUser);
		persistVideoContent(videoThatShouldRemain);
		
		List<VideoContent> allVideos = videoContentRepository.findByOwnerId(owner.getUserId());
		// we should have 2 now
		Assert.assertFalse(allVideos.isEmpty());
		Assert.assertTrue(allVideos.size() == 2);
		
		// now delete any that dont match the video id we just created for the main user
		EntityManagerSupport.beginTransaction();
		videoContentRepository.deleteWithoutIds(owner.getUserId(), Arrays.asList(new Long[] { anotherVideo.getVideoContentId() }));
		EntityManagerSupport.commit();

		// we should have 1 now
		allVideos = videoContentRepository.findByOwnerId(owner.getUserId());
		Assert.assertFalse(allVideos.isEmpty());
		Assert.assertTrue(allVideos.size() == 1);
		
		// make sure the other user still has his video
		allVideos = videoContentRepository.findByOwnerId(anotherUser.getUserId());
		
		Assert.assertFalse(allVideos.isEmpty());
		Assert.assertTrue(allVideos.size() == 1);
		
	}
	
	
	private VideoContent createTestVideoWithMinimumRequiredProperties(User user) {
		return new VideoContent.Builder()
		.title(UUID.randomUUID().toString())
		.category(UUID.randomUUID().toString())
		.video(new Video.Builder().itemKey(UUID.randomUUID().toString()).build())
		.thumb(new Image(UUID.randomUUID().toString()))
		.description(UUID.randomUUID().toString())
		.owner(user)
		.lastUpdated(System.currentTimeMillis())
		.build();
		
	}
	
	private void persistUser(User user) {
		EntityManagerSupport.beginTransaction();
		userRepository.create(user);
		EntityManagerSupport.commit();
	}
	
	private void persistVideoContent(VideoContent videoContent) {
		EntityManagerSupport.beginTransaction();
		videoContentRepository.create(videoContent);
		EntityManagerSupport.commit();
	}
	

	

}
