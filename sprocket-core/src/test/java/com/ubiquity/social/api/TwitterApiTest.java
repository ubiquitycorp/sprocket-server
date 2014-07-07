package com.ubiquity.social.api;

import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiquity.content.api.VimeoAPITest;
import com.ubiquity.identity.domain.ClientPlatform;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.social.domain.Message;
import com.ubiquity.social.domain.SocialNetwork;

public class TwitterApiTest {
private static Logger log = LoggerFactory.getLogger(VimeoAPITest.class);
	
	private static ExternalIdentity identity;
	
	@BeforeClass
	public static void setUp() throws Exception {
		identity = new ExternalIdentity.Builder()
				.accessToken("2576165924-bjuqdtF54hoIw4fufobnX6O6DCaHfFhp4riitH1")
				.secretToken("8StcfxfvMzdyuUFRcmf7dtn9kI1VTAvFCoB0deZZy8qkW")
				.build();
		log.debug("authenticated Twitter with identity {} ", identity);
	}
	
	@Test
	public void sendMessage() {
		
		SocialAPI twitterAPI = SocialAPIFactory.createProvider(SocialNetwork.Twitter, ClientPlatform.WEB);
		Boolean sent = twitterAPI.sendMessage(identity, null, "engminashafik", "test");
		Assert.assertTrue(sent);
	}
}
