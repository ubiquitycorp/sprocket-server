package com.ubiquity.sprocket.repository;

import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.niobium.repository.BaseRepositoryJpaImpl;
import com.ubiquity.sprocket.domain.Place;

public class PlaceRepositoryJpaImpl extends BaseRepositoryJpaImpl<Long, Place> implements
PlaceRepository {

	public PlaceRepositoryJpaImpl(EntityManager em) {
		super(Place.class, em);
	}

	public PlaceRepositoryJpaImpl() {
		super(Place.class);
	}

	@Override
	public Place findByName(String name, Locale locale) {
		Query query = getEntityManager().createQuery("select p from Place p where p.name = :name and p.locale = :locale");
		query.setParameter("name", name);
		query.setParameter("locale", locale);
		return (Place)query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Place> findAll() {
		Query query = getEntityManager().createQuery("select p from Place p");
		return (List<Place>)query.getResultList();
	}
	
	
}
