package com.ubiquity.sprocket.network.api.dto.model;

public enum Gender {
	
	Male, Female, NotSpecified;
	
	public static Gender getGenderById(int id) {
		validate(id);
		return Gender.values()[id];
	}
	
	private static void validate(int id) {
		if(id > Gender.values().length)
		throw new IllegalArgumentException("No such gender for id: " + id);
	}

}
