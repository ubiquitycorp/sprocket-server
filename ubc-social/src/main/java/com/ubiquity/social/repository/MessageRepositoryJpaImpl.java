package com.ubiquity.social.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.niobium.repository.BaseRepositoryJpaImpl;
import com.ubiquity.social.domain.Message;

public class MessageRepositoryJpaImpl extends BaseRepositoryJpaImpl <Long, Message> implements MessageRepository {

	public MessageRepositoryJpaImpl(EntityManager em) {
		super(Message.class, em);
	}

	public MessageRepositoryJpaImpl() {
		super(Message.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Message> findByOwnerId(Long ownerId) {
		assert(ownerId != null);
		Query query = getEntityManager().createQuery("select m from Message m where m.owner.userId = :ownerId");
		query.setParameter("ownerId", ownerId);
		return (List<Message>)query.getResultList();
	}
	
}
