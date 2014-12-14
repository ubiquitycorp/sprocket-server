package com.ubiquity.sprocket.network.api.facebook;

import java.util.List;

import com.ubiquity.sprocket.network.api.dto.model.Activity;
import com.ubiquity.sprocket.network.api.dto.model.Contact;
import com.ubiquity.sprocket.network.api.dto.model.Conversation;
import com.ubiquity.sprocket.network.api.facebook.dto.container.FacebookDataDto;
import com.ubiquity.sprocket.network.api.facebook.dto.model.FacebookContactDto;
import com.ubiquity.sprocket.network.api.random.generator.RandomListGenerator;
import com.ubiquity.sprocket.network.api.random.generator.RandomObjectGenerator;

public class FacebookMockNetwork {
	
	public static FacebookContactDto Authenticate(Long userId){
		Contact contact = RandomObjectGenerator.generateContact(userId, null);
		return FacebookGraphApiDtoAssembler.assembleContact(contact);
	}
	
	public static FacebookDataDto getNewsFeed(Long userId, Long lastRequest , Long thisRequest){
		List<Activity>  activities = RandomListGenerator.GenerateActivityList(userId, lastRequest ,thisRequest,false,false);
		FacebookDataDto facebookData = new FacebookDataDto();
		for (Activity activity : activities)
			facebookData.getData().add(FacebookGraphApiDtoAssembler.assembleActivity(activity));
		
		return facebookData;
	}
	
	public static FacebookDataDto getInbox(Long userId, Long lastRequest , Long thisRequest){
		List<Conversation>  conversations = RandomListGenerator.GenerateConverstationList(userId, lastRequest ,thisRequest);
		FacebookDataDto facebookData = new FacebookDataDto();
		for (Conversation conversation : conversations)
			facebookData.getData().add(FacebookGraphApiDtoAssembler.assembleConversation(conversation));
		
		return facebookData;
	}
	
}
