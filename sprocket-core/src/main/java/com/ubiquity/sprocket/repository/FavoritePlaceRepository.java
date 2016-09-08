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
	 * selects modified favorite Places by userId and ExternalNetwork
	 * @param userId
	 * @param externalNetwork
	 * @param modifiedSince
	 * @return
	 */
	List<Place> getFavoritePlaceByUserIdAndModifiedSince(Long userId,
			ExternalNetwork externalNetwork, Long modifiedSince);
	/***
	 * selects favorite Places by userId , ExternalNetwork and placeID
	 * @param userId
	 * @param externalNetwork
	 * @param placeId
	 * @return
	 */
	List<Place> getFavoritePlaceByUserIdAndPlaceId(Long userId,
			ExternalNetwork externalNetwork, Long placeId);
	/***
	 * selects modified favorite Places by userId , ExternalNetwork and placeID
	 * @param userId
	 * @param externalNetwork
	 * @param placeId
	 * @param modifiedSince
	 * @return
	 */
	List<Place> getFavoritePlaceByUserIdAndPlaceIdAndModifiedSince(Long userId,
			ExternalNetwork externalNetwork, Long placeId, Long modifiedSince);
	/***
	 * find favorite by bussniessId ,userId ,ExternalNetwork 
	 * @param userId
	 * @param externalNetwork
	 * @param businessId
	 * @return
	 */
	FavoritePlace getFavoritePlaceByUserIdAndBusinessId(Long userId,
			ExternalNetwork externalNetwork, Long businessId);
	
}
