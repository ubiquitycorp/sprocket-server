package com.ubiquity.identity.domain;

public enum ClientPlatform {
	Android,
	IOS;

	public static ClientPlatform getEnum(int clientPlatform) {
		if(ClientPlatform.values().length == clientPlatform)
			throw new IllegalArgumentException("Unknown platform");
		return ClientPlatform.values()[clientPlatform];
	}
}

