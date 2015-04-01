package com.ubiquity.sprocket.repository;

import java.util.List;

import com.niobium.repository.Repository;
import com.ubiquity.sprocket.domain.Profile;
import com.ubiquity.sprocket.domain.ProfilePK;

/**
 * Interface for CRUD operations on a profile entity
 * 
 * @author chris
 *
 */
public interface ProfileRepository extends Repository <ProfilePK, Profile>  {
	
	/**
	 * Add a search term to the search history for a profile
	 * 
	 * @param pk
	 * @param searchTerm
	 */
	void addToSearchHistory(ProfilePK pk, String searchTerm);
	
	/**
	 * Returns the top n most searched terms for a profile
	 * 
	 * @param pk
	 * @param limit
	 * 
	 * @return top n most searched on terms
	 */
	List<String> findMostSearchedOn(ProfilePK pk, Integer limit);
}
