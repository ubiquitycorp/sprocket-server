package com.ubiquity.sprocket.network.api.reddit;

import java.util.LinkedList;
import java.util.List;

import com.ubiquity.sprocket.network.api.dto.model.Activity;
import com.ubiquity.sprocket.network.api.dto.model.Comment;
import com.ubiquity.sprocket.network.api.dto.model.Contact;
import com.ubiquity.sprocket.network.api.random.generator.RandomListGenerator;
import com.ubiquity.sprocket.network.api.random.generator.RandomObjectGenerator;
import com.ubiquity.sprocket.network.api.reddit.dto.container.RedditCommentDataContainerDto;
import com.ubiquity.sprocket.network.api.reddit.dto.container.RedditPostDataContainerDto;
import com.ubiquity.sprocket.network.api.reddit.dto.model.RedditCommentDataDto;
import com.ubiquity.sprocket.network.api.reddit.dto.model.RedditContactDto;

public class RedditMockNetwork {
	public static RedditContactDto Authenticate(Long userId) {
		Contact contact = RandomObjectGenerator.generateContact(userId, null);
		return RedditGraphApiDtoAssembler.assembleContact(contact);
	}

	public static RedditPostDataContainerDto getHotPosts(Long userId,
			Long lastRequest, Long thisRequest, int maxResults) {
		RedditPostDataContainerDto redditPostDataContainerDto = new RedditPostDataContainerDto();
		List<Activity> activities = RandomListGenerator.GenerateActivityList(
				userId, lastRequest, thisRequest, false, false, maxResults);
		for (Activity activity : activities){
			redditPostDataContainerDto.getData().getChildren().add(RedditGraphApiDtoAssembler.assembleActivity(activity));
		}
		return redditPostDataContainerDto;
	}
	
	public static List<RedditCommentDataContainerDto> getComments(Long userId,
			Long lastRequest, Long thisRequest, int maxResults,String article) {
		List<RedditCommentDataContainerDto> redditCommentDataContainerDtos = new LinkedList<RedditCommentDataContainerDto>();
		redditCommentDataContainerDtos.add(new RedditCommentDataContainerDto());
		RedditCommentDataContainerDto redditCommentDataContainerDto = new RedditCommentDataContainerDto();
		List<Comment> comments = RandomListGenerator.GenerateCommentList(userId, lastRequest, article);
		for (Comment comment : comments){
			redditCommentDataContainerDto.getData().getChildren().add(assembleComment(comment));
		}
		redditCommentDataContainerDtos.add(redditCommentDataContainerDto);
		return redditCommentDataContainerDtos;
	}

	private static RedditCommentDataDto assembleComment(Comment comment){
		RedditCommentDataDto redditCommentDataDto = RedditGraphApiDtoAssembler.assembleComment(comment);
		if(comment.getReplies() != null && comment.getReplies().size()>0){
			RedditCommentDataContainerDto redditCommentDataContainerDto = new RedditCommentDataContainerDto(); 
			for(Comment reply : comment.getReplies())
			{
				redditCommentDataContainerDto.getData().getChildren().add(assembleComment(reply));
			}
			redditCommentDataDto.getData().setReplies(redditCommentDataContainerDto);
		}else{
			redditCommentDataDto.getData().setReplies("");
		}
		return redditCommentDataDto;
	}
}
