package com.ubiquity.sprocket.repository;

import java.util.Arrays;
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
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.VideoContent;
import com.ubiquity.integration.factory.TestVideoContentFactory;
import com.ubiquity.integration.repository.VideoContentRepository;
import com.ubiquity.integration.repository.VideoContentRepositoryJpaImpl;

/***
 * Tests testing basic CRUD operations for a user repository
 * 
 * @author chris
 *
 */
// tests need to be revisited because model methods have changed relative to ContentNetwork
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
		
		owner = TestUserFactory.createTestUserWithMinimumRequiredProperties(null);
		persistUser(owner);
		
		videoContent = TestVideoContentFactory.createVideoContentWithMininumRequiredFields(owner, ExternalNetwork.YouTube);
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
		List<VideoContent> allVideos = videoContentRepository.findByItemKeyAndContentNetwork(owner.getUserId(), videoContent.getVideo().getItemKey(), ExternalNetwork.YouTube);
		Assert.assertFalse(allVideos.isEmpty());
		VideoContent persisted = allVideos.get(0);
		Assert.assertTrue(persisted.getVideoContentId().longValue() == videoContent.getVideoContentId().longValue());
	}
	
	@Test
	public void testFindByOwnerAndContentNetwork() throws Exception {
		List<VideoContent> videos = videoContentRepository.findByOwnerIdAndContentNetwork(owner.getUserId(), ExternalNetwork.YouTube);
		Assert.assertFalse(videos.isEmpty());
		
		// same user, different network
		videos = videoContentRepository.findByOwnerIdAndContentNetwork(owner.getUserId(), ExternalNetwork.Netflix);
		Assert.assertTrue(videos.isEmpty());
		
	}

	@Test
	public void testDeleteWhereIdsNotEqualToList() throws Exception {
		
		// create another video for the main user
		VideoContent anotherVideo = TestVideoContentFactory.createVideoContentWithMininumRequiredFields(owner, ExternalNetwork.YouTube);
		persistVideoContent(anotherVideo);
		
		
		// create a video for another user
		User anotherUser = TestUserFactory.createTestUserWithMinimumRequiredProperties(null);
		persistUser(anotherUser);
		VideoContent videoThatShouldRemain = TestVideoContentFactory.createVideoContentWithMininumRequiredFields(anotherUser, ExternalNetwork.YouTube);
		persistVideoContent(videoThatShouldRemain);
		
		List<VideoContent> allVideos = videoContentRepository.findByOwnerId(owner.getUserId());
		// we should have 2 now
		Assert.assertFalse(allVideos.isEmpty());
		Assert.assertTrue(allVideos.size() == 2);
		
		// now delete any that dont match the video id we just created for the main user
		EntityManagerSupport.beginTransaction();
		videoContentRepository.deleteWithoutIds(owner.getUserId(), Arrays.asList(new Long[] { anotherVideo.getVideoContentId() }), ExternalNetwork.YouTube);
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
