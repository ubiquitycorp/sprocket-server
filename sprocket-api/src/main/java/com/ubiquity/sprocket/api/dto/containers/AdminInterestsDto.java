package com.ubiquity.sprocket.api.dto.containers;

import java.util.LinkedList;
import java.util.List;

import com.ubiquity.sprocket.api.dto.model.admin.AdminInterestDto;

public class AdminInterestsDto {
	List <AdminInterestDto> interests = new LinkedList<AdminInterestDto>();
	
	List<AdminInterestDto> add;
	
	List<AdminInterestDto> delete;

	public List<AdminInterestDto> getAdd() {
		return add;
	}

	public List<AdminInterestDto> getDelete() {
		return delete;
	}

	public List<AdminInterestDto> getInterests() {
		return interests;
	}
	
	
	
}
