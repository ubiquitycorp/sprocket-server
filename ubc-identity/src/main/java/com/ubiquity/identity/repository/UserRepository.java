package com.ubiquity.identity.repository;

import java.util.List;

import com.niobium.repository.Repository;
import com.ubiquity.identity.domain.User;

/***
 * 
 * Interface exposing CRUD methods for the user entity
 * 
 * @author chris
 *
 */
public interface UserRepository extends Repository <Long, User> {
	
	/***
	 * Returns a count of all users in the system
	 * @return
	 */
	int countAllUsers();
	
	/***
	 * Deletes all users.
	 * 
	 * @return true if users were deleted, false if no users were deleted
	 */
	boolean deleteAll();
	
	/***
	 * Returns all users in the system
	 * @return
	 */
	List<User> findAll();
	
	/***
	 * Returns a user by the associated identity pk
	 * 
	 * @param identityId
	 * 
	 * @return A user or null if there is no user by that identity
	 */
	User getByIdentityId(Long identityId);
}

