package com.ubiquity.social.domain;

/***
 * SocialNetwork enum for all supported social networks
 * 
 * @author Peter
 * 
 */
public enum SocialNetwork {
    Facebook (1),
    Yahoo (2),
    LinkedIn (3),
    Google (4);
    
    private final int value;

    private SocialNetwork(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }

	public static String getName(int providerId) {
		validate(providerId);
		return SocialNetwork.values()[(providerId - 1)].name();
	}
	
	public static SocialNetwork getEnum(int providerId) {
		validate(providerId);
		return SocialNetwork.values()[(providerId - 1)];
	}
	
	private static void validate(int providerId) {
		if(SocialNetwork.values().length == providerId - 1)
		throw new IllegalArgumentException("Unknown provider id");
	}
}
