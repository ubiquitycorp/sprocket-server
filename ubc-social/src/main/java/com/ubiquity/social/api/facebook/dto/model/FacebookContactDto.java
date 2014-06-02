package com.ubiquity.social.api.facebook.dto.model;

import com.google.gson.annotations.SerializedName;

public class FacebookContactDto {

	private String id;
	
	private String name;
	
	@SerializedName("first_name")
	private String firstName;
    
	@SerializedName("last_name")
	private String lastName;
	
	private String email;
    
	private String link;

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
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

	public String getLink() {
		return link;
	}


	
    
   
}

