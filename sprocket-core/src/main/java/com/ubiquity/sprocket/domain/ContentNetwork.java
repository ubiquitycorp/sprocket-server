package com.ubiquity.sprocket.domain;

public enum ContentNetwork {
	YouTube,
	Netflix;
	
	public static ContentNetwork getContentNetworkFromId(Integer id) {
		if(id <= ContentNetwork.values().length && id > 0) {
			return ContentNetwork.values()[id];
		}
		throw new IllegalArgumentException("No such content network for id: " + id);
	}
}


