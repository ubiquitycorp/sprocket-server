package com.ubiquity.identity.domain;

public enum ClientPlatform {
	Android,
	IOS;

	public static ClientPlatform getEnum(int clientPlatform) {
		return ClientPlatform.values()[clientPlatform];
	}
}

