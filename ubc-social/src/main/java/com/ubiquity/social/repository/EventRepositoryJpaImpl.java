package com.ubiquity.social.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.niobium.repository.BaseRepositoryJpaImpl;
import com.ubiquity.social.domain.Event;

public class EventRepositoryJpaImpl extends BaseRepositoryJpaImpl <Long, Event> implements EventRepository {

	public EventRepositoryJpaImpl(EntityManager em) {
		super(Event.class, em);
	}

	public EventRepositoryJpaImpl() {
		super(Event.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Event> findByUserId(Long userId) {
		assert(userId != null);
		Query query = getEntityManager().createQuery("select e from Event e where e.user.userId = :userId");
		query.setParameter("userId", userId);
		return (List<Event>)query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Event> findByUserIdAndTimeInterval(Long userId, Long startInterval, Long endInterval) {
		assert(startInterval != null);
		assert(endInterval != null);
		assert(userId != null);

		Query query = getEntityManager().createQuery("select e from Event e where e.startDate >= :startInterval and e.startDate <= :endInterval and e.user.userId = :userId");
		query.setParameter("startInterval", startInterval);
		query.setParameter("endInterval", endInterval);
		query.setParameter("userId", userId);
		return (List<Event>)query.getResultList();
	}

	@Override
	public int countAllEventsByOwnerIdAndSocialIdentityProvider(Long userId) {
		Query query = getEntityManager().createQuery("select count(e.eventId) from Event e where e.user.userId = :ownerId");
		query.setParameter("ownerId", userId);
		Number count = (Number)query.getSingleResult();
		return count.intValue();
	}
	
}
