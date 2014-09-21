package com.ubiquity.sprocket.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.niobium.repository.BaseRepositoryJpaImpl;
import com.ubiquity.content.domain.VideoContent;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.sprocket.domain.RecommendedVideo;

public class RecommendedVideoRepositoryJpaImpl extends BaseRepositoryJpaImpl<Long, RecommendedVideo> implements
RecommendedVideoRepository {

	public RecommendedVideoRepositoryJpaImpl(EntityManager em) {
		super(RecommendedVideo.class, em);
	}

	public RecommendedVideoRepositoryJpaImpl() {
		super(RecommendedVideo.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VideoContent> findRecommendedVideosByGroup(String group) {
		Query query = getEntityManager().createQuery("select rv.videoContent from RecommendedVideo rv where rv.groupIdentifier = :groupIdentifier");
		query.setParameter("groupIdentifier", group);
		return (List<VideoContent>)query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RecommendedVideo> findAllByExternalNetwork(
			ExternalNetwork network) {
		Query query = getEntityManager().createQuery("select rv from RecommendedVideo rv where rv.videoContent.externalNetwork = :externalNetwork");
		query.setParameter("externalNetwork", network);
		return (List<RecommendedVideo>)query.getResultList();
	}
}
