package com.ubiquity.sprocket.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.niobium.repository.BaseRepositoryJpaImpl;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.sprocket.domain.GroupMembership;

public class GroupMembershipRepositoryJpaImpl extends BaseRepositoryJpaImpl<Long, GroupMembership> implements
GroupMembershipRepository {

	public GroupMembershipRepositoryJpaImpl(EntityManager em) {
		super(GroupMembership.class, em);
	}

	public GroupMembershipRepositoryJpaImpl() {
		super(GroupMembership.class);
	}

	@Override
	public boolean deleteByExternalNetwork(ExternalNetwork externalNetwork) {
		Query query = getEntityManager().createQuery("delete from GroupMembership gm where gm.externalNetwork = :externalNetwork");
		query.setParameter("externalNetwork", externalNetwork);
		getEntityManager().getTransaction().begin();
		boolean deleted = query.executeUpdate() > 0 ? true : false;
		getEntityManager().getTransaction().commit();
		return deleted;
		
	}

	@Override
	public boolean deleteWithNoNetwork() {
		Query query = getEntityManager().createQuery("delete from GroupMembership gm where gm.externalNetwork is null");
		getEntityManager().getTransaction().begin();
		boolean deleted = query.executeUpdate() > 0 ? true : false;
		getEntityManager().getTransaction().commit();
		return deleted;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GroupMembership> findAllByUserId(Long userId) {
		Query query = getEntityManager().createQuery("select gm from GroupMembership gm where gm.user.userId = :userId");
		query.setParameter("userId", userId);
		return (List<GroupMembership>)query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> findGroupIdentifiersByExternalNetwork(
			ExternalNetwork externalNetwork) {
		Query query = getEntityManager().createQuery("select distinct gm.groupIdentifier from GroupMembership gm where gm.externalNetwork = :externalNetwork");
		query.setParameter("externalNetwork", externalNetwork);
		return (List<String>)query.getResultList();
	}
}
