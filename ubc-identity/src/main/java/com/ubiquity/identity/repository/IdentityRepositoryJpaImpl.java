package com.ubiquity.identity.repository;

import javax.persistence.EntityManager;

import com.niobium.repository.BaseRepositoryJpaImpl;
import com.ubiquity.identity.domain.Identity;

public class IdentityRepositoryJpaImpl extends BaseRepositoryJpaImpl <Long, Identity> implements IdentityRepository {

	public IdentityRepositoryJpaImpl(EntityManager em) {
		super(Identity.class, em);
	}

	public IdentityRepositoryJpaImpl() {
		super(Identity.class);
	}
	
}
