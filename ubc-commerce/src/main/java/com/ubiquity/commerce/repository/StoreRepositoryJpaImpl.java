package com.ubiquity.commerce.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.niobium.repository.BaseRepositoryJpaImpl;
import com.ubiquity.commerce.domain.Store;
/***
 * 
 * @author peter.tadros
 *
 */
public class StoreRepositoryJpaImpl extends BaseRepositoryJpaImpl <Long, Store> implements StoreRepository {

	public StoreRepositoryJpaImpl(EntityManager em) {
		super(Store.class, em);
	}

	public StoreRepositoryJpaImpl() {
		super(Store.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Store> findAll() {
		Query query = getEntityManager().createQuery("select s from Store s");		
		return query.getResultList();
	}
	
}
