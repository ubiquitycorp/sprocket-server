package com.ubiquity.sprocket.api.dto.containers;

import java.util.LinkedList;
import java.util.List;

import com.ubiquity.sprocket.api.dto.model.developer.ExternalApplicationDto;

public class ExternalApplicationsDto {
	
	private List<ExternalApplicationDto> externalApplications = new LinkedList<ExternalApplicationDto>();

	public List<ExternalApplicationDto> getExternalApplications() {
		return externalApplications;
	}
}
