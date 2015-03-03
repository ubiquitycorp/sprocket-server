package com.ubiquity.sprocket.repository;

import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.repository.UserRepository;
/***
 * 
 * @author peter.tadros
 *
 */
public interface SprocketUserRepository extends UserRepository {

	/***
	 * 
	 * @param identifier
	 * @param appId
	 * @return
	 */
	User searchByIdentifierAndApplicationId(String identifier, Long appId);
}
