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
	public List<EngagedActivity> findMeanByGroup(String group) {
		Query query = getEntityManager().createQuery("select ei from EngagedItem ei where ei.user in (select gm.user from GroupMembership gm where gm.groupIdentifier = :group) group by ei.activity.activityId order by count(*) desc");
		query.setParameter("group", group);
		return (List<EngagedActivity>)query.getResultList();
	}
}
