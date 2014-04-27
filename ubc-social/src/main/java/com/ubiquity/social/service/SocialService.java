package com.ubiquity.social.service;

import org.apache.commons.configuration.Configuration;

import com.ubiquity.social.domain.SocialIdentity;
import com.ubiquity.social.domain.SocialProviderType;
import com.ubiquity.social.repository.SocialIdentityRepository;
import com.ubiquity.social.repository.SocialIdentityRepositoryJpaImpl;

public class SocialService {
	
	private SocialIdentityRepository socialIdentityRepository;

	public SocialService(Configuration configuration) {
		this.socialIdentityRepository = new SocialIdentityRepositoryJpaImpl();
	}
	
	public SocialIdentity findSocialIdentity(Long userId, SocialProviderType providerType) {
		return socialIdentityRepository.findOne(userId, providerType);
	}

	public SocialIdentity findSocialIdentity(String providerIdentifier, SocialProviderType providerType) {
		SocialIdentity identity = socialIdentityRepository.findOneByProviderIdentifier(providerIdentifier, providerType);
		return identity;
	}	

}
