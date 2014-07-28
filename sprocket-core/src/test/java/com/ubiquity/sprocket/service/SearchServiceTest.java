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
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.factory.TestUserFactory;
import com.ubiquity.integration.factory.TestActivityFactory;
import com.ubiquity.integration.factory.TestContactFactory;
import com.ubiquity.integration.factory.TestMessageFactory;
import com.ubiquity.integration.factory.TestVideoContentFactory;
import com.ubiquity.social.api.SocialAPIFactory;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.Message;
import com.ubiquity.sprocket.domain.Document;
import com.ubiquity.sprocket.search.SearchKeys;

public class SearchServiceTest {

	private static SearchService searchService;

	private Logger log = LoggerFactory.getLogger(getClass());
	
	private static User owner;

	@BeforeClass
	public static void setUp() throws Exception {
		Configuration config = new PropertiesConfiguration("test.properties");
		searchService = new SearchService(config);
		
		JedisConnectionFactory.initialize(config);
		SocialAPIFactory.initialize(config);
		ContentAPIFactory.initialize(config);
		ServiceFactory.initialize(config);
		
		owner = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		ServiceFactory.getUserService().create(owner);
		
		searchService.deleteAll();
	}

	
	@Test
	public void testLiveSearchWithFacebook() {
		
		User user = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		user.getIdentities().add(new ExternalIdentity.Builder().user(user).accessToken("CAACEdEose0cBADv5biV5FN9Vqfxzg0l794hNmHvEvgWE9RtLXTZCalwK6wYGMjttqe8wuTkC5ZC7uIyzrdBTsLUxKiYxREoZCrqLTZAfdwJFv2hbOt8QTnno7t7tWYJIPWh1szP65gLjdxwUOJQUs5QScrWqnPidFDj6U0sZBKh3S9zeAVam0ND7zZAxwNg9RcrZBAI4NoZBqMjVezZB28vLv").clientPlatform(ClientPlatform.WEB).externalNetwork(ExternalNetwork.Facebook.ordinal()).build());
		List<Document> documents = searchService.searchLiveDocuments("Karate", user, ExternalNetwork.Facebook, 1);
		log.debug("documents: {}", documents);
		Assert.assertFalse(documents.isEmpty());
		
	}
	
	@Test
	public void testLiveSearchWithVimeo() {
		
		User user = TestUserFactory.createTestUserWithMinimumRequiredProperties();
		user.getIdentities().add(new ExternalIdentity.Builder().user(user).accessToken("a5f46897abbbd2b83501ea79b4916f44").clientPlatform(ClientPlatform.WEB).externalNetwork(ExternalNetwork.Vimeo.ordinal()).build());
		List<Document> documents = searchService.searchLiveDocuments("Karate", user, ExternalNetwork.Vimeo, 1);
		log.debug("documents: {}", documents);
		Assert.assertFalse(documents.isEmpty());
		
	}
	
	@Test 
	public void testLiveSearchWithYouTube() {
		User user = TestUserFactory.createTestUserWithMinimumRequiredProperties();
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
		Contact sender = TestContactFactory.createContactWithMininumRequiredFieldsAndSocialNetwork(owner, ExternalNetwork.Facebook);
		Message message = TestMessageFactory.createMessageWithMininumRequiredFields(owner, sender, ExternalNetwork.Facebook, UUID.randomUUID().toString());

		ServiceFactory.getSocialService().create(message);
		searchService.indexMessages(
				Arrays.asList(new Message[] { message }));

		// search by sender display name, making sure that only this entity shows up and it's of type "Message"
		List<Document> documents = searchService.searchIndexedDocuments(message.getTitle(), owner.getUserId());

		Assert.assertTrue(documents.size() == 1);
		Document result = documents.get(0);
		Assert.assertEquals(Message.class.getSimpleName(), result.getFields().get(SearchKeys.Fields.FIELD_DATA_TYPE));

		testUserAndSocialNetworkFilter(message.getSender().getDisplayName(), owner.getUserId(), ExternalNetwork.Facebook);


	}
	
	@Test
	public void testAddPublicActvityIsAvailableToAnyUser() {
		// create an activity with no owner
		Activity activity = TestActivityFactory.createActivityWithMininumRequirements(null, ExternalNetwork.LinkedIn);
		Contact postedBy = TestContactFactory.createContactWithMininumRequiredFieldsAndSocialNetwork(owner, ExternalNetwork.LinkedIn);
		activity.setPostedBy(postedBy);
		
		// save 
		searchService.indexActivities(null,
				Arrays.asList(new Activity[] { activity }));
		
		// search for this user; should return public even though the user fitler is set
		List<Document> documents = searchService.searchIndexedDocuments(activity.getTitle(), owner.getUserId());
		log.debug("documents: {}", documents);
		Assert.assertTrue(documents.size() == 1);
		
		
	}

	@Test
	public void testDedupe() {
		// build a video content with random strings so that it contains the same signature 
		VideoContent videoContent = TestVideoContentFactory.createVideoContentWithMininumRequiredFields(owner, ExternalNetwork.YouTube);
		ServiceFactory.getContentService().create(videoContent);
		// add 2
		searchService.indexVideos(null,
				Arrays.asList(new VideoContent[] { videoContent, videoContent }));

		// search, making sure that only this entity shows up and it's of type "VideoContent"
		List<Document> documents = searchService.searchIndexedDocuments(videoContent.getTitle(), null);
		log.debug("documents {}", documents);
		Assert.assertTrue(documents.size() == 1);

	}



	@Test
	public void testAddActivitiesReturnsInBasicSearch() {
		// build partial doc with the fields being indexed
		
		Activity activity = TestActivityFactory.createActivityWithMininumRequirements(owner, ExternalNetwork.LinkedIn);
		Contact postedBy = TestContactFactory.createContactWithMininumRequiredFieldsAndSocialNetwork(owner, ExternalNetwork.LinkedIn);
		activity.setPostedBy(postedBy);
		
		searchService.indexActivities(owner.getUserId(),
				Arrays.asList(new Activity[] { activity }));

		// search by sender display name, making sure that only this entity shows up and it's of type "Message"
		List<Document> documents = searchService.searchIndexedDocuments(activity.getTitle(), owner.getUserId());
		log.debug("documents: {}", documents);
		Assert.assertTrue(documents.size() == 1);
		Document result = documents.get(0);
		Assert.assertEquals(Activity.class.getSimpleName(), result.getFields().get(SearchKeys.Fields.FIELD_DATA_TYPE));

		testUserAndSocialNetworkFilter(activity.getPostedBy().getDisplayName(), owner.getUserId(), ExternalNetwork.LinkedIn);



	}

	@Test
	public void testAddVideoReturnsInBasicSearch() {
		// build partial doc with the fields being indexed
		VideoContent videoContent = TestVideoContentFactory.createVideoContentWithMininumRequiredFields(owner, ExternalNetwork.YouTube);
		ServiceFactory.getContentService().create(videoContent);

		
		searchService.indexVideos(owner.getUserId(),
				Arrays.asList(new VideoContent[] { videoContent }));

		// search, making sure that only this entity shows up and it's of type "VideoContent"
		List<Document> documents = searchService.searchIndexedDocuments(videoContent.getTitle(), owner.getUserId());

		Assert.assertTrue(documents.size() == 1);
		Document result = documents.get(0);
		Assert.assertEquals(VideoContent.class.getSimpleName(), result.getFields().get(SearchKeys.Fields.FIELD_DATA_TYPE));

		// test the user filter with a different user id
		documents = searchService.searchIndexedDocuments(videoContent.getTitle(), owner.getUserId() + 1);
		Assert.assertTrue(documents.isEmpty());

	}	

	/***
	 * Convenience method for testing if the social network and user id filters are filtering properly
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
		documents = searchService.searchIndexedDocuments(searchTerm, owner.getUserId(), anotherNetwork);
		Assert.assertTrue(documents.isEmpty());
		
		
	}



}
