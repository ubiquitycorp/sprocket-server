package com.ubiquity.sprocket.network.api.random.generator;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.ubiquity.sprocket.network.api.dto.model.Activity;
import com.ubiquity.sprocket.network.api.dto.model.VideoContent;

public class RandomListGenerator {
	private static Random random = new Random();

	public static List<Activity> GenerateActivityList(Long userId, Long lastRequest , Long thisRequest){
		List<Activity> activities = new LinkedList<Activity>();
		for (int i = 1 ; i <= 10 ; i++ ){
			activities.add(RandomObjectGenerator.generateActivity(userId, lastRequest, i, random.nextInt(6)));
		}
		for (int i = 11 ; i <= 20 ; i++ ){
			activities.add(RandomObjectGenerator.generateActivity(userId,  thisRequest, i, random.nextInt(6)));
		}
		return activities;
	}
	
	public static List<VideoContent> GenerateVideoList(Long userId, Long lastRequest , Long thisRequest){
		List<VideoContent> videos = new LinkedList<VideoContent>();
		for (int i = 1 ; i <= 10 ; i++ ){
			videos.add(RandomObjectGenerator.generateVideoContent(userId, lastRequest, i));
		}
		for (int i = 11 ; i <= 20 ; i++ ){
			videos.add(RandomObjectGenerator.generateVideoContent(userId, thisRequest, i));
		}
		return videos;
	}
}
