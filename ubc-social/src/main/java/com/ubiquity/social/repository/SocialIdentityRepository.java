package com.ubiquity.social.repository;

import java.util.List;

import com.niobium.repository.Repository;
import com.ubiquity.social.domain.SocialIdentity;
import com.ubiquity.social.domain.SocialProviderType;

/***
 *
 * Interface exposing CRUD methods for the SocialIdentity entity
 *
 * @author peter.tadros
 *
 */
public interface SocialIdentityRepository extends Repository <Long, SocialIdentity> {
	List<SocialIdentity> findByUserId(Long ownerId);
	SocialIdentity findOne(Long ownerId, SocialProviderType providerType);
	SocialIdentity findOneByProviderIdentifier(String providerIdentifier, SocialProviderType providerType);
}
