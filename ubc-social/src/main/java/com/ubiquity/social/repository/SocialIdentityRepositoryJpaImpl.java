package com.ubiquity.social.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.niobium.repository.BaseRepositoryJpaImpl;
import com.ubiquity.social.domain.SocialIdentity;
import com.ubiquity.social.domain.SocialProviderType;

public class SocialIdentityRepositoryJpaImpl extends BaseRepositoryJpaImpl<Long, SocialIdentity> implements
SocialIdentityRepository {

	public SocialIdentityRepositoryJpaImpl(EntityManager em) {
		super(SocialIdentity.class, em);
	}

	public SocialIdentityRepositoryJpaImpl() {
		super(SocialIdentity.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SocialIdentity> findByUserId(Long userId) {
		assert (userId != null);

		Query query = getEntityManager()
				.createQuery(
						"select c from SocialIdentity c where c.user.userId = :ownerId");
		query.setParameter("userId", userId);
		return (List<SocialIdentity>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public SocialIdentity findOne(Long userId, SocialProviderType providerType) {
		assert (userId != null);
		assert (providerType != null);
		Query query = getEntityManager()
				.createQuery(
						"select c from SocialIdentity c where c.user.userId = :userId and c.socialProviderType = :providerType");
		query.setParameter("userId", userId);
		query.setParameter("providerType", providerType);
		List<SocialIdentity> results = query.getResultList();
		return results.size() > 0 ? (SocialIdentity)results.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SocialIdentity findOneByProviderIdentifier(String providerIdentifier, SocialProviderType providerType) {
		assert (providerIdentifier != null);
		assert (providerType != null);
		Query query = getEntityManager().createQuery(
				"select c from SocialIdentity c where c.identifier = :providerIdentifier and c.socialProviderType = :providerType");
		query.setParameter("providerIdentifier", providerIdentifier);
		query.setParameter("providerType", providerType);
		List<SocialIdentity> results = query.getResultList();
		return results.size() > 0 ? (SocialIdentity)results.get(0) : null;
	}
}
