package com.ubiquity.sprocket.repository;

import javax.persistence.EntityManager;

import com.niobium.repository.BaseRepositoryJpaImpl;
import com.ubiquity.sprocket.domain.ExternalInterest;

public class ExternalInterestRepositoryJpaImpl extends BaseRepositoryJpaImpl<Long, ExternalInterest> implements
ExternalInterestRepository {

	public ExternalInterestRepositoryJpaImpl(EntityManager em) {
		super(ExternalInterest.class, em);
	}

	public ExternalInterestRepositoryJpaImpl() {
		super(ExternalInterest.class);
	}

	
}
