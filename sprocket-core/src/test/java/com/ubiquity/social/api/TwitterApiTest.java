package com.ubiquity.social.api;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.content.api.VimeoAPITest;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.social.domain.ActivityType;
import com.ubiquity.social.domain.Message;
import com.ubiquity.social.domain.PostActivity;

@Ignore
public class TwitterApiTest {
private static Logger log = LoggerFactory.getLogger(VimeoAPITest.class);
	
	private static ExternalIdentity identity;
	
	@BeforeClass
	public static void setUp() throws Exception {
//		identity = ServiceFactory.getExternalIdentityService().findExternalIdentity((long)1, ExternalNetwork.Twitter);
//		log.debug("authenticated Twitter with identity {} ", identity);
		User user = new User.Builder()
		.lastUpdated(System.currentTimeMillis())
		.firstName(UUID.randomUUID().toString())
		.lastName(UUID.randomUUID().toString())
		.email(UUID.randomUUID().toString())
		.clientPlatform(ClientPlatform.Android)
		.displayName(UUID.randomUUID().toString())
		.build();
		
		identity = new ExternalIdentity.Builder()
				.accessToken("2576165924-bjuqdtF54hoIw4fufobnX6O6DCaHfFhp4riitH1")
				.secretToken("8StcfxfvMzdyuUFRcmf7dtn9kI1VTAvFCoB0deZZy8qkW")
				.identifier("2576165924")
				.user(user)
				.build();
		log.debug("authenticated Twitter with identity {} ", identity);
	}
	
	@Test
	public void sendMessage() {
		
		SocialAPI twitterAPI = SocialAPIFactory.createProvider(ExternalNetwork.Twitter, ClientPlatform.WEB);
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH-mm-ss");
		Date date = new Date();
		String message = dateFormat.format(date);
		message = message.replaceAll(" ", "%20");
		Boolean sent = twitterAPI.sendMessage(identity, null, "engminashafik", message, "");
		Assert.assertTrue(sent);
	}
	@Test
	public void listMessages() {
		
		SocialAPI twitterAPI = SocialAPIFactory.createProvider(ExternalNetwork.Twitter, ClientPlatform.WEB);
		List<Message> messages = twitterAPI.listMessages(identity);
		Assert.assertFalse(messages.isEmpty());
		// all fb messages will have conversations
		for(Message message : messages) {
			log.debug("message {}", message);
			Assert.assertNotNull(message.getConversationIdentifier());
		}
		
	}
	@Test
	public void postTweet() {
		
		SocialAPI twitterAPI = SocialAPIFactory.createProvider(ExternalNetwork.Twitter, ClientPlatform.WEB);
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH-mm-ss");
		Date date = new Date();
		String message = dateFormat.format(date);
		message = message.replaceAll(" ", "%20");
		PostActivity postActivity = new PostActivity.Builder()
									.activityTypeId(ActivityType.STATUS.ordinal())
									.body(message).build();
		
		Boolean sent = twitterAPI.postActivity(identity, postActivity);
		Assert.assertTrue(sent);
	}
	@Test
	public void listActivities() {
		
		SocialAPI twitterAPI = SocialAPIFactory.createProvider(ExternalNetwork.Twitter, ClientPlatform.WEB);
		List<Activity> activities = twitterAPI.listActivities(identity);
		Assert.assertFalse(activities.isEmpty());
		// all fb messages will have conversations
		for(Activity activity : activities) {
			log.debug("message {}", activities);
			Assert.assertNotNull(activity.getExternalIdentifier());
		}
		
	}
}
