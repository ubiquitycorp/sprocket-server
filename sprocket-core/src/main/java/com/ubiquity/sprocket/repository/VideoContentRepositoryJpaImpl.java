package com.ubiquity.sprocket.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.niobium.repository.BaseRepositoryJpaImpl;
import com.ubiquity.sprocket.domain.ContentNetwork;
import com.ubiquity.sprocket.domain.VideoContent;

public class VideoContentRepositoryJpaImpl extends BaseRepositoryJpaImpl <Long, VideoContent> implements VideoContentRepository {

	public VideoContentRepositoryJpaImpl(EntityManager em) {
		super(VideoContent.class, em);
	}

	public VideoContentRepositoryJpaImpl() {
		super(VideoContent.class);
	}

	@SuppressWarnings("unchecked")
	public List<VideoContent> findByOwnerId(Long ownerId) {
		assert(ownerId != null);
		Query query = getEntityManager().createQuery("select vc from VideoContent vc where vc.owner.userId = :ownerId");
		query.setParameter("ownerId", ownerId);
		return (List<VideoContent>)query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<VideoContent> findByOwnerIdAndItemKey(Long ownerId,
			String itemKey) {
		assert(ownerId != null);
		assert(itemKey != null);
		Query query = getEntityManager().createQuery("select vc from VideoContent vc where vc.owner.userId = :ownerId and vc.video.itemKey = :itemKey");
		query.setParameter("ownerId", ownerId);
		query.setParameter("itemKey", itemKey);
		return (List<VideoContent>)query.getResultList();
	}

	public int deleteWithoutIds(Long ownerId, List<Long> videoContentIds) {
		assert(ownerId != null);
		assert(videoContentIds != null);
		assert(!videoContentIds.isEmpty());
		Query query = getEntityManager().createQuery("delete from VideoContent vc where vc.owner.userId = :ownerId and vc.videoContentId not in :videoContentIds");
		query.setParameter("ownerId", ownerId);
		query.setParameter("videoContentIds", videoContentIds);
		return query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	public List<VideoContent> findByOwnerIdAndContentNetwork(Long ownerId,
			ContentNetwork contentNetwork) {
		assert(ownerId != null);
		assert(contentNetwork != null);
		Query query = getEntityManager().createQuery("select vc from VideoContent vc where vc.owner.userId = :ownerId and vc.contentNetwork = :contentNetwork");
		query.setParameter("ownerId", ownerId);
		query.setParameter("contentNetwork", contentNetwork);
		return (List<VideoContent>)query.getResultList();
	}
	
}
