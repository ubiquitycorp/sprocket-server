package com.ubiquity.sprocket.api.dto.containers;

import java.util.LinkedList;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.ubiquity.sprocket.api.dto.model.IdentityDto;
import com.ubiquity.sprocket.api.validation.EngagementValidation;

public class IdentitiesDto {
	
	@NotNull(groups = {EngagementValidation.class })
	private List<IdentityDto> identities = new LinkedList<IdentityDto>();

	public List<IdentityDto> getIdentities() {
		return identities;
	}
}
