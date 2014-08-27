package com.ubiquity.sprocket.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

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

	@SuppressWarnings("unchecked")
	@Override
	public List<EngagedItem> findMeanByGroup(String group) {
		Query query = getEntityManager().createQuery("select ei from EngagedItem ei where :group in elements(ei.user.groups) group by ei.");
		query.setParameter("group", group);
		return (List<EngagedItem>)query.getResultList();
	}
}
