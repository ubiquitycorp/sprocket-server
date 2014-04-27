package com.ubiquity.identity.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.niobium.repository.BaseRepositoryJpaImpl;
import com.ubiquity.identity.domain.Identity;
import com.ubiquity.identity.domain.User;

public class UserRepositoryJpaImpl extends BaseRepositoryJpaImpl <Long, User> implements UserRepository {

	public UserRepositoryJpaImpl(EntityManager em) {
		super(User.class, em);
	}

	public UserRepositoryJpaImpl() {
		super(User.class);
	}

	@Override
	public int countAllUsers() {
		Query query = getEntityManager().createQuery("select count(u.userId) from User u");
		Number count = (Number)query.getSingleResult();
		return count.intValue();
	}

	@Override
	public boolean deleteAll() {
		Query query = getEntityManager().createQuery("delete from User u");
		getEntityManager().getTransaction().begin();
		boolean deleted = query.executeUpdate() > 0 ? true : false;
		getEntityManager().getTransaction().commit();
		return deleted;
	}

	@SuppressWarnings("unchecked")
	public List<User> findAll() {
		Query query = getEntityManager().createQuery("select u from User u");		
		return query.getResultList();
	}

	@Override
	public User getByIdentityId(Long identityId) {
		Query query = getEntityManager().createQuery("select i from Identity i where i.identityId = :identityId");
		query.setParameter("identityId", identityId);
		Identity identity;
		try {
			identity = (Identity)query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
		return identity.getUser();

	}

	@Override
	public User searchUserByUsername(String username) {
		Query query = getEntityManager().createQuery("select ni from NativeIdentity ni where ni.username = :username");
		query.setParameter("username", username);
		Identity identity;
		try {
			identity = (Identity)query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
		return identity.getUser();
	}

	@Override
	public User searchUserByUsernameAndPassword(String username, String password) {
		Query query = getEntityManager().createQuery("select ni from NativeIdentity ni where ni.username = :username and ni.password = :password");
		query.setParameter("username", username);
		query.setParameter("password", password);
		Identity identity;
		try {
			identity = (Identity)query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
		return identity.getUser();
	}
	
}
