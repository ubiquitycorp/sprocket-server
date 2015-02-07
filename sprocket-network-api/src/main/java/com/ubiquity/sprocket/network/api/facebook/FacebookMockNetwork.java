package com.ubiquity.sprocket.network.api.facebook;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.niobium.common.serialize.JsonConverter;
import com.ubiquity.sprocket.network.api.dto.model.Activity;
import com.ubiquity.sprocket.network.api.dto.model.Contact;
import com.ubiquity.sprocket.network.api.dto.model.Conversation;
import com.ubiquity.sprocket.network.api.facebook.dto.container.FacebookDataDto;
import com.ubiquity.sprocket.network.api.facebook.dto.model.FacebookBatchResponseDto;
import com.ubiquity.sprocket.network.api.facebook.dto.model.FacebookContactDto;
import com.ubiquity.sprocket.network.api.facebook.dto.model.FacebookPageDto;
import com.ubiquity.sprocket.network.api.random.generator.RandomListGenerator;
import com.ubiquity.sprocket.network.api.random.generator.RandomObjectGenerator;

public class FacebookMockNetwork {
	private static Random random = new Random();
	private static JsonConverter jsonConverter = JsonConverter.getInstance();

	public static FacebookContactDto Authenticate(Long userId) {
		Contact contact = RandomObjectGenerator.generateContact(userId, null);
		return FacebookGraphApiDtoAssembler.assembleContact(contact);
	}

	public static FacebookDataDto getNewsFeed(Long userId, Long lastRequest,
			Long thisRequest, Integer maxResults) {
		List<Activity> activities = RandomListGenerator.GenerateActivityList(
				userId, lastRequest, thisRequest, false, false, maxResults);
		FacebookDataDto facebookData = new FacebookDataDto();
		for (Activity activity : activities)
			facebookData.getData().add(
					FacebookGraphApiDtoAssembler.assembleActivity(activity));

		return facebookData;
	}

	public static FacebookDataDto getInbox(Long userId, Long lastRequest,
			Long thisRequest) {
		List<Conversation> conversations = RandomListGenerator
				.GenerateConverstationList(userId, lastRequest, thisRequest);
		FacebookDataDto facebookData = new FacebookDataDto();
		for (Conversation conversation : conversations)
			facebookData.getData().add(
					FacebookGraphApiDtoAssembler
							.assembleConversation(conversation));

		return facebookData;
	}
	
	public static FacebookDataDto getFriends(Long userId)
	{
		List<Contact> contacts = RandomListGenerator.GenerateContactList(userId);
		FacebookDataDto facebookData = new FacebookDataDto();
		for (Contact contact : contacts)
			facebookData.getData().add(
					FacebookGraphApiDtoAssembler.assembleContact(contact));

		return facebookData;
	}

	public static List<FacebookBatchResponseDto> getbatchActivity(Long userId,
			Long lastRequest, Long thisRequest, Integer maxResults) {
		List<FacebookBatchResponseDto> facebookData = new LinkedList<FacebookBatchResponseDto>();
		for (int i = 1; i < maxResults; i++)
			facebookData.add(generateBatchResponse(userId, lastRequest, thisRequest, 2));

		return facebookData;
	}

	public static FacebookDataDto search(int size) {
		FacebookDataDto facebookData = new FacebookDataDto();
		for (int i = 0; i < size; i++)
			facebookData.getData().add(genertatePageDto());

		return facebookData;
	}

	private static FacebookPageDto genertatePageDto() {
		return new FacebookPageDto.Builder().can_post(random.nextBoolean())
				.id(UUID.randomUUID().toString()).likes(random.nextLong())
				.category(UUID.randomUUID().toString()).build();
		
	}

	private static FacebookBatchResponseDto generateBatchResponse(Long userId, Long lastRequest , Long thisRequest,Integer maxResults){
		FacebookDataDto facebookData = getNewsFeed(userId, lastRequest, thisRequest, maxResults);
		String body = jsonConverter.convertToPayload(facebookData);
		FacebookBatchResponseDto facebookBatchResponseDto = new FacebookBatchResponseDto();
		facebookBatchResponseDto.setCode(200);
		facebookBatchResponseDto.setBody(body);
		
		return facebookBatchResponseDto;
	}
}
