package com.ubiquity.social.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.niobium.repository.BaseRepositoryJpaImpl;
import com.ubiquity.social.domain.ExternalIdentity;
import com.ubiquity.social.domain.SocialProvider;

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
						"select c from SocialIdentity c where c.user.userId = :ownerId");
		query.setParameter("userId", userId);
		return (List<ExternalIdentity>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ExternalIdentity findOne(Long userId, SocialProvider providerType) {
		assert (userId != null);
		assert (providerType != null);
		Query query = getEntityManager()
				.createQuery(
						"select c from SocialIdentity c where c.user.userId = :userId and c.socialProviderType = :providerType");
		query.setParameter("userId", userId);
		query.setParameter("providerType", providerType);
		List<ExternalIdentity> results = query.getResultList();
		return results.size() > 0 ? (ExternalIdentity)results.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ExternalIdentity findOneByProviderIdentifier(String providerIdentifier, SocialProvider providerType) {
		assert (providerIdentifier != null);
		assert (providerType != null);
		Query query = getEntityManager().createQuery(
				"select c from SocialIdentity c where c.identifier = :providerIdentifier and c.socialProviderType = :providerType");
		query.setParameter("providerIdentifier", providerIdentifier);
		query.setParameter("providerType", providerType);
		List<ExternalIdentity> results = query.getResultList();
		return results.size() > 0 ? (ExternalIdentity)results.get(0) : null;
	}
}
