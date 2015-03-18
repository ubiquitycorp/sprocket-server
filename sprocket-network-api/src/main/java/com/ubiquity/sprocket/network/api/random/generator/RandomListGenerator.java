package com.ubiquity.sprocket.network.api.random.generator;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.ubiquity.sprocket.network.api.cache.CacheFactory;
import com.ubiquity.sprocket.network.api.dto.model.Activity;
import com.ubiquity.sprocket.network.api.dto.model.ActivityType;
import com.ubiquity.sprocket.network.api.dto.model.Comment;
import com.ubiquity.sprocket.network.api.dto.model.Contact;
import com.ubiquity.sprocket.network.api.dto.model.Conversation;
import com.ubiquity.sprocket.network.api.dto.model.VideoContent;

public class RandomListGenerator {
	private static Random random = new Random();

	public static List<Activity> GenerateActivityList(Long userId,
			Long lastRequest, Long thisRequest, boolean withComments,
			boolean withTags, Integer maxResults) {
		List<Activity> activities = new LinkedList<Activity>();
		int mid = (maxResults / 2) + 1;
		for (int i = 1; i <= mid; i++) {
			activities.add(RandomObjectGenerator.generateActivity(userId,
					lastRequest, i, random.nextInt(6), withComments, withTags, null));
		}
		for (int i = mid; i <= maxResults; i++) {
			activities.add(RandomObjectGenerator.generateActivity(userId,
					thisRequest, i, random.nextInt(6), withComments, withTags, null));
		}
		String activityBody = CacheFactory.getLastActivityBody(userId);
		if (activityBody != null)
		{
			activities.add(RandomObjectGenerator.generateActivity(userId,
					thisRequest, maxResults + 1, ActivityType.STATUS.ordinal(), withComments, withTags, activityBody));
		}
		return activities;
	}

	public static List<VideoContent> GenerateVideoList(Long userId,
			Long lastRequest, Long thisRequest, Integer maxResults) {
		int mid = (maxResults / 2) + 1;
		List<VideoContent> videos = new LinkedList<VideoContent>();
		for (int i = 1; i <= mid; i++) {
			videos.add(RandomObjectGenerator.generateVideoContent(userId,
					lastRequest, i));
		}
		for (int i = mid + 1; i <= maxResults; i++) {
			videos.add(RandomObjectGenerator.generateVideoContent(userId,
					thisRequest, i));
		}
		return videos;
	}

	public static List<Conversation> GenerateConverstationList(Long userId,
			Long lastRequest, Long thisRequest) {
		List<Conversation> conversations = new LinkedList<Conversation>();
		int index = 1;
		for (int i = 1; i <= 10; i++) {
			Conversation conversation = RandomObjectGenerator
					.generateConvesationObject(userId, lastRequest, i);
			for (int j = 1; j <= 25; j++)
				conversation.getMessages().add(
						RandomObjectGenerator.generateMessage(userId,
								lastRequest, index++));
			conversations.add(conversation);
		}
		for (int i = 1; i <= 10; i++) {
			Conversation conversation = RandomObjectGenerator
					.generateConvesationObject(userId, lastRequest, i);
			for (int j = 1; j <= 25; j++)
				conversation.getMessages().add(
						RandomObjectGenerator.generateMessage(userId,
								thisRequest, index++));
			conversations.add(conversation);
		}
		return conversations;
	}

	public static List<Comment> GenerateCommentList(Long userId,
			Long thisRequest, String ActivityId) {

		return RandomObjectGenerator.GenerateCommentList(userId, thisRequest,
				ActivityId);
	}

	public static List<Contact> GenerateContactList(Long userId) {

		return RandomObjectGenerator.generateContactList(userId);
	}
}
