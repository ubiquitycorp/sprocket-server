package com.ubiquity.sprocket.repository;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.repository.UserRepositoryJpaImpl;
import com.ubiquity.sprocket.domain.SprocketUser;

/***
 * extends UserRepositoryJpaImpl and adds new methods for SprocketUser
 * @author peter.tadros
 * 
 */
public class SprocketUserRepositoryJpaImpl extends UserRepositoryJpaImpl
		implements SprocketUserRepository {

	@Override
	public User getSprocketUserById(Long userId) {
		assert (userId != null);
		User user = null;
		try {
			Query query = getEntityManager().createQuery(
					"select u from SprocketUser u where u.userId = :userId");
			query.setParameter("userId", userId);
			user = (User) query.getSingleResult();
		} catch (NoResultException e) {
		}
		return user;
	}

	@Override
	public User searchByIdentifierAndApplicationId(String identifier, Long appId) {
		assert (!identifier.isEmpty());
		assert (appId != null);
		Query query = getEntityManager()
				.createQuery(
						"select u from SprocketUser u where u.externalIdentifier = :identifier and u.createdBy.appId = :appId");
		query.setParameter("identifier", identifier);
		query.setParameter("appId", appId);
		User user = null;
		try {
			user = (User) query.getSingleResult();
		} catch (NoResultException e) {
		}
		return user;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<SprocketUser> findAllActiveSprocketUserIds() {
		Query query = getEntityManager()
				.createNativeQuery(
						"select u.user_id ,u.app_id from user u where u.last_login + 1209600000 > :lastLogin order by u.app_id");
		query.setParameter("lastLogin", System.currentTimeMillis());
		return (List<SprocketUser>)query.getResultList();
	}

}
