package com.ubiquity.sprocket.api.dto.model;

import java.util.LinkedList;
import java.util.List;

public class InterestDto {
	
	private Long id;
	private String name;
	
	private List<InterestDto> children = new LinkedList<InterestDto>();

	
	public InterestDto(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<InterestDto> getChildren() {
		return children;
	}
	
	

}
