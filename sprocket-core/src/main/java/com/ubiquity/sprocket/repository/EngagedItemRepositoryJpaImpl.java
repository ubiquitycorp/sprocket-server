package com.ubiquity.sprocket.repository;

import javax.persistence.EntityManager;

import com.niobium.repository.BaseRepositoryJpaImpl;
import com.ubiquity.sprocket.domain.EngagedItem;

public class EngagedItemRepositoryJpaImpl extends BaseRepositoryJpaImpl<Long, EngagedItem> implements
EngagedItemRepository {

	public EngagedItemRepositoryJpaImpl(EntityManager em) {
		super(EngagedItem.class, em);
	}

	public EngagedItemRepositoryJpaImpl() {
		super(EngagedItem.class);
	}
}
