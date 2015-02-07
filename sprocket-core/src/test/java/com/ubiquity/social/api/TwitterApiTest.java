package com.ubiquity.social.api;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.ubiquity.content.api.VimeoAPITest;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.integration.api.SocialAPI;
import com.ubiquity.integration.api.SocialAPIFactory;
import com.ubiquity.integration.domain.Activity;
import com.ubiquity.integration.domain.ActivityType;
import com.ubiquity.integration.domain.Contact;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.domain.Message;
import com.ubiquity.integration.domain.PostActivity;

public class TwitterApiTest {
	private static Logger log = LoggerFactory.getLogger(VimeoAPITest.class);

	private static ExternalIdentity identity;

	@BeforeClass
	public static void setUp() throws Exception {

		Configuration configuration = new PropertiesConfiguration(
				"test.properties");

		JedisConnectionFactory.initialize(configuration);

		SocialAPIFactory.initialize(configuration);
		User user = new User.Builder().lastUpdated(System.currentTimeMillis())
				.firstName(UUID.randomUUID().toString())
				.lastName(UUID.randomUUID().toString())
				.email(UUID.randomUUID().toString())
				.clientPlatform(ClientPlatform.Android)
				.displayName(UUID.randomUUID().toString()).build();

		identity = new ExternalIdentity.Builder()
				.accessToken(
						"2576165924-bjuqdtF54hoIw4fufobnX6O6DCaHfFhp4riitH1")
				.secretToken("8StcfxfvMzdyuUFRcmf7dtn9kI1VTAvFCoB0deZZy8qkW")
				.identifier("2576165924").user(user).build();
		log.debug("authenticated Twitter with identity {} ", identity);
	}

	@Test
	public void sendMessage() {

		SocialAPI twitterAPI = SocialAPIFactory.createProvider(
				ExternalNetwork.Twitter, ClientPlatform.WEB);
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH-mm-ss");
		Date date = new Date();
		String message = dateFormat.format(date);
		Contact c = new Contact.Builder()
				.externalIdentity(
						new ExternalIdentity.Builder()
								.externalNetwork(
										ExternalNetwork.Twitter.ordinal())
								.identifier("2576165924").build())
				.build();
		Boolean sent = twitterAPI.sendMessage(identity, c, null, message, "");
		Assert.assertTrue(sent);
	}

	@Test
	public void listMessages() {

		SocialAPI twitterAPI = SocialAPIFactory.createProvider(
				ExternalNetwork.Twitter, ClientPlatform.WEB);
		List<Message> messages = twitterAPI.listMessages(identity, null, null);
		Assert.assertFalse(messages.isEmpty());
		// all fb messages will have conversations
		for (Message message : messages) {
			log.debug("message {}", message);
			Assert.assertNotNull(message.getConversation()
					.getConversationIdentifier());
		}

	}

	@Test
	public void postTweet() {

		SocialAPI twitterAPI = SocialAPIFactory.createProvider(
				ExternalNetwork.Twitter, ClientPlatform.WEB);
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH-mm-ss");
		Date date = new Date();
		String message = dateFormat.format(date);

		PostActivity postActivity = new PostActivity.Builder()
				.activityTypeId(ActivityType.STATUS.ordinal()).body(message)
				.build();

		Boolean sent = twitterAPI.postActivity(identity, postActivity);
		Assert.assertTrue(sent);
	}

	@Test
	public void listActivities() {

		SocialAPI twitterAPI = SocialAPIFactory.createProvider(
				ExternalNetwork.Twitter, ClientPlatform.WEB);
		List<Activity> activities = twitterAPI.listActivities(identity);
		Assert.assertFalse(activities.isEmpty());
		// all fb messages will have conversations
		for (Activity activity : activities) {
			log.debug("activities {}", activities);
			Assert.assertNotNull(activity.getExternalIdentifier());
		}

	}

	@Test
	public void searchActivities() {

		SocialAPI twitterAPI = SocialAPIFactory.createProvider(
				ExternalNetwork.Twitter, ClientPlatform.WEB);
		List<Activity> activities = twitterAPI.searchActivities("test", 1, 5,
				identity);
		Assert.assertFalse(activities.isEmpty());
		// all fb messages will have conversations
		for (Activity activity : activities) {
			log.debug("activities {}", activities);
			Assert.assertNotNull(activity.getExternalIdentifier());
		}

	}

}
