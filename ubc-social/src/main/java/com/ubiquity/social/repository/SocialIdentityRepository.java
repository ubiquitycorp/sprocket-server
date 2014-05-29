package com.ubiquity.social.repository;

import java.util.List;

import com.niobium.repository.Repository;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.social.domain.SocialNetwork;

/***
 *
 * Interface exposing CRUD methods for the SocialIdentity entity
 *
 * @author peter.tadros
 *
 */
public interface SocialIdentityRepository extends Repository <Long, ExternalIdentity> {
	List<ExternalIdentity> findByUserId(Long ownerId);
	ExternalIdentity findOne(Long ownerId, SocialNetwork providerType);
	ExternalIdentity findOneByProviderIdentifier(String providerIdentifier, SocialNetwork providerType);
}
