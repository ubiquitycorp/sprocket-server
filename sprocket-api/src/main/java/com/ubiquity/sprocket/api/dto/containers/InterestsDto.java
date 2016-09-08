package com.ubiquity.sprocket.api.dto.containers;

import java.util.LinkedList;
import java.util.List;

import com.ubiquity.sprocket.api.dto.model.InterestDto;

public class InterestsDto {
	
	public List<InterestDto> interests = new LinkedList<InterestDto>();

	public List<InterestDto> getInterests() {
		return interests;
	}
	
	

}
