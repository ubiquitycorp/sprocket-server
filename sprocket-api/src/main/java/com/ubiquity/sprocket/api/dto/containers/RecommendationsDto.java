package com.ubiquity.sprocket.api.dto.containers;

import java.util.LinkedList;
import java.util.List;

import com.ubiquity.sprocket.api.dto.model.media.VideoDto;
import com.ubiquity.sprocket.api.dto.model.social.ActivityDto;

public class RecommendationsDto {
	
	private List<VideoDto> videos = new LinkedList<VideoDto>();
	private List<ActivityDto> activities = new LinkedList<ActivityDto>();
	
	public List<VideoDto> getVideos() {
		return videos;
	}
	public List<ActivityDto> getActivities() {
		return activities;
	}
	
	
	
	

}
