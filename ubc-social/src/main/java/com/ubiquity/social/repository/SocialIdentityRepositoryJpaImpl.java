package com.ubiquity.social.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.niobium.repository.BaseRepositoryJpaImpl;
import com.ubiquity.identity.domain.ExternalIdentity;
import com.ubiquity.social.domain.SocialNetwork;

public class SocialIdentityRepositoryJpaImpl extends BaseRepositoryJpaImpl<Long, ExternalIdentity> implements
SocialIdentityRepository {

	public SocialIdentityRepositoryJpaImpl(EntityManager em) {
		super(ExternalIdentity.class, em);
	}

	public SocialIdentityRepositoryJpaImpl() {
		super(ExternalIdentity.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ExternalIdentity> findByUserId(Long userId) {
		assert (userId != null);

		Query query = getEntityManager()
				.createQuery(
						"select c from ExternalIdentity c where c.user.userId = :ownerId");
		query.setParameter("userId", userId);
		return (List<ExternalIdentity>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ExternalIdentity findOne(Long userId, SocialNetwork socialNetwork) {
		assert (userId != null);
		assert (socialNetwork != null);
		Query query = getEntityManager()
				.createQuery(
						"select c from ExternalIdentity c where c.user.userId = :userId and c.identityProvider = :provider");
		query.setParameter("userId", userId);
		query.setParameter("provider", socialNetwork.getValue());
		List<ExternalIdentity> results = query.getResultList();
		return results.size() > 0 ? (ExternalIdentity)results.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ExternalIdentity findOneByProviderIdentifier(String providerIdentifier, SocialNetwork socialNetwork) {
		assert (providerIdentifier != null);
		assert (socialNetwork != null);
		Query query = getEntityManager().createQuery(
				"select c from Externaldentity c where c.identifier = :providerIdentifier and c.identityProvider = :provider");
		query.setParameter("providerIdentifier", providerIdentifier);
		query.setParameter("provider", socialNetwork.getValue());
		List<ExternalIdentity> results = query.getResultList();
		return results.size() > 0 ? (ExternalIdentity)results.get(0) : null;
	}
}
