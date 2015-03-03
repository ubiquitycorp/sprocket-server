package com.ubiquity.sprocket.repository;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.repository.UserRepositoryJpaImpl;
/***
 * 
 * @author peter.tadros
 *
 */
public class SprocketUserRepositoryJpaImpl extends UserRepositoryJpaImpl implements SprocketUserRepository {

	@Override
	public User searchByIdentifierAndApplicationId(String identifier,
			Long appId) {
		assert(!identifier.isEmpty());
		assert(appId != null);
		Query query = getEntityManager().createQuery("select u from SprocketUser u where u.externalIdentifier = :identifier and u.createdBy.appId = :appId");
		query.setParameter("identifier", identifier);
		query.setParameter("appId", appId);
		User user = null;
		try {
			user = (User)query.getSingleResult();
		} catch (NoResultException e) {
		}
		return user;
	}
	
}
