package com.ubiquity.sprocket.repository;

import java.util.List;

import com.niobium.repository.Repository;
import com.ubiquity.integration.domain.ExternalNetwork;
import com.ubiquity.location.domain.Place;
import com.ubiquity.sprocket.domain.FavoritePlace;

public interface FavoritePlaceRepository extends Repository <Long, FavoritePlace>  {
	/***
	 * selects favorite Places by userId and ExternalNetwork
	 * @param userId
	 * @return
	 */
	List<Place> getFavoritePlaceByUserId(Long userId ,ExternalNetwork externalNetwork);
	/***
	 * selects favorite Places by userId , ExternalNetwork and placeID
	 * @param userId
	 * @param externalNetwork
	 * @param placeId
	 * @return
	 */
	List<Place> getFavoritePlaceByUserIdAndPlaceId(Long userId,
			ExternalNetwork externalNetwork, Long placeId);

}
