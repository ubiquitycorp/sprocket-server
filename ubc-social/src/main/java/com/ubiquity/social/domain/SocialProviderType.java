package com.ubiquity.social.domain;

/***
 * SocialProviders enum for all supported social providers
 * 
 * @author Peter
 * 
 */
public enum SocialProviderType
{
    Facebook (1),
    Yahoo (2),
    LinkedIn (3),
    Google (4);
    
    private final int value;

    private SocialProviderType(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }

	public static String getName(int provider_id) {
		return SocialProviderType.values()[(provider_id - 1)].name();
	}
	
	public static SocialProviderType getEnum(int provider_id) {
		return SocialProviderType.values()[(provider_id - 1)];
	}
}
