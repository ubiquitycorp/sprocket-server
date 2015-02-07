package com.ubiquity.sprocket.network.api.reddit;

import java.util.UUID;

import com.ubiquity.sprocket.network.api.dto.model.Activity;
import com.ubiquity.sprocket.network.api.dto.model.ActivityType;
import com.ubiquity.sprocket.network.api.dto.model.Comment;
import com.ubiquity.sprocket.network.api.dto.model.Contact;
import com.ubiquity.sprocket.network.api.reddit.dto.model.RedditCommentDataDto;
import com.ubiquity.sprocket.network.api.reddit.dto.model.RedditCommentDto;
import com.ubiquity.sprocket.network.api.reddit.dto.model.RedditContactDto;
import com.ubiquity.sprocket.network.api.reddit.dto.model.RedditMediaEmbedDto;
import com.ubiquity.sprocket.network.api.reddit.dto.model.RedditPostDataDto;
import com.ubiquity.sprocket.network.api.reddit.dto.model.RedditPostDto;

public class RedditGraphApiDtoAssembler {
	/***
	 * Assembles a contact and sets the user of the identity parameter as the
	 * owner. It also sets the Identifier and social provider on the passed in
	 * identity reference.
	 * 
	 * @param identity
	 * @param result
	 * @return
	 * 
	 * @throws IllegalArgumentException
	 *             if user property on identity is null
	 */
	public static RedditContactDto assembleContact(Contact contact) {
		return new RedditContactDto.Builder().name(
				contact.getExternalIdentity().getIdentifier()).build();
	}

	public static RedditPostDataDto assembleActivity(Activity activity) {
		RedditPostDto.Builder redditPostDto = new RedditPostDto.Builder();
		redditPostDto
				.author(activity.getPostedBy().getExternalIdentity()
						.getIdentifier())
				.name(activity.getExternalIdentifier())
				.createdUtc(activity.getCreationDate() / 1000)
				.title(activity.getTitle())
				.commentsNum(activity.getCommentsNum())
				.score(activity.getRating().getNumRatings())
				.id(activity.getExternalIdentifier());
		if (activity.getOwnerVote() == -1)
			redditPostDto.likes(false);
		else if (activity.getOwnerVote() == 1)
			redditPostDto.likes(true);
		RedditMediaEmbedDto mediaEmbed = new RedditMediaEmbedDto();
		RedditMediaEmbedDto secuermedisEmbed = new RedditMediaEmbedDto();
		// check in activity type
		if (activity.getActivityType().equals(ActivityType.EMBEDEDHTML)) {
			
			mediaEmbed.setContent("<iframe src=\"" + activity.getBody()
					+ "\" ></iframe>");
			redditPostDto.secureMediaEmbed(secuermedisEmbed);

		} else if (activity.getActivityType().equals(ActivityType.STATUS)) {
			// Status
			redditPostDto.selfTextHtml(activity.getBody());
		} else if (activity.getActivityType().equals(ActivityType.PHOTO)) {
			redditPostDto.url(activity.getImage().getUrl() + ".jpg");
		} else if (activity.getActivityType().equals(ActivityType.VIDEO)){
			redditPostDto.url(activity.getVideo().getUrl());
		} else if (activity.getActivityType().equals(ActivityType.LINK)){
			redditPostDto.url(activity.getLink());
		} else if(activity.getActivityType().equals(ActivityType.AUDIO)){
			redditPostDto.url(activity.getAudio().getUrl());
		}
		redditPostDto.mediaEmbed(mediaEmbed);
		redditPostDto.subreddit(UUID.randomUUID().toString());
		return new RedditPostDataDto.Builder().data(redditPostDto.build())
				.build();
	}
	//
	public static RedditCommentDataDto assembleComment(Comment comment) {
		RedditCommentDataDto.Builder redditCommentDataDtoBuilder = new RedditCommentDataDto.Builder();
		redditCommentDataDtoBuilder.kind("comment");
		RedditCommentDto.Builder redditCommentDtoBuilder = new RedditCommentDto.Builder(); 
		//RedditCommentDto redditCommentDto = redditCommentDataDto.getData();
		redditCommentDtoBuilder.author(comment.getPostedBy().getExternalIdentity()
				.getIdentifier())
				.name(comment.getExternalIdentifier())
				.createdUtc(comment.getCreationDate() / 1000)
				.bodyHtml(comment.getBody())
				.score(comment.getRating().getNumRatings())
				.id(comment.getExternalIdentifier());
		if (comment.getOwnerVote() == -1)
			redditCommentDtoBuilder.likes(false);
		else if (comment.getOwnerVote() == 1)
			redditCommentDtoBuilder.likes(true);
		
		redditCommentDataDtoBuilder.data(redditCommentDtoBuilder.build());
		return redditCommentDataDtoBuilder.build();
	}
	//
	// public static Captcha assembleCaptcha(byte[] inputstreamImage,
	// String iden) {
	// return new
	// Captcha.Builder().image(inputstreamImage).identifier(iden).imageType("png").build();
	// }
}
