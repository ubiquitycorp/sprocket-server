package com.ubiquity.social.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.niobium.repository.BaseRepositoryJpaImpl;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.SocialProvider;

public class ContactRepositoryJpaImpl extends BaseRepositoryJpaImpl <Long, Contact> implements ContactRepository {

	public ContactRepositoryJpaImpl(EntityManager em) {
		super(Contact.class, em);
	}

	public ContactRepositoryJpaImpl() {
		super(Contact.class);
	}

	@Override
	public int countAllContactsByOwnerIdAndSocialIdentityProvider(Long ownerId,
			SocialProvider socialProviderType) {
		Query query = getEntityManager().createQuery("select count(c.contactId) from Contact c where c.owner.userId = :ownerId and c.socialIdentity.socialProviderType = :socialProviderType");
		query.setParameter("ownerId", ownerId);
		query.setParameter("socialProviderType", socialProviderType);
		Number count = (Number)query.getSingleResult();
		return count.intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Contact> findByOwnerIdAndSocialIdentityProvider(Long ownerId,
			SocialProvider type) {
		Query query = getEntityManager().createQuery("select c from Contact c where c.owner.userId = :ownerId and c.socialIdentity.socialProviderType = :socialProviderType");
		query.setParameter("ownerId", ownerId);
		query.setParameter("socialProviderType", type);
		return (List<Contact>)query.getResultList();	
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Contact> findByOwnerId(Long ownerId, Boolean active) {
		assert(ownerId != null);
		Query query = getEntityManager().createQuery("select c from Contact c where c.owner.userId = :ownerId and c.socialIdentity.isActive = :active");
		query.setParameter("ownerId", ownerId);
		query.setParameter("active", active);
		return (List<Contact>)query.getResultList();
	}


	@Override
	public Contact getBySocialIdentityId(Long identityId) {
		assert(identityId != null);
		Query query = getEntityManager().createQuery("select c from Contact c where c.socialIdentity.identityId = :identityId");
		query.setParameter("identityId", identityId);
		 return (Contact)query.getSingleResult();
	}
	
}
