package com.ubiquity.sprocket.network.api.random.generator;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.ubiquity.sprocket.network.api.dto.model.Activity;
import com.ubiquity.sprocket.network.api.dto.model.ActivityType;
import com.ubiquity.sprocket.network.api.dto.model.AudioTrack;
import com.ubiquity.sprocket.network.api.dto.model.Comment;
import com.ubiquity.sprocket.network.api.dto.model.Contact;
import com.ubiquity.sprocket.network.api.dto.model.Conversation;
import com.ubiquity.sprocket.network.api.dto.model.ExternalIdentity;
import com.ubiquity.sprocket.network.api.dto.model.Gender;
import com.ubiquity.sprocket.network.api.dto.model.Image;
import com.ubiquity.sprocket.network.api.dto.model.Message;
import com.ubiquity.sprocket.network.api.dto.model.Rating;
import com.ubiquity.sprocket.network.api.dto.model.Video;
import com.ubiquity.sprocket.network.api.dto.model.VideoContent;

public class RandomObjectGenerator {
	private static Random random = new Random();

	private static String generateIdentifier(Long userID,
			ResourceType resource, Long lastRequest, int index,
			Integer parentIndex) {
		if (parentIndex == null)
			return userID + "-" + resource.toString() + "-" + lastRequest + "-"
					+ convertToString(index);
		else
			return userID + "-" + resource.toString() + "-" + lastRequest + "-"
					+ convertToString(parentIndex) + "-"
					+ convertToString(index);
	}

	public static VideoContent generateVideoContent(Long userID,
			Long lastRequest, int index) {
		String vedioKey = generateIdentifier(userID, ResourceType.videos,
				lastRequest, index, null);
		return new VideoContent.Builder().title(UUID.randomUUID().toString())
				.categoryExternalIdentifier(UUID.randomUUID().toString())
				.video(GenerateVideo(vedioKey)).thumb(GeneratePhoto())
				.lastUpdated(System.currentTimeMillis())
				.publishedAt(System.currentTimeMillis())
				.description(UUID.randomUUID().toString()).build();
	}

	public static Conversation generateConvesationObject(Long userID,
			Long lastRequest, int index) {
		String conversationIdentifier = generateIdentifier(userID,
				ResourceType.conversation, lastRequest, index, null);
		Conversation conversation = new Conversation.Builder()
				.conversationIdentifier(conversationIdentifier).build();
		for (int i = 0; i < 10; i++) {
			conversation.getReceivers().add(generateContact(userID, index));
		}

		return conversation;
	}

	public static Message generateMessage(Long userID, Long lastRequest,
			int index) {
		return new Message.Builder().title(UUID.randomUUID().toString())
				.body(UUID.randomUUID().toString())
				.sentDate(System.currentTimeMillis())
				.lastUpdated(System.currentTimeMillis())
				.sender(generateContact(userID, index))
				.externalIdentifier(UUID.randomUUID().toString()).build();
	}

	public static Activity generateActivity(Long userID, Long lastRequest,
			int index, int activityType, boolean withComments, boolean withTags) {
		Activity.Builder activityBuilder = new Activity.Builder();
		activityBuilder
				.title(UUID.randomUUID().toString())
				.body(UUID.randomUUID().toString())
				.creationDate(System.currentTimeMillis())
				.postedBy(generateContact(userID, index))
				.rating(GenerateRating())
				.commentsNum(random.nextInt(300))
				.ownerVote(random.nextInt(3) - 1)
				.lastUpdated(System.currentTimeMillis())
				.externalIdentifier(
						generateIdentifier(userID, ResourceType.activities,
								lastRequest, index, null));

		switch (activityType) {
		case 0:
			activityBuilder.video(GenerateVideo(null))
					.activityType(ActivityType.VIDEO).image(GeneratePhoto());
			break;
		case 1:
			activityBuilder.image(GeneratePhoto()).activityType(
					ActivityType.PHOTO);
			break;
		case 2:
			activityBuilder.link(UUID.randomUUID().toString()).activityType(
					ActivityType.LINK);
			break;
		case 3:
			activityBuilder.activityType(ActivityType.STATUS);
			break;
		case 4:
			activityBuilder.audio(GenerateAudio()).activityType(
					ActivityType.AUDIO);
			break;
		case 5:
			activityBuilder.activityType(ActivityType.EMBEDEDHTML);
			break;
		}

		Activity activity = activityBuilder.build();
		if (withComments)
			activity.getComments().addAll(
					GenerateCommentList(userID, lastRequest, activity.getExternalIdentifier()));
		activity.getTags().addAll(GenerateTagList());
		return activity;
	}

	public static Contact generateContact(Long userId, Integer index) {
		ExternalIdentity externalIdentity = null;
		if (index == null) {
			externalIdentity = new ExternalIdentity.Builder().identifier(
					(userId + "")).build();
		} else {
			externalIdentity = new ExternalIdentity.Builder().identifier(
					(userId + index + random.nextInt(100)) + "").build();
		}
		int genderInt = random.nextInt(3);
		Gender gender = Gender.getGenderById(genderInt);
		Contact.Builder contactBuilder = new Contact.Builder();
		contactBuilder.externalIdentity(externalIdentity)
				.firstName(UUID.randomUUID().toString()).gender(gender)
				.lastName(UUID.randomUUID().toString())
				.displayName(UUID.randomUUID().toString())
				.lastUpdated(System.currentTimeMillis()).image(GeneratePhoto());

		return contactBuilder.build();
	}

	public static List<String> GenerateTagList() {
		List<String> tags = new LinkedList<String>();
		for (int i = 0; i < 3; i++) {
			int tag = random.nextInt(30) + 1;
			tags.add(convertToString(tag));
		}
		return tags;
	}

	public static List<Comment> GenerateCommentList(Long userID,
			Long lastRequest, String index) {
		int commentIndex = 1;
		List<Comment> comments = new LinkedList<Comment>();
		for (int i = 0; i < 4; i++) {
			Comment comment = GenerateComment(userID, lastRequest,
					index,commentIndex);
			commentIndex++;
			for (int j = 0; j < 2; j++) {
				Comment childReply = GenerateComment(userID, lastRequest,
						index,commentIndex);
				commentIndex++;
				childReply.addReply(GenerateComment(userID, lastRequest,
						index,commentIndex));
				commentIndex++;
				comment.addReply(childReply);
				commentIndex++;

			}
			comments.add(comment);
		}
		return comments;
	}

	public static Comment GenerateComment(Long userID, Long lastRequest,
			String activityIndex, int index) {
		Comment.Builder commentBuilder = new Comment.Builder();

		return commentBuilder
				.body(UUID.randomUUID().toString())
				.creationDate(System.currentTimeMillis())
				.rating(GenerateRating())
				.ownerVote(random.nextInt(3) - 1)
				.postedBy(generateContact(userID, index))
				.externalIdentifier(
						activityIndex + "-" + ResourceType.comment + "-"
								+ index).build();
	}

	public static Rating GenerateRating() {
		return new Rating.Builder().numRatings(random.nextInt(700)).build();
	}

	public static Video GenerateVideo(String itemKey) {
		return new Video.Builder().url(UUID.randomUUID().toString())
				.itemKey(itemKey).build();
	}

	public static Image GeneratePhoto() {
		return new Image.Builder().url(UUID.randomUUID().toString())
				.itemKey(UUID.randomUUID().toString()).build();
	}

	public static AudioTrack GenerateAudio() {
		return new AudioTrack.Builder().url(UUID.randomUUID().toString())
				.build();
	}

	private static String convertToString(int index) {
		if (index < 10)
			return "0" + index;
		else
			return "" + index;
	}

}
