package com.ubiquity.sprocket.api.dto.containers;

import java.util.LinkedList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.ubiquity.sprocket.api.dto.model.VideoDto;
import com.ubiquity.sprocket.api.validation.EngagementValidation;

public class VideosDto {
	
	@Size(min = 1, groups = {EngagementValidation.class })
	@NotNull(groups = {EngagementValidation.class })
	private List<VideoDto> videos = new LinkedList<VideoDto>();

	public List<VideoDto> getVideos() {
		return videos;
	}

	private String historyEmptyMessage;
	
	public void setHistoryEmptyMessage(String historyEmptyMessage)
	{
		this.historyEmptyMessage = historyEmptyMessage;
	}

	public String getHistoryEmptyMessage() {
		return historyEmptyMessage;
	}
	
	
}
