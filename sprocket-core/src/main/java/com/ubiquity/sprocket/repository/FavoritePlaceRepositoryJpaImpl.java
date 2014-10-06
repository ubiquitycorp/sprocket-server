package com.ubiquity.sprocket.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.niobium.repository.BaseRepositoryJpaImpl;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.location.domain.Place;
import com.ubiquity.sprocket.domain.FavoritePlace;

public class FavoritePlaceRepositoryJpaImpl  extends BaseRepositoryJpaImpl<Long, FavoritePlace> implements
FavoritePlaceRepository {

	public FavoritePlaceRepositoryJpaImpl(EntityManager em) {
		super(FavoritePlace.class, em);
	}
	public FavoritePlaceRepositoryJpaImpl() {
		super(FavoritePlace.class);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Place> getFavoritePlaceByUserId(Long userId,ExternalNetwork externalNetwork) {
		Query query = getEntityManager().createQuery("select fp.Place from FavoritePlace fp where fp.user.userId = :userId and fp.Place.network =:network");
		query.setParameter("userId", userId);
		query.setParameter("network", externalNetwork);
		return (List<Place>)query.getResultList();
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<Place> getFavoritePlaceByUserIdAndPlaceId(Long userId,
			ExternalNetwork externalNetwork, Long placeId) {
		Query query = getEntityManager().createQuery("select fp.Place from FavoritePlace fp inner join fp.Place.parent as neighborhood inner join neighborhood.parnet as city where fp.user.userId = :userId and fp.Place.network =:network and (neighborhood.placeId = :placeId or city.placeId = :placeId )");
		query.setParameter("userId", userId);
		query.setParameter("placeId", placeId);
		query.setParameter("network", externalNetwork);
		return (List<Place>)query.getResultList();
	}
}
