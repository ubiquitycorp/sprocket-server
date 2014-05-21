package com.ubiquity.social.service;

import org.apache.commons.configuration.Configuration;

import com.ubiquity.identity.domain.Identity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.social.domain.ExternalIdentity;
import com.ubiquity.social.domain.SocialProvider;
import com.ubiquity.social.repository.SocialIdentityRepository;
import com.ubiquity.social.repository.SocialIdentityRepositoryJpaImpl;

public class SocialService {
	
	private SocialIdentityRepository socialIdentityRepository;

	public SocialService(Configuration configuration) {
		this.socialIdentityRepository = new SocialIdentityRepositoryJpaImpl();
	}
	
	public ExternalIdentity findSocialIdentity(Long userId, SocialProvider providerType) {
		return socialIdentityRepository.findOne(userId, providerType);
	}

	public ExternalIdentity findSocialIdentity(String providerIdentifier, SocialProvider providerType) {
		ExternalIdentity identity = socialIdentityRepository.findOneByProviderIdentifier(providerIdentifier, providerType);
		return identity;
	}
	
	
	
	/***
	 * Utility method for retrieving social identity already associated with this user
	 * 
	 * @param user
	 * @param socialProvider
	 * 
	 * @throws IllegalArgumentException if the user does not have an identity for this provider
	 * 
	 * @return
	 */
	public static ExternalIdentity getAssociatedSocialIdentity(User user, SocialProvider socialProvider) {
		ExternalIdentity external = null;
		for(Identity identity : user.getIdentities()) {
			if(identity instanceof ExternalIdentity) {
				ExternalIdentity ext = (ExternalIdentity)identity;
				if(ext.getSocialProvider().getValue() == socialProvider.getValue()) {
					external = ext;
					break;
				}
			}
		}
		if(external == null)
			throw new IllegalArgumentException("User has no identity for this provider: " + socialProvider.toString());
		
		return external;
	}

}
