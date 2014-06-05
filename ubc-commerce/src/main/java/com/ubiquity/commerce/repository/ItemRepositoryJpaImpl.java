package com.ubiquity.commerce.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.niobium.repository.BaseRepositoryJpaImpl;
import com.ubiquity.commerce.domain.Item;

public class ItemRepositoryJpaImpl extends BaseRepositoryJpaImpl<Long, Item>
		implements ItemRepository {

	public ItemRepositoryJpaImpl(EntityManager em) {
		super(Item.class, em);
	}

	public ItemRepositoryJpaImpl() {
		super(Item.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Item> findAll() {
		Query query = getEntityManager().createQuery(
				"select i from Item i");
		return (List<Item>) query.getResultList();
	}

}
