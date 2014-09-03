package com.ubiquity.sprocket.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.niobium.repository.BaseRepositoryJpaImpl;
import com.ubiquity.sprocket.domain.Location;

public class LocationRepositoryJpaImpl extends BaseRepositoryJpaImpl<Long, Location> implements
LocationRepository {

	public LocationRepositoryJpaImpl(EntityManager em) {
		super(Location.class, em);
	}

	public LocationRepositoryJpaImpl() {
		super(Location.class);
	}

	@Override
	public Location findByUserId(Long userId) {
		Query query = getEntityManager().createQuery("select l from Location l where l.user.userId = :userId");
		query.setParameter("userId", userId);
		return (Location)query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Location> findAll() {
		Query query = getEntityManager().createQuery("select l from Location l");
		return (List<Location>)query.getSingleResult();
	}
	
	
}
