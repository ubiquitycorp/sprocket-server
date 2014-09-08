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
		boolean deleted = query.executeUpdate() > 0 ? true : false;
		return deleted;
		
	}

	@Override
	public boolean deleteWithNoNetwork() {
		Query query = getEntityManager().createQuery("delete from GroupMembership gm where gm.externalNetwork is null");
		boolean deleted = query.executeUpdate() > 0 ? true : false;
		return deleted;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GroupMembership> findAllByUserId(Long userId) {
		Query query = getEntityManager().createQuery("select gm from GroupMembership gm where gm.user.userId = :userId");
		query.setParameter("userId", userId);
		return (List<GroupMembership>)query.getResultList();
	}

	@Override
	public boolean deleteByExternalNetworkAndUserId(ExternalNetwork network,
			Long userId) {
		Query query = getEntityManager().createQuery("delete from GroupMembership gm where gm.externalNetwork = :externalNetwork and gm.user.userId = :userId");
		query.setParameter("externalNetwork", network);
		query.setParameter("userId", userId);
		boolean deleted = query.executeUpdate() > 0 ? true : false;
		return deleted;
	}

	@Override
	public boolean deleteByUserId(Long userId) {
		Query query = getEntityManager().createQuery("delete from GroupMembership gm where gm.user.userId = :userId");
		query.setParameter("userId", userId);
		boolean deleted = query.executeUpdate() > 0 ? true : false;
		return deleted;
	}
}
