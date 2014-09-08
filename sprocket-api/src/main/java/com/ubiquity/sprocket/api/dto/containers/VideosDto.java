package com.ubiquity.sprocket.api.dto.containers;

import java.util.LinkedList;
import java.util.List;

import com.ubiquity.sprocket.api.dto.model.VideoDto;

public class VideosDto {
	
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
