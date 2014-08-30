package com.ubiquity.sprocket.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.niobium.repository.BaseRepositoryJpaImpl;
import com.ubiquity.sprocket.domain.EngagedVideo;

public class EngagedVideoRepositoryJpaImpl extends BaseRepositoryJpaImpl<Long, EngagedVideo> implements
EngagedVideoRepository {

	public EngagedVideoRepositoryJpaImpl(EntityManager em) {
		super(EngagedVideo.class, em);
	}

	public EngagedVideoRepositoryJpaImpl() {
		super(EngagedVideo.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EngagedVideo> findMeanByGroup(String group) {
		Query query = getEntityManager().createQuery("select ei from EngagedItem ei where ei.user in (select gm.user from GroupMembership gm where gm.groupIdentifier = :group) group by ei.videoContent.videoContentId order by count(*) desc");
		query.setParameter("group", group);
		return (List<EngagedVideo>)query.getResultList();
	}
}
