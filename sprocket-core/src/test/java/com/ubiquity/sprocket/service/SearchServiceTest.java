package com.ubiquity.sprocket.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.redis.JedisConnectionFactory;
import com.ubiquity.content.api.ContentAPIFactory;
import com.ubiquity.content.domain.VideoContent;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.factory.UserFactory;
import com.ubiquity.media.domain.Image;
import com.ubiquity.media.domain.Video;
import com.ubiquity.social.api.SocialAPIFactory;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.Message;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.sprocket.domain.Document;
import com.ubiquity.sprocket.search.SearchKeys;

public class SearchServiceTest {

	private static SearchService searchService;

	private Logger log = LoggerFactory.getLogger(getClass());

	@BeforeClass
	public static void setUp() throws Exception {
		Configuration config = new PropertiesConfiguration("test.properties");
		searchService = new SearchService(config);
		
		JedisConnectionFactory.initialize(config);
		SocialAPIFactory.initialize(config);
		ContentAPIFactory.initialize(config);

		searchService.deleteAll();
	}

	
	@Test
	public void testLiveSearchWithFacebook() {
		
		User user = UserFactory.createTestUserWithMinimumRequiredProperties();
		user.getIdentities().add(new ExternalIdentity.Builder().user(user).accessToken("CAACEdEose0cBADv5biV5FN9Vqfxzg0l794hNmHvEvgWE9RtLXTZCalwK6wYGMjttqe8wuTkC5ZC7uIyzrdBTsLUxKiYxREoZCrqLTZAfdwJFv2hbOt8QTnno7t7tWYJIPWh1szP65gLjdxwUOJQUs5QScrWqnPidFDj6U0sZBKh3S9zeAVam0ND7zZAxwNg9RcrZBAI4NoZBqMjVezZB28vLv").clientPlatform(ClientPlatform.WEB).externalNetwork(ExternalNetwork.Facebook.ordinal()).build());
		List<Document> documents = searchService.searchLiveDocuments("Karate", user, ExternalNetwork.Facebook, 1);
		log.debug("documents: {}", documents);
		Assert.assertFalse(documents.isEmpty());
		
	}
	
	@Test
	public void testLiveSearchWithVimeo() {
		
		User user = UserFactory.createTestUserWithMinimumRequiredProperties();
		user.getIdentities().add(new ExternalIdentity.Builder().user(user).accessToken("a5f46897abbbd2b83501ea79b4916f44").clientPlatform(ClientPlatform.WEB).externalNetwork(ExternalNetwork.Vimeo.ordinal()).build());
		List<Document> documents = searchService.searchLiveDocuments("Karate", user, ExternalNetwork.Vimeo, 1);
		log.debug("documents: {}", documents);
		Assert.assertFalse(documents.isEmpty());
		
	}
	
	@Test 
	public void testLiveSearchWithYouTube() {
		User user = UserFactory.createTestUserWithMinimumRequiredProperties();
		user.getIdentities().add(new ExternalIdentity.Builder().user(user).accessToken("ya29.SQCCPSWqzhtnbxwAAACJpZt8z7tsGirHhSIiUKhaaj2uIe8IkfhTMZSq8kbDzg").clientPlatform(ClientPlatform.WEB).externalNetwork(ExternalNetwork.YouTube.ordinal()).build());
		List<Document> documents = searchService.searchLiveDocuments("Karate", user, ExternalNetwork.YouTube, 1);
		log.debug("documents: {}", documents);
		Assert.assertFalse(documents.isEmpty());
	}
	
	@Test
	public void testLiveSearchWithLinkedIn() {
		
	}
	
	@Test
	public void testLiveSearchWithTwitter() {
		
	}
	
	

	@Test
	public void testAddMessagesReturnsInBasicSearch() {
		// build partial doc with the fields being indexed
		Message message = new Message.Builder()
		.messageId(new java.util.Random().nextLong())
		.owner(new User.Builder().userId(1l).build())
		.body(UUID.randomUUID().toString())
		.externalNetwork(ExternalNetwork.Facebook)
		.title(UUID.randomUUID().toString()).sender(
				new Contact.Builder().displayName("Jack").build())
				.externalNetwork(ExternalNetwork.Facebook)
				.build();

		searchService.indexMessages(
				Arrays.asList(new Message[] { message }));

		// search by sender display name, making sure that only this entity shows up and it's of type "Message"
		List<Document> documents = searchService.searchIndexedDocuments("Jack", 1l);

		Assert.assertTrue(documents.size() == 1);
		Document result = documents.get(0);
		Assert.assertEquals(Message.class.getSimpleName(), result.getFields().get(SearchKeys.Fields.FIELD_DATA_TYPE));

		testUserAndSocialNetworkFilter(message.getSender().getDisplayName(), 1l, ExternalNetwork.Facebook);


	}

	@Test
	public void testDedupe() {
		// build a video content with random strings so that it contains the same signature 
		VideoContent videoContent = new VideoContent.Builder()
		.videoContentId(new java.util.Random().nextLong())
		.category(UUID.randomUUID().toString())
		.title(UUID.randomUUID().toString())
		.video(new Video.Builder().itemKey(UUID.randomUUID().toString()).build())
		.thumb(new Image("http://"+UUID.randomUUID().toString()+".com"))
		.description(UUID.randomUUID().toString()).build();
		// add 2
		searchService.indexVideos(
				Arrays.asList(new VideoContent[] { videoContent, videoContent }), 1l);

		// search, making sure that only this entity shows up and it's of type "VideoContent"
		List<Document> documents = searchService.searchIndexedDocuments(videoContent.getTitle(), 1l);
		log.debug("documents {}", documents);
		Assert.assertTrue(documents.size() == 1);

	}



	@Test
	public void testAddActivitiesReturnsInBasicSearch() {
		// build partial doc with the fields being indexed
		Activity activity = new Activity.Builder()
		.activityId(new java.util.Random().nextLong())
		.owner(new User.Builder().userId(1l).build())
		.body(UUID.randomUUID().toString())
		.externalNetwork(ExternalNetwork.LinkedIn)
		.title(UUID.randomUUID().toString()).postedBy(
				new Contact.Builder().displayName("Bob").build())
				.build();
		searchService.indexActivities(
				Arrays.asList(new Activity[] { activity }));

		// search by sender display name, making sure that only this entity shows up and it's of type "Message"
		List<Document> documents = searchService.searchIndexedDocuments("Bob", 1l);
		log.debug("documents: {}", documents);
		Assert.assertTrue(documents.size() == 1);
		Document result = documents.get(0);
		Assert.assertEquals(Activity.class.getSimpleName(), result.getFields().get(SearchKeys.Fields.FIELD_DATA_TYPE));

		
		testUserAndSocialNetworkFilter(activity.getPostedBy().getDisplayName(), 1l, ExternalNetwork.LinkedIn);



	}

	@Test
	public void testAddVideoReturnsInBasicSearch() {
		// build partial doc with the fields being indexed
		VideoContent videoContent = new VideoContent.Builder()
		.videoContentId(new java.util.Random().nextLong())
		.category(UUID.randomUUID().toString())
		.title("video")
		.video(new Video.Builder().itemKey(UUID.randomUUID().toString()).build())
		.thumb(new Image("http://"+UUID.randomUUID().toString()+".com"))
		.description(UUID.randomUUID().toString()).build();

		searchService.indexVideos(
				Arrays.asList(new VideoContent[] { videoContent }), 1l);

		// search, making sure that only this entity shows up and it's of type "VideoContent"
		List<Document> documents = searchService.searchIndexedDocuments("video", 1l);

		Assert.assertTrue(documents.size() == 1);
		Document result = documents.get(0);
		Assert.assertEquals(VideoContent.class.getSimpleName(), result.getFields().get(SearchKeys.Fields.FIELD_DATA_TYPE));

		// test the user filter
		documents = searchService.searchIndexedDocuments(videoContent.getTitle(), 2l);
		Assert.assertTrue(documents.isEmpty());
		



	}	

	/***
	 * Convenience method for testing if the social network and user id filters persisted
	 * 
	 * @param searchTerm
	 * @param userId
	 * @param socialNetwork
	 */
	private void testUserAndSocialNetworkFilter(String searchTerm, Long userId, ExternalNetwork socialNetwork) {
		// test the user filter
		List<Document> documents = searchService.searchIndexedDocuments(searchTerm, userId + 1);
		Assert.assertTrue(documents.isEmpty());

		// test the social filter
		documents = searchService.searchIndexedDocuments(searchTerm, userId, socialNetwork);
		Assert.assertTrue(!documents.isEmpty());

		ExternalNetwork anotherNetwork = socialNetwork.ordinal() == 0 ? ExternalNetwork.values()[1] : ExternalNetwork.values()[0];
		// pick a network that is not the passed in network
		documents = searchService.searchIndexedDocuments(searchTerm, 1l, anotherNetwork);
		Assert.assertTrue(documents.isEmpty());
	}



}
