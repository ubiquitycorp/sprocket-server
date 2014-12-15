package com.ubiquity.sprocket.network.api.reddit.dto.model;

import java.util.List;

public class RedditResponseJson {
	
	private String captcha;
	private List<List<String>> errors;
	private RedditCaptchaDto data;
	
	public String getCaptcha() {
		return captcha;
	}
	public List<List<String>> getErrors() {
		return errors;
	}
	public RedditCaptchaDto getData() {
		return data;
	}
	public String getErrorsMessage(){
		StringBuilder errorString = new StringBuilder(); 
		if (errors!= null && errors.size() > 0)
		{
			for (List<String> error : errors)
			{
				if (error != null && !error.isEmpty()) {
					if(error.size()>2)
						errorString.append(error.get(0)+" "+ error.get(1));
					else
						errorString.append(error.get(0)+" ");
				}
			}
		}		
		return errorString.toString();
	}
	
}
