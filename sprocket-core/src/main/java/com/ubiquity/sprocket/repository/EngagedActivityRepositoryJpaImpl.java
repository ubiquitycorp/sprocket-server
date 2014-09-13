package com.ubiquity.sprocket.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.niobium.repository.BaseRepositoryJpaImpl;
import com.ubiquity.sprocket.domain.EngagedActivity;

public class EngagedActivityRepositoryJpaImpl extends BaseRepositoryJpaImpl<Long, EngagedActivity> implements
EngagedActivityRepository {

	public EngagedActivityRepositoryJpaImpl(EntityManager em) {
		super(EngagedActivity.class, em);
	}

	public EngagedActivityRepositoryJpaImpl() {
		super(EngagedActivity.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EngagedActivity> findMeanByGroup(String group, Integer limit) {
		Query query = getEntityManager().createQuery("select distinct ei from EngagedItem ei where ei.activity is not null and ei.documentDataType is null and ei.user in (select gm.user from GroupMembership gm where gm.groupIdentifier = :group) group by ei.activity.activityId order by count(*) desc");
		query.setParameter("group", group);
		query.setMaxResults(limit);
		return (List<EngagedActivity>)query.getResultList();
	}
}
