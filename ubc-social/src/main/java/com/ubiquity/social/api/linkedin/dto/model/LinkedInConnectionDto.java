package com.ubiquity.social.api.linkedin.dto.model;



public class LinkedInConnectionDto {
	
	private String formattedName;
	
	private String firstName;
    
	private String lastName;
	
	private String emailAddress;
    
	private String publicProfileUrl;

	private String pictureUrl;
	
	private String id;

	public String getPictureUrl() {
		return pictureUrl;
	}

	public String getFormattedName() {
		return formattedName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public String getPublicProfileUrl() {
		return publicProfileUrl;
	}

	public String getId() {
		return id;
	}
	
	
}
