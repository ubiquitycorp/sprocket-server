package com.ubiquity.social.api.linkedin.dto.model;

public class LinkedInRecipientDto {
	
	private LinkedInPersonDto person;

	public LinkedInRecipientDto(LinkedInPersonDto person) {
		this.person = person;
	}

	public LinkedInPersonDto getPerson() {
		return person;
	}
	
	

}
