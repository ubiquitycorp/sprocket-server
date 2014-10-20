package com.ubiquity.sprocket.service;

import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.CollectionVariant;
import com.niobium.repository.cache.DataCacheKeys;
import com.niobium.repository.cache.UserDataModificationCache;
import com.niobium.repository.cache.UserDataModificationCacheRedisImpl;
import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.integration.repository.cache.CacheKeys;
import com.ubiquity.location.domain.Place;
import com.ubiquity.sprocket.domain.FavoritePlace;
import com.ubiquity.sprocket.repository.FavoritePlaceRepository;
import com.ubiquity.sprocket.repository.FavoritePlaceRepositoryJpaImpl;

public class FavoriteService {
	@SuppressWarnings("unused")
	private Logger log = LoggerFactory.getLogger(getClass());
	/***
	 * Service for favorites
	 * 
	 * @param configuration
	 */
	private UserDataModificationCache dataModificationCache;

	public FavoriteService(Configuration configuration) {
		dataModificationCache = new UserDataModificationCacheRedisImpl(
				configuration
						.getInt(DataCacheKeys.Databases.ENDPOINT_MODIFICATION_DATABASE_USER));

	}

	public CollectionVariant<Place> getFavoritePlacesByOwnerIdandProvider(
			Long userId, ExternalNetwork externalNetwork, Long ifModifiedSince,
			Boolean delta) {
		String key = CacheKeys.generateCacheKeyForExternalNetworkAndPlaceID(
				CacheKeys.UserProperties.FAVORITES, externalNetwork, (long) 0);
		Long lastModified = dataModificationCache.getLastModified(userId, key,
				ifModifiedSince);

		// If there is no cache entry, there is no data
		if (lastModified == null) {
			return null;
		}
		try {
			FavoritePlaceRepository favoritePlaceRepository = new FavoritePlaceRepositoryJpaImpl();
			List<Place> places;
			if (delta == null || !delta)
				places = favoritePlaceRepository.getFavoritePlaceByUserId(
						userId, externalNetwork);
			else
				places = favoritePlaceRepository
						.getFavoritePlaceByUserIdAndModifiedSince(userId,
								externalNetwork, ifModifiedSince);
			return new CollectionVariant<Place>(places, lastModified);
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}

	public CollectionVariant<Place> getFavoritePlacesByOwnerIdandProviderAndPlaceId(
			Long userId, ExternalNetwork externalNetwork, Long placeId,
			Long ifModifiedSince, Boolean delta) {
		String key = CacheKeys.generateCacheKeyForExternalNetworkAndPlaceID(
				CacheKeys.UserProperties.FAVORITES, externalNetwork, placeId);
		Long lastModified = dataModificationCache.getLastModified(userId, key,
				ifModifiedSince);

		// If there is no cache entry, there is no data
		if (lastModified == null) {
			return null;
		}
		try {

			FavoritePlaceRepository favoritePlaceRepository = new FavoritePlaceRepositoryJpaImpl();
			List<Place> places;
			if (delta == null || !delta)
				places = favoritePlaceRepository
						.getFavoritePlaceByUserIdAndPlaceId(userId,
								externalNetwork, placeId);
			else
				places = favoritePlaceRepository
						.getFavoritePlaceByUserIdAndPlaceIdAndModifiedSince(
								userId, externalNetwork, placeId, ifModifiedSince);
			return new CollectionVariant<Place>(places, lastModified);
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}

	public FavoritePlace findOrCreate(FavoritePlace favPlace) {
		FavoritePlaceRepository favoritePlaceRepositoryJpaImpl = new FavoritePlaceRepositoryJpaImpl();
		FavoritePlace temp = favoritePlaceRepositoryJpaImpl
				.getFavoritePlaceByUserIdAndBusinessId(favPlace.getUser()
						.getUserId(), favPlace.getPlace().getExternalNetwork(),
						favPlace.getPlace().getPlaceId());
		if (temp != null) {
			EntityManagerSupport.beginTransaction();
			favoritePlaceRepositoryJpaImpl.create(favPlace);
			EntityManagerSupport.commit();
			return favPlace;
		}
		return temp;
	}
	/***
	 * set favorite place cache
	 * @param userId
	 * @param network
	 * @param placeId
	 * @param ifModifiedSince
	 */
	public void setFavoritePlaceCache(Long userId ,ExternalNetwork network, Long neighburhoodId ){
		Long ifModifiedSince = System.currentTimeMillis();
		String key = CacheKeys.generateCacheKeyForExternalNetworkAndPlaceID(
				CacheKeys.UserProperties.FAVORITES, network, neighburhoodId);
		dataModificationCache.put(userId, key,ifModifiedSince);
		
		key = CacheKeys.generateCacheKeyForExternalNetworkAndPlaceID(
				CacheKeys.UserProperties.FAVORITES, network, (long) 0);
		dataModificationCache.put(userId, key, ifModifiedSince);
	}
}
