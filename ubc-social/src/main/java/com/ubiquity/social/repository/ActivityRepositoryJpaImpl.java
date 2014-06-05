package com.ubiquity.social.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.niobium.repository.BaseRepositoryJpaImpl;
import com.ubiquity.social.domain.Activity;

public class ActivityRepositoryJpaImpl extends BaseRepositoryJpaImpl <Long, Activity> implements ActivityRepository {

	public ActivityRepositoryJpaImpl(EntityManager em) {
		super(Activity.class, em);
	}

	public ActivityRepositoryJpaImpl() {
		super(Activity.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Activity> findByOwnerId(Long ownerId) {
		assert(ownerId != null);
		Query query = getEntityManager().createQuery("select a from Activity a where a.owner.userId = :ownerId");
		query.setParameter("ownerId", ownerId);
		return (List<Activity>)query.getResultList();
	}
	
}
