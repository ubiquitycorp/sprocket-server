package com.ubiquity.social.service;

import org.apache.commons.configuration.Configuration;

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

}
