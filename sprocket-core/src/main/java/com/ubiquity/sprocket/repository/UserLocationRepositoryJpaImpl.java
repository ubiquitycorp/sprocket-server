package com.ubiquity.sprocket.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.niobium.repository.BaseRepositoryJpaImpl;
import com.ubiquity.sprocket.domain.UserLocation;

public class UserLocationRepositoryJpaImpl extends BaseRepositoryJpaImpl<Long, UserLocation> implements
UserLocationRepository {

	public UserLocationRepositoryJpaImpl(EntityManager em) {
		super(UserLocation.class, em);
	}

	public UserLocationRepositoryJpaImpl() {
		super(UserLocation.class);
	}

	@Override
	public UserLocation findByUserId(Long userId) {
		Query query = getEntityManager().createQuery("select ul from UserLocation ul where ul.user.userId = :userId");
		query.setParameter("userId", userId);
		try {
			return (UserLocation)query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserLocation> findAll() {
		Query query = getEntityManager().createQuery("select ul from UserLocation ul");
		return (List<UserLocation>)query.getSingleResult();
	}
	
	
}
