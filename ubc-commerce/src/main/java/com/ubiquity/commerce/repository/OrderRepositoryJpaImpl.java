package com.ubiquity.commerce.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.niobium.repository.BaseRepositoryJpaImpl;
import com.ubiquity.commerce.domain.Order;

public class OrderRepositoryJpaImpl extends BaseRepositoryJpaImpl<Long, Order>
		implements OrderRepository {

	public OrderRepositoryJpaImpl(EntityManager em) {
		super(Order.class, em);
	}

	public OrderRepositoryJpaImpl() {
		super(Order.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Order> findSentOrdersByOwnerId(Long ownerId) {
		Query query = getEntityManager().createQuery("select o from Order o where o.owner.userId = :ownerId");
		query.setParameter("ownerId", ownerId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Order> findReceivedOrdersByUserId(Long userId) {
		Query query = getEntityManager().createQuery("select o from Order o, SocialIdentity s where o.contact.socialProviderIdentifier = s.identifier and s.user.userId = :userId");
		query.setParameter("userId", userId);
		return query.getResultList();
	}

}
