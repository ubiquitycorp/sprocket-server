package com.ubiquity.sprocket.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import com.niobium.repository.BaseRepositoryJpaImpl;
import com.ubiquity.integration.domain.ExternalNetwork;
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
		Query query = getEntityManager().createQuery("select fp.place from FavoritePlace fp where fp.user.userId = :userId and fp.place.network =:network");
		query.setParameter("userId", userId);
		query.setParameter("network", ExternalNetwork.ordinalOrDefault(externalNetwork));
		return (List<Place>)query.getResultList();
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<Place> getFavoritePlaceByUserIdAndPlaceId(Long userId,
			ExternalNetwork externalNetwork, Long placeId) {
		Query query = getEntityManager().createQuery("select fp.place from FavoritePlace fp inner join fp.place.parent as neighborhood inner join neighborhood.parent as city where fp.user.userId = :userId and fp.place.network =:network and (neighborhood.placeId = :placeId or city.placeId = :placeId )");
		query.setParameter("userId", userId);
		query.setParameter("placeId", placeId);
		query.setParameter("network", ExternalNetwork.ordinalOrDefault(externalNetwork));
		return (List<Place>)query.getResultList();
	}
	@SuppressWarnings("unchecked")
	@Override
	public FavoritePlace getFavoritePlaceByUserIdAndBusinessId(Long userId,
			ExternalNetwork externalNetwork, Long businessId) {
		Query query = getEntityManager().createQuery("select fp from FavoritePlace fp where fp.user.userId = :userId and fp.place.network =:network and fp.place.placeId = :placeId ");
		query.setParameter("userId", userId);
		query.setParameter("placeId", businessId);
		query.setParameter("network", ExternalNetwork.ordinalOrDefault(externalNetwork));
		try{
			return (FavoritePlace)query.getSingleResult();
		}
		catch(NoResultException ex)
		{
			return null;
		}
		catch(NonUniqueResultException ex)
		{
			return ((List<FavoritePlace>)query.getResultList()).get(0);
		}
	}
}
