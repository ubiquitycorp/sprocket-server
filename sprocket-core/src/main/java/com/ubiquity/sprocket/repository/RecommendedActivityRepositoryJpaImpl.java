package com.ubiquity.sprocket.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.niobium.repository.BaseRepositoryJpaImpl;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.social.domain.Activity;
import com.ubiquity.sprocket.domain.RecommendedActivity;

public class RecommendedActivityRepositoryJpaImpl extends BaseRepositoryJpaImpl<Long, RecommendedActivity> implements
RecommendedActivityRepository {

	public RecommendedActivityRepositoryJpaImpl(EntityManager em) {
		super(RecommendedActivity.class, em);
	}

	public RecommendedActivityRepositoryJpaImpl() {
		super(RecommendedActivity.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Activity> findRecommendedActivitiesByGroup(String group) {
		Query query = getEntityManager().createQuery("select ra.activity from RecommendedActivity ra where ra.groupIdentifier = :groupIdentifier");
		query.setParameter("groupIdentifier", group);
		return (List<Activity>)query.getResultList();
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<Activity> findRecommendedActivitiesByGroup(String group,
			ExternalNetwork network) {
		Query query = getEntityManager().createQuery("select ra.activity from RecommendedActivity ra where ra.activity.externalNetwork = :externalNetwork and ra.groupIdentifier = :groupIdentifier");
		query.setParameter("groupIdentifier", group);
		query.setParameter("externalNetwork", network);
		return (List<Activity>)query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RecommendedActivity> findAllByExternalNetwork(ExternalNetwork network) {
		Query query = getEntityManager().createQuery("select ra from RecommendedActivity ra where ra.activity.externalNetwork = :externalNetwork");
		query.setParameter("externalNetwork", network);
		return (List<RecommendedActivity>)query.getResultList();
	}
}
