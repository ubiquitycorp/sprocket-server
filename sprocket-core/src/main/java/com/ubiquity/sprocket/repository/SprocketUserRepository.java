package com.ubiquity.sprocket.repository;

import java.math.BigDecimal;
import java.util.List;

import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.repository.UserRepository;
import com.ubiquity.sprocket.domain.SprocketUser;
/***
 * 
 * @author peter.tadros
 *
 */
public interface SprocketUserRepository extends UserRepository {

	/***
	 * 
	 * @param userId
	 * @return
	 */
	User getSprocketUserById(Long userId);
	
	/***
	 * 
	 * @param identifier
	 * @param appId
	 * @return
	 */
	User searchByIdentifierAndApplicationId(String identifier, Long appId);
	
	/***
	 * 
	 * @return
	 */
	List<BigDecimal[]> findAllActiveSprocketUserIds();
	/***
	 * 
	 */
	List<SprocketUser> findSprocketUsersInRange(List<Long> userIds);
}
