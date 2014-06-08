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

import com.ubiquity.identity.domain.User;
import com.ubiquity.media.domain.Image;
import com.ubiquity.media.domain.Video;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.Message;
import com.ubiquity.social.domain.SocialNetwork;
import com.ubiquity.sprocket.domain.Document;
import com.ubiquity.sprocket.domain.VideoContent;
import com.ubiquity.sprocket.search.SearchKeys;


public class SearchServiceTest {

	private static SearchService searchService;

	private Logger log = LoggerFactory.getLogger(getClass());
	
	@BeforeClass
	public static void setUp() throws Exception {
		Configuration config = new PropertiesConfiguration("test.properties");
		searchService = new SearchService(config);
		
		searchService.deleteAll();
	}


	@Test
	public void testAddMessagesReturnsInBasicSearch() {
		// build partial doc with the fields being indexed
		Message message = new Message.Builder()
		.messageId(new java.util.Random().nextLong())
		.owner(new User.Builder().userId(1l).build())
		.body(UUID.randomUUID().toString())
		.title(UUID.randomUUID().toString()).sender(
				new Contact.Builder().displayName("Jack").build())
				.socialNetwork(SocialNetwork.Facebook)
				.build();
		searchService.indexMessages(
				Arrays.asList(new Message[] { message }));

		// search by sender display name, making sure that only this entity shows up and it's of type "Message"
		List<Document> documents = searchService.searchDocuments("Jack", 1l);

		Assert.assertTrue(documents.size() == 1);
		Document result = documents.get(0);
		Assert.assertEquals(Message.class.getSimpleName(), result.getFields().get(SearchKeys.CommonFields.FIELD_DATA_TYPE));

		// test the user filter
		documents = searchService.searchDocuments(message.getSender().getDisplayName(), 2l);
		Assert.assertTrue(documents.isEmpty());

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
		List<Document> documents = searchService.searchDocuments(videoContent.getTitle(), 1l);
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
		.title(UUID.randomUUID().toString()).postedBy(
				new Contact.Builder().displayName("Bob").build())
				.build();
		searchService.indexActivities(
				Arrays.asList(new Activity[] { activity }));

		// search by sender display name, making sure that only this entity shows up and it's of type "Message"
		List<Document> documents = searchService.searchDocuments("Bob", 1l);
		log.debug("documents: {}", documents);
		Assert.assertTrue(documents.size() == 1);
		Document result = documents.get(0);
		Assert.assertEquals(Activity.class.getSimpleName(), result.getFields().get(SearchKeys.CommonFields.FIELD_DATA_TYPE));

		// test the user filter
		documents = searchService.searchDocuments(activity.getPostedBy().getDisplayName(), 2l);
		Assert.assertTrue(documents.isEmpty());

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
		List<Document> documents = searchService.searchDocuments("video", 1l);

		Assert.assertTrue(documents.size() == 1);
		Document result = documents.get(0);
		Assert.assertEquals(VideoContent.class.getSimpleName(), result.getFields().get(SearchKeys.CommonFields.FIELD_DATA_TYPE));

		// test the user filter
		documents = searchService.searchDocuments(videoContent.getTitle(), 2l);
		Assert.assertTrue(documents.isEmpty());
		
		

	}	


}
