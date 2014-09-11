package com.ubiquity.sprocket.api.dto.containers;

import java.util.LinkedList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.ubiquity.sprocket.api.dto.model.ActivityDto;
import com.ubiquity.sprocket.api.validation.EngagementValidation;

public class ActivitiesDto {
	
	@Size(min = 1, max = 1000, groups = {EngagementValidation.class })
	@NotNull(groups = {EngagementValidation.class })
	private List<ActivityDto> activities = new LinkedList<ActivityDto>();

	public List<ActivityDto> getActivities() {
		return activities;
	}
	
	

}
