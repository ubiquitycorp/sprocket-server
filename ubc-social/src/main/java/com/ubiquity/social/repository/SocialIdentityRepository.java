package com.ubiquity.social.repository;

import java.util.List;

import com.niobium.repository.Repository;
import com.ubiquity.social.domain.ExternalIdentity;
import com.ubiquity.social.domain.SocialProvider;

/***
 *
 * Interface exposing CRUD methods for the SocialIdentity entity
 *
 * @author peter.tadros
 *
 */
public interface SocialIdentityRepository extends Repository <Long, ExternalIdentity> {
	List<ExternalIdentity> findByUserId(Long ownerId);
	ExternalIdentity findOne(Long ownerId, SocialProvider providerType);
	ExternalIdentity findOneByProviderIdentifier(String providerIdentifier, SocialProvider providerType);
}
