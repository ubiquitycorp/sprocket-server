package com.ubiquity.sprocket.api.dto.model;

public class AggregationDto {
	
	private String key;
	private Long count;
	
	public AggregationDto(String key, Long count) {
		this.key = key;
		this.count = count;
	}

	public String getKey() {
		return key;
	}

	public Long getCount() {
		return count;
	}
	
	

}
