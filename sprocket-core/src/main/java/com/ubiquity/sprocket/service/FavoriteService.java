package com.ubiquity.sprocket.service;

import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.external.domain.ExternalNetwork;
import com.ubiquity.location.domain.Place;
import com.ubiquity.sprocket.repository.FavoritePlaceRepositoryJpaImpl;

public class FavoriteService {
	@SuppressWarnings("unused")
	private Logger log = LoggerFactory.getLogger(getClass());
	/***
	 * Service for favorites 
	 * 
	 * @param configuration
	 */
	public FavoriteService(Configuration configuration) {
		
	}
	
	public List<Place> getFavoritePlacesByOwnerIdandProvider(Long userId , ExternalNetwork externalNetwork) {

		try {
			return new FavoritePlaceRepositoryJpaImpl().getFavoritePlaceByUserId(userId,externalNetwork);
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}
	public List<Place> getFavoritePlacesByOwnerIdandProviderAndPlaceId(Long userId , ExternalNetwork externalNetwork ,Long PlaceId) {

		try {
			return new FavoritePlaceRepositoryJpaImpl().getFavoritePlaceByUserIdAndPlaceId(userId,externalNetwork,PlaceId);
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}
}
