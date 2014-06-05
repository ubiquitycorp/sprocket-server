package com.ubiquity.commerce.repository;

import javax.persistence.EntityManager;

import com.niobium.repository.BaseRepositoryJpaImpl;
import com.ubiquity.commerce.domain.PaymentMethod;

public class PaymentMethodRepositoryJpaImpl extends BaseRepositoryJpaImpl <Long, PaymentMethod> implements PaymentMethodRepository {

	public PaymentMethodRepositoryJpaImpl(EntityManager em) {
		super(PaymentMethod.class, em);
	}

	public PaymentMethodRepositoryJpaImpl() {
		super(PaymentMethod.class);
	}
	
}
