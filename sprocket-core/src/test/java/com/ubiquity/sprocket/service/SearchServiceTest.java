package com.ubiquity.sprocket.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.niobium.repository.redis.JedisConnectionFactory;
import com.ubiquity.identity.domain.Application;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.Developer;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.ExternalNetworkApplication;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.factory.TestDeveloperFactory;
import com.ubiquity.identity.factory.TestUserFactory;
import com.ubiquity.identity.repository.DeveloperRepositoryJpaImpl;
import com.ubiquity.integration.api.ContentAPIFactory;
import com.ubiquity.integration.api.SocialAPIFactory;
import com.ubiquity.integration.domain.Activity;
import com.ubiquity.integration.domain.Contact;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.Message;
import com.ubiquity.integration.domain.VideoContent;
import com.ubiquity.integration.factory.TestActivityFactory;
import com.ubiquity.integration.factory.TestContactFactory;
import com.ubiquity.integration.factory.TestMessageFactory;
import com.ubiquity.integration.factory.TestVideoContentFactory;
import com.ubiquity.sprocket.domain.Document;
import com.ubiquity.sprocket.search.SearchKeys;

public class SearchServiceTest {

	private static SearchService searchService;
	private Logger log = LoggerFactory.getLogger(getClass());

	private static User owner;
	private static Application application;

	@BeforeClass
	public static void setUp() throws Exception {
		Configuration config = new PropertiesConfiguration("test.properties");
		Configuration errorsConfiguration = new PropertiesConfiguration(
				"messages.properties");

		JedisConnectionFactory.initialize(config);
		SocialAPIFactory.initialize(config);
		ContentAPIFactory.initialize(config);
		ServiceFactory.initialize(config, errorsConfiguration);
		searchService = ServiceFactory.getSearchService();

		owner = TestUserFactory
				.createTestUserWithMinimumRequiredProperties(null);
		ServiceFactory.getUserService().create(owner);

		searchService.deleteAll();
		
		Developer developer = TestDeveloperFactory
				.createTestDeveloperWithMinimumRequiredProperties();
		
		EntityManagerSupport.beginTransaction();
		new DeveloperRepositoryJpaImpl().create(developer);
		EntityManagerSupport.commit();
		
		application = ServiceFactory.getApplicationService()
				.createDefaultAppIFNotExsists(developer,UUID.randomUUID().toString(),UUID.randomUUID().toString());
		
		
	}

	@Test
	public void testLiveSearchWithFacebook() {

		User user = TestUserFactory
				.createTestUserWithMinimumRequiredProperties(null);
		user.getIdentities()
				.add(new ExternalIdentity.Builder()
						.user(user)
						.accessToken(
								"CAAIQjk0elpEBAHOw7lgI0DEXgr9WZATxvFqWIwZCdbq7PuxEkBD3fjLE1oP6Www1J8T21f1XITpNDijPu2xlaoh8AdviGZCLIIzp5eTmrfnuWcy6D1OKn6GRliPuJQZA5LmUxqTRthADMuhbGUXPiYllUgl09CUfevjSSMSphsyBwPFZCLy9fX8QFYYieYRUZD")
						.clientPlatform(ClientPlatform.WEB)
						.externalNetwork(ExternalNetwork.Facebook.ordinal())
						.build());

		
		ExternalNetworkApplication externalNetworkApplication = ServiceFactory
				.getApplicationService()
				.getExAppByAppIdAndExternalNetworkAndClientPlatform(application.getAppId(),
						ExternalNetwork.Facebook.ordinal(),
						ClientPlatform.Android);
		List<Document> documents = searchService.searchLiveDocuments("Karate",
				user, ExternalNetwork.Facebook, 1, null, null, null,
				externalNetworkApplication);
		log.debug("documents: {}", documents);
		Assert.assertFalse(documents.isEmpty());

	}

	@Test
	public void testLiveSearchWithVimeo() {

		User user = TestUserFactory
				.createTestUserWithMinimumRequiredProperties(null);
		user.getIdentities().add(
				new ExternalIdentity.Builder().user(user)
						.accessToken("a5f46897abbbd2b83501ea79b4916f44")
						.clientPlatform(ClientPlatform.WEB)
						.externalNetwork(ExternalNetwork.Vimeo.ordinal())
						.build());

		ExternalNetworkApplication externalNetworkApplication = ServiceFactory
				.getApplicationService()
				.getExAppByAppIdAndExternalNetworkAndClientPlatform(application.getAppId(),
						ExternalNetwork.Vimeo.ordinal(), ClientPlatform.Android);
		List<Document> documents = searchService.searchLiveDocuments("Karate",
				user, ExternalNetwork.Vimeo, 1, null, null, null,
				externalNetworkApplication);
		log.debug("documents: {}", documents);
		Assert.assertFalse(documents.isEmpty());

	}

	@Test
	public void testLiveSearchWithYouTube() {
		User user = TestUserFactory
				.createTestUserWithMinimumRequiredProperties(null);
		user.getIdentities()
				.add(new ExternalIdentity.Builder()
						.user(user)
						.accessToken(
								"ya29.YADq4neRcxr8kiIAAABXuPKmNyMLWRUfEOtm3zfio0Ua5xIuW9sho7Mbms1nCTs96nsseog3eWo7vq2sdLw")
						.clientPlatform(ClientPlatform.Android)
						.externalNetwork(ExternalNetwork.YouTube.ordinal())
						.build());
		
		ExternalNetworkApplication externalNetworkApplication = ServiceFactory
				.getApplicationService()
				.getExAppByAppIdAndExternalNetworkAndClientPlatform(application.getAppId(),
						ExternalNetwork.Vimeo.ordinal(), ClientPlatform.Android);
		List<Document> documents = searchService.searchLiveDocuments("Karate",
				user, ExternalNetwork.YouTube, 1, null, null, null,
				externalNetworkApplication);
		log.debug("documents: {}", documents);
		Assert.assertFalse(documents.isEmpty());
	}

	@Test
	public void testLiveSearchWithLinkedIn() {

	}

	@Test
	public void testLiveSearchWithTwitter() {

	}

	@Ignore
	@Test
	public void testAddMessagesReturnsInBasicSearch() {
		// build partial doc with the fields being indexed
		Contact sender = TestContactFactory
				.createContactWithMininumRequiredFieldsAndExternalNetwork(
						owner, ExternalNetwork.Facebook);
		Message message = TestMessageFactory
				.createMessageWithMininumRequiredFields(owner, sender,
						ExternalNetwork.Facebook, UUID.randomUUID().toString());

		// ServiceFactory.getSocialService().create(message);
		searchService.indexMessages(message.getOwner().getUserId(),
				Arrays.asList(new Message[] { message }));

		// search by sender display name, making sure that only this entity
		// shows up and it's of type "Message"
		List<Document> documents = searchService.searchIndexedDocuments(
				message.getTitle(), owner.getUserId());

		Assert.assertTrue(documents.size() == 1);
		Document result = documents.get(0);
		Assert.assertEquals(Message.class.getSimpleName(), result.getFields()
				.get(SearchKeys.Fields.FIELD_DATA_TYPE));

	}

	@Test
	public void testAddPublicVideoUserFilter() {
		// create an activity with no owner
		VideoContent videoContent = TestVideoContentFactory
				.createVideoContentWithMininumRequiredFields(null,
						ExternalNetwork.YouTube);
		ServiceFactory.getContentService().create(videoContent);

		// save with no use filter
		searchService.indexVideos(null,
				Arrays.asList(new VideoContent[] { videoContent }), true);

		// search for this user; should return public even though the user
		// fitler is set
		List<Document> documents = searchService.searchIndexedDocuments(
				videoContent.getDescription(), null, ExternalNetwork.YouTube);
		log.debug("documents: {}", documents);
		Assert.assertTrue(documents.size() == 1);

		// save with user filter
		searchService.indexVideos(owner.getUserId(),
				Arrays.asList(new VideoContent[] { videoContent }), true);

		// search for this user; should return when searching with this user
		// filter specified
		documents = searchService.searchIndexedDocuments(
				videoContent.getDescription(), owner.getUserId(),
				ExternalNetwork.YouTube);
		log.debug("documents: {}", documents);
		Assert.assertTrue(documents.size() == 1);

		// search for this user; should not return when a different user is
		// specified
		documents = searchService.searchIndexedDocuments(
				videoContent.getDescription(), owner.getUserId() + 1,
				ExternalNetwork.YouTube);
		log.debug("documents: {}", documents);
		Assert.assertTrue(documents.isEmpty());

	}

	@Test
	public void testAddPublicActivityUserFilter() {
		Activity activity = TestActivityFactory
				.createActivityWithMininumRequirements(null,
						ExternalNetwork.Facebook);
		Contact postedBy = TestContactFactory
				.createContactWithMininumRequiredFieldsAndExternalNetwork(
						owner, ExternalNetwork.Facebook);
		activity.setPostedBy(postedBy);

		searchService.indexActivities(null,
				Arrays.asList(new Activity[] { activity }), true);

		// search by sender display name, making sure that only this entity
		// shows up and it's of type "Message"
		List<Document> documents = searchService.searchIndexedDocuments(
				activity.getTitle(), null, ExternalNetwork.Facebook);
		log.debug("documents: {}", documents);
		Assert.assertEquals(1, documents.size());

		documents = searchService.searchIndexedDocuments(activity.getTitle(),
				owner.getUserId(), ExternalNetwork.Facebook);
		log.debug("documents: {}", documents);
		Assert.assertTrue(documents.isEmpty());

	}

	@Test
	public void testDedupe() {
		// build a video content with random strings so that it contains the
		// same signature
		VideoContent videoContent = TestVideoContentFactory
				.createVideoContentWithMininumRequiredFields(owner,
						ExternalNetwork.YouTube);
		ServiceFactory.getContentService().create(videoContent);
		// add 2
		searchService.indexVideos(null, Arrays.asList(new VideoContent[] {
				videoContent, videoContent }), false);

		// search, making sure that only this entity shows up and it's of type
		// "VideoContent"
		List<Document> documents = searchService.searchIndexedDocuments(
				videoContent.getTitle(), null);
		log.debug("documents {}", documents);
		Assert.assertEquals(1, documents.size());

	}

	@Test
	public void testAddActivitiesReturnsInBasicSearch() {
		// build partial doc with the fields being indexed

		Activity activity = TestActivityFactory
				.createActivityWithMininumRequirements(owner,
						ExternalNetwork.LinkedIn);
		Contact postedBy = TestContactFactory
				.createContactWithMininumRequiredFieldsAndExternalNetwork(
						owner, ExternalNetwork.LinkedIn);
		activity.setPostedBy(postedBy);

		searchService.indexActivities(owner.getUserId(),
				Arrays.asList(new Activity[] { activity }), true);

		// search by sender display name, making sure that only this entity
		// shows up and it's of type "Message"
		List<Document> documents = searchService.searchIndexedDocuments(
				activity.getTitle(), owner.getUserId());
		log.debug("documents: {}", documents);
		Assert.assertEquals(1, documents.size());
		Document result = documents.get(0);

		Assert.assertEquals(Activity.class.getSimpleName(), result.getFields()
				.get(SearchKeys.Fields.FIELD_DATA_TYPE));

		Assert.assertEquals(activity.getTitle(),
				result.getFields().get(SearchKeys.Fields.FIELD_TITLE));
		Assert.assertEquals(activity.getBody(),
				result.getFields().get(SearchKeys.Fields.FIELD_BODY));

	}

	@Test
	public void testAddVideoReturnsInBasicSearch() {
		// build partial doc with the fields being indexed
		VideoContent videoContent = TestVideoContentFactory
				.createVideoContentWithMininumRequiredFields(owner,
						ExternalNetwork.YouTube);
		ServiceFactory.getContentService().create(videoContent);

		searchService.indexVideos(owner.getUserId(),
				Arrays.asList(new VideoContent[] { videoContent }), false);

		// search, making sure that only this entity shows up and it's of type
		// "VideoContent"
		List<Document> documents = searchService.searchIndexedDocuments(
				videoContent.getTitle(), owner.getUserId());

		Assert.assertEquals(1, documents.size());
		Document result = documents.get(0);
		Assert.assertEquals(VideoContent.class.getSimpleName(), result
				.getFields().get(SearchKeys.Fields.FIELD_DATA_TYPE));

		// test the user filter with a different user id
		documents = searchService.searchIndexedDocuments(
				videoContent.getTitle(), owner.getUserId() + 1);
		Assert.assertTrue(documents.isEmpty());

	}

}
