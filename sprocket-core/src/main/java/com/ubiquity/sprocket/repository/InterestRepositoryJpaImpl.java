package com.ubiquity.sprocket.repository;

import javax.persistence.EntityManager;

import com.niobium.repository.BaseRepositoryJpaImpl;
import com.ubiquity.sprocket.domain.Interest;

public class InterestRepositoryJpaImpl extends BaseRepositoryJpaImpl<Long, Interest> implements
InterestRepository {

	public InterestRepositoryJpaImpl(EntityManager em) {
		super(Interest.class, em);
	}

	public InterestRepositoryJpaImpl() {
		super(Interest.class);
	}

	
}
