package com.ubiquity.sprocket.network.api.facebook.dto.model;

import com.google.gson.annotations.SerializedName;

public class FacebookBatchRequestDto {
	
	private String method;
	
	@SerializedName("relative_url")
	private String relativeUrl;

	public String getMethod() {
		return method;
	}

	public String getRelativeUrl() {
		return relativeUrl;
	}

	/**
	 * Default constructor required by most serialization frameworks
	 */
	protected FacebookBatchRequestDto() {}
	
	/**
	 * Parameterized constructor builds a batch with required fields
	 * 
	 * @param method
	 * @param relativeUrl
	 */
	public FacebookBatchRequestDto(String method, String relativeUrl) {
		this.method = method;
		this.relativeUrl = relativeUrl;
	}
	
	
	

}
