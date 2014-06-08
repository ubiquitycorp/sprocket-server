package com.ubiquity.commerce.repository;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.niobium.repository.BaseRepositoryJpaImpl;
import com.ubiquity.commerce.domain.ItemDoubleOption;
import com.ubiquity.commerce.domain.ItemOption;
import com.ubiquity.commerce.domain.ItemStringOption;


public class ItemOptionRepositoryJpaImpl extends BaseRepositoryJpaImpl<Long, ItemOption>
		implements ItemOptionRepository {

	public ItemOptionRepositoryJpaImpl(EntityManager em) {
		super(ItemOption.class, em);
	}

	public ItemOptionRepositoryJpaImpl() {
		super(ItemOption.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ItemDoubleOption> findByValues(Double[] values) {
		Query query = getEntityManager().createQuery("select ido from ItemDoubleOption ido where ido.value in (:values)");
		query.setParameter("values", Arrays.asList(values));
		return(List<ItemDoubleOption>)query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ItemStringOption> findByValues(String[] values) {
		Query query = getEntityManager().createQuery("select iso from ItemStringOption iso where iso.value in (:values)");
		query.setParameter("values", Arrays.asList(values));
		return(List<ItemStringOption>)query.getResultList();
	}

}
