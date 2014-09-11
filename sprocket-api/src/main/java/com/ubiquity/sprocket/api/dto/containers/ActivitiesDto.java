package com.ubiquity.sprocket.api.dto.containers;

import java.util.LinkedList;
import java.util.List;

import com.ubiquity.sprocket.api.dto.model.ActivityDto;

public class ActivitiesDto {
	
	private List<ActivityDto> activities = new LinkedList<ActivityDto>();

	private Boolean newsFeedIsModified = true;
	private Boolean localNewsFeedIsModified = true;
	
	public List<ActivityDto> getActivities() {
		return activities;
	}

	public Boolean getNewsFeedIsModified() {
		return newsFeedIsModified;
	}

	public void setNewsFeedIsModified(Boolean newsFeedIsModified) {
		this.newsFeedIsModified = newsFeedIsModified;
	}

	public Boolean getLocalNewsFeedIsModified() {
		return localNewsFeedIsModified;
	}

	public void setLocalNewsFeedIsModified(Boolean localNewsFeedIsModified) {
		this.localNewsFeedIsModified = localNewsFeedIsModified;
	}

}
