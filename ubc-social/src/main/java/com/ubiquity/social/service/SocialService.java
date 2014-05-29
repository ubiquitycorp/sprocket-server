package com.ubiquity.social.service;

import org.apache.commons.configuration.Configuration;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.identity.domain.Identity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.social.domain.SocialNetwork;
import com.ubiquity.social.repository.SocialIdentityRepository;
import com.ubiquity.social.repository.SocialIdentityRepositoryJpaImpl;

public class SocialService {
	
	private SocialIdentityRepository socialIdentityRepository;

	public SocialService(Configuration configuration) {
		this.socialIdentityRepository = new SocialIdentityRepositoryJpaImpl();
	}
	
	public void update(ExternalIdentity identity) {
		try {	
			EntityManagerSupport.beginTransaction();
			socialIdentityRepository.update(identity);
			EntityManagerSupport.commit();
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}
	
	public ExternalIdentity findSocialIdentity(Long userId, SocialNetwork socialNetwork) {
		return socialIdentityRepository.findOne(userId, socialNetwork);
	}

	public ExternalIdentity findSocialIdentity(String providerIdentifier, SocialNetwork socialNetwork) {
		ExternalIdentity identity = socialIdentityRepository.findOneByProviderIdentifier(providerIdentifier, socialNetwork);
		return identity;
	}
	
	
	
	/***
	 * Utility method for retrieving social identity already associated with this user
	 * 
	 * @param user
	 * @param socialNetwork
	 * 
	 * @throws IllegalArgumentException if the user does not have an identity for this provider
	 * 
	 * @return
	 */
	public static ExternalIdentity getAssociatedSocialIdentity(User user, SocialNetwork socialNetwork) {
		ExternalIdentity external = null;
		for(Identity identity : user.getIdentities()) {
			if(identity instanceof ExternalIdentity) {
				ExternalIdentity ext = (ExternalIdentity)identity;
				if(ext.getIdentityProvider() == socialNetwork.getValue()) {
					external = ext;
					break;
				}
			}
		}
		if(external == null)
			throw new IllegalArgumentException("User has no identity for this social network: " + socialNetwork.toString());
		
		return external;
	}

}
