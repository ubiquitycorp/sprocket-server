package com.ubiquity.social.domain;

/***
 * SocialProviders enum for all supported social providers
 * 
 * @author Peter
 * 
 */
public enum SocialProvider
{
    Facebook (1),
    Yahoo (2),
    LinkedIn (3),
    Google (4);
    
    private final int value;

    private SocialProvider(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }

	public static String getName(int providerId) {
		validate(providerId);
		return SocialProvider.values()[(providerId - 1)].name();
	}
	
	public static SocialProvider getEnum(int providerId) {
		validate(providerId);
		return SocialProvider.values()[(providerId - 1)];
	}
	
	private static void validate(int providerId) {
		if(SocialProvider.values().length == providerId - 1)
		throw new IllegalArgumentException("Unknown provider id");
	}
}
