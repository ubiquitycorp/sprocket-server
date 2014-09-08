package com.ubiquity.sprocket.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.niobium.repository.BaseRepositoryJpaImpl;
import com.ubiquity.sprocket.domain.EngagedDocument;

public class EngagedDocumentRepositoryJpaImpl extends BaseRepositoryJpaImpl<Long, EngagedDocument> implements
EngagedDocumentRepository {

	public EngagedDocumentRepositoryJpaImpl(EntityManager em) {
		super(EngagedDocument.class, em);
	}

	public EngagedDocumentRepositoryJpaImpl() {
		super(EngagedDocument.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EngagedDocument> findMeanByGroup(String group, Integer limit) {
		Query query = getEntityManager().createQuery("select ei from EngagedItem ei where ei.user in (select gm.user from GroupMembership gm where gm.groupIdentifier = :group) group by ei.documentDataType, ei.activity.activityId, ei.videoContent.videoContentId order by count(*) desc");
		query.setParameter("group", group);
		query.setMaxResults(limit);
		return (List<EngagedDocument>)query.getResultList();
	}
}
