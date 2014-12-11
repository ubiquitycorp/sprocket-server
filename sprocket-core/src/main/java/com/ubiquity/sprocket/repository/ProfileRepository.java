package com.ubiquity.sprocket.repository;

import java.util.List;

import com.niobium.repository.Repository;
import com.ubiquity.sprocket.domain.Profile;
import com.ubiquity.sprocket.domain.ProfilePK;

public interface ProfileRepository extends Repository <ProfilePK, Profile>  {
	void addToSearchHistory(ProfilePK pk, String searchTerm);
	
	List<String> findMostSearchedOn(ProfilePK pk, Integer limit);
}
