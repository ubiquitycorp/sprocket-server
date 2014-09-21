package com.ubiquity.sprocket.repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.niobium.repository.BaseRepositoryJpaImpl;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.sprocket.domain.ExternalInterest;

public class ExternalInterestRepositoryJpaImpl extends BaseRepositoryJpaImpl<Long, ExternalInterest> implements
ExternalInterestRepository {

	public ExternalInterestRepositoryJpaImpl(EntityManager em) {
		super(ExternalInterest.class, em);
	}

	public ExternalInterestRepositoryJpaImpl() {
		super(ExternalInterest.class);
	}

	@Override
	public ExternalInterest getByNameAndExternalNetwork(String name,
			ExternalNetwork network) {
		Query query = getEntityManager().createQuery("select ei from ExternalInterest ei where ei.name = :name and ei.externalNetwork = :externalNetwork");
		query.setParameter("name", name);
		query.setParameter("externalNetwork", network);

		try {
			return (ExternalInterest)query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
		
	}

	
}
