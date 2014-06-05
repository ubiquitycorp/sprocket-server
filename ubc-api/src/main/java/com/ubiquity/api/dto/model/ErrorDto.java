package com.ubiquity.api.dto.model;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for returning error messages in a JSON list
 * 
 * @author chris
 *
 */
public class ErrorDto {
	
	private List<String> messages = new ArrayList<String>();

	/***
	 * User friendly messages
	 * @return
	 */
	public List<String> getMessages() {
		return messages;
	}
	
	
	
	
	

}
