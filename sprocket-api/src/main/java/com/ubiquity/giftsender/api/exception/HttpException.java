package com.ubiquity.giftsender.api.exception;

public class HttpException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private Integer responseCode;
	
	public HttpException(String message, Integer responseCode) {
		super(message);
		this.responseCode = responseCode;
	}
	
	public Integer getResponseCode() {
		return responseCode;
	}
	
	
	
	

}
