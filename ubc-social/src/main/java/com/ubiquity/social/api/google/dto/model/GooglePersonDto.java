package com.ubiquity.social.api.google.dto.model;

import com.ubiquity.media.domain.Image;


public class GooglePersonDto {

	private String displayName;
	
	private String firstName;
    
	private String lastName;
	
	private String email ;

	private Image image;
    
	private String url;

	private String id;

	public String getDisplayName() {
		return displayName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public Image getImage() {
		return image;
	}

	public String getUrl() {
		return url;
	}

	public String getId() {
		return id;
	}
	
    
}
