package com.ubiquity.sprocket.api.dto.containers;

import java.util.LinkedList;
import java.util.List;

import com.ubiquity.sprocket.api.dto.model.developer.ApplicationDto;

/***
 * 
 * @author mina.shafik
 * 
 */
public class ApplicationsDto {
	private List<ApplicationDto> applications = new LinkedList<ApplicationDto>();

	// methods
	public List<ApplicationDto> getApplications() {
		return applications;
	}
}
